package ua.edu.chdtu.deanoffice.service;

import com.google.common.util.concurrent.AtomicDouble;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.entity.KnowledgeControl;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;
import ua.edu.chdtu.deanoffice.util.LanguageUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static ua.edu.chdtu.deanoffice.util.DocumentUtil.cleanFileName;
import static ua.edu.chdtu.deanoffice.util.DocumentUtil.getFileCreationDateAndTime;
import static ua.edu.chdtu.deanoffice.util.DocumentUtil.getJavaTempDirectory;

@Service
public class GraduateGroupCoursesService {

    @Value(value = "classpath:fonts/arial/arial.ttf")
    private Resource ttf;
    private static BaseColor headerColor = new BaseColor(238, 238, 238);
    private static final String FILE_NAME_PREFIX = "PREDMETY_";

    public File formDocument(List<CourseForGroup> courseForGroups) throws DocumentException, IOException {
        BaseFont baseFont = BaseFont.createFont(ttf.getURI().getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font font = new Font(baseFont);
        PdfPCell numberOfRowHeaderCell = createHeaderCell("№", font);
        PdfPCell semesterHeaderCell = createHeaderCell("Сем.", font);
        PdfPCell hoursHeaderCell = createHeaderCell("Години", font);
        PdfPCell creditsHeaderCell = createHeaderCell("Кредити", font);

        String groupName = courseForGroups.get(0).getStudentGroup().getName();
        Map<KnowledgeControl, List<CourseForGroup>> tableMap = preprocessedData(courseForGroups);
        Document document = new Document(PageSize.A4);
        File file = getTempFile(groupName);
        PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();
        PdfPTable table = createPdfPTable();

        addHeader(groupName, table, font);

        AtomicInteger hoursSum = new AtomicInteger(0);
        AtomicDouble creditsSum = new AtomicDouble(0);

        tableMap.forEach((knowledgeControl, courseForGroupList) -> {
            addSubtitle(table, knowledgeControl, numberOfRowHeaderCell, semesterHeaderCell, hoursHeaderCell, creditsHeaderCell, font);
            AtomicInteger index = new AtomicInteger(1);
            courseForGroupList.forEach(courseForGroup -> {
                Course currentCourse = courseForGroup.getCourse();
                addDataRow(table, index, currentCourse, font);

                hoursSum.addAndGet(currentCourse.getHours());
                creditsSum.addAndGet(currentCourse.getCredits().doubleValue());

                index.getAndIncrement();
            });
            index.set(1);
        });
        addFooter(table, hoursSum, creditsSum, font);
        document.add(table);
        document.close();
        return file;
    }

    private Map<KnowledgeControl, List<CourseForGroup>> preprocessedData(List<CourseForGroup> courseForGroups) {
        Map<KnowledgeControl, List<CourseForGroup>> tableMap = new TreeMap<>(Comparator.comparingInt(BaseEntity::getId));
        courseForGroups.forEach(courseForGroup -> tableMap
                .computeIfAbsent(courseForGroup.getCourse().getKnowledgeControl(), knowledgeControl -> new ArrayList<>())
                .add(courseForGroup));
        tableMap.forEach((knowledgeControl, courseForGroupList) -> courseForGroupList
                .sort(Comparator.comparingInt((CourseForGroup o) -> o.getCourse().getSemester())
                    .thenComparing(o -> o.getCourse().getCourseName().getName())
                ));
        return tableMap;
    }

    private File getTempFile(String groupName) {
        String filePath = getJavaTempDirectory()
                + "/" + FILE_NAME_PREFIX
                + cleanFileName(LanguageUtil.transliterate(groupName))
                + getFileCreationDateAndTime() + ".pdf";
        return new File(filePath);
    }

    private PdfPTable createPdfPTable() throws DocumentException {
        PdfPTable table = new PdfPTable(5);
        table.setWidths(new int[]{1, 1, 8, 2, 2});
        table.setWidthPercentage(100);
        return table;
    }

    private void addDataRow(PdfPTable table, AtomicInteger index, Course currentCourse, Font font) {
        table.addCell(createCellWithAlignCenter(index.toString(), font));
        table.addCell(createCellWithAlignCenter(currentCourse.getSemester().toString(), font));
        PdfPCell courseNameDataCell = new PdfPCell(new Phrase(currentCourse.getCourseName().getName(), font));
        table.addCell(courseNameDataCell);
        table.addCell(createCellWithAlignCenter(currentCourse.getHours().toString(), font));
        table.addCell(createCellWithAlignCenter(currentCourse.getCredits().toPlainString(), font));
    }

    private void addHeader(String groupName, PdfPTable table, Font font) {
        PdfPCell headerCell = new PdfPCell(new Phrase("Група: " + groupName, font));
        headerCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        headerCell.setColspan(5);
        table.addCell(headerCell);
    }

    private void addFooter(PdfPTable table, AtomicInteger hoursSum, AtomicDouble creditsSum, Font font) {
        PdfPCell sumCell = new PdfPCell(new Phrase("Всього: ", font));
        sumCell.setColspan(3);
        table.addCell(sumCell);
        table.addCell(createCellWithAlignCenter(hoursSum.toString(), font));
        table.addCell(createCellWithAlignCenter(creditsSum.toString(), font));
    }

    private void addSubtitle(PdfPTable table, KnowledgeControl knowledgeControl, PdfPCell numberOfRowHeaderCell, PdfPCell semesterHeaderCell, PdfPCell hoursHeaderCell, PdfPCell creditsHeaderCell, Font font) {
        table.addCell(numberOfRowHeaderCell);
        table.addCell(semesterHeaderCell);
        PdfPCell knowledgeControlHeader = new PdfPCell(new Phrase(knowledgeControl.getName(), font));
        knowledgeControlHeader.setBackgroundColor(headerColor);
        table.addCell(knowledgeControlHeader);
        table.addCell(hoursHeaderCell);
        table.addCell(creditsHeaderCell);
    }

    private PdfPCell createCellWithAlignCenter(String text, Font font) {
        PdfPCell semesterDataCell = new PdfPCell(new Phrase(text, font));
        semesterDataCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        return semesterDataCell;
    }

    private PdfPCell createHeaderCell(String text, Font font) {
        PdfPCell numberOfRowHeaderCell = new PdfPCell(new Phrase(text, font));
        numberOfRowHeaderCell.setBackgroundColor(headerColor);
        numberOfRowHeaderCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        return numberOfRowHeaderCell;
    }

}
