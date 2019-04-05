package ua.edu.chdtu.deanoffice.service.document.report.graduates.group;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Specialization;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ua.edu.chdtu.deanoffice.util.DocumentUtil.getFileCreationDateAndTime;

@Service
public class GraduatesGroupReportService {
    private StudentGroupService studentGroupService;
    @Value(value = "classpath:fonts/arial/arial.ttf")
    private Resource ttf;

    public GraduatesGroupReportService(StudentGroupService studentGroupService) {
        this.studentGroupService = studentGroupService;
    }

    public File createGraduatesReportForGroupPdf(int groupId) throws IOException, DocumentException {
        StudentGroup group = studentGroupService.getById(groupId);
        Document document = new Document(PageSize.A4, 28f, 28f, 28f, 28f);
//        String filePath = getJavaTempDirectory() + "/" + "vidomist_vypusknykiv_" + group.getName() +"_" + getFileCreationDateAndTime() + ".pdf";
        String filePath = "D:/deanoffice/deanoffice-backend" + "/" + "vidomist_vypusknykiv_" + group.getName() + "_" + getFileCreationDateAndTime() + ".pdf";
        File file = new File(filePath);
        PdfWriter.getInstance(document, new FileOutputStream(file));
        try {
            document.open();
            BaseFont baseFont = BaseFont.createFont(ttf.getURI().getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font basefont = new Font(baseFont, 14);
            document.add(getCenterAlignedParagraph("ЧЕРКАСЬКИЙ ДЕРЖАВНИЙ ТЕХНОЛОГІЧНИЙ УНІВЕРСИТЕТ", basefont, 0));
            document.add(getCenterAlignedParagraph("ФАКУЛЬТЕТ ЕЛЕКТРОННИХ ТЕХНОЛОГІЙ І РОБОТОТЕХНІКИ", basefont, 50));
            document.add(getCenterAlignedParagraph("ВІДОМІСТЬ", new Font(baseFont, 14, Font.BOLD), 0));
            document.add(getCenterAlignedParagraph("навчальних досягнень здобувачів вищої освіти,", basefont, 0));
            document.add(getCenterAlignedParagraph("які виконали усі вимоги навчального плану освітньої програми", basefont, 0));
            document.add(getCenterAlignedParagraph("за період навчання", basefont, 30));
            document.add(createGroupAttributeTable(basefont, createGroupAttributeBean(group)));
            document.add(createMainTable(new Font(baseFont, 10), group.getActiveStudents()));
        } finally {
            if (document != null)
                document.close();
        }
        return file;
    }

    private Paragraph getCenterAlignedParagraph(String text, Font font, int spacingAfter) {
        Paragraph element = new Paragraph(text, font);
        element.setAlignment(Element.ALIGN_CENTER);
        element.setSpacingAfter(spacingAfter);
        return element;
    }

    private PdfPTable createGroupAttributeTable(Font font, GroupAttributeBean bean) throws DocumentException {
        PdfPTable mainTable = new PdfPTable(2);
        mainTable.setTotalWidth(new float[]{150, 250});
        mainTable.setLockedWidth(true);
        ArrayList<PdfPCell> cells = new ArrayList<>();
        cells.add(new PdfPCell(new Paragraph("   Освітній рівень:", font)));
        cells.add(new PdfPCell(new Paragraph(bean.getDegree(), font)));
        cells.add(new PdfPCell(new Paragraph(new Paragraph("   Спеціальність:", font))));
        cells.add(new PdfPCell(new Paragraph(new Paragraph(bean.getSpeciality(), font))));
        cells.add(new PdfPCell(new Paragraph(new Paragraph("   Освітня програма:", font))));
        cells.add(new PdfPCell(new Paragraph(new Paragraph(bean.getSpecialization(), font))));
        cells.add(new PdfPCell(new Paragraph(new Paragraph("   Академічна група:", font))));
        cells.add(new PdfPCell(new Paragraph(new Paragraph(bean.getAcademicGroup(), font))));
        for (PdfPCell cell : cells) {
            cell.setBorder(0);
            mainTable.addCell(cell);
        }
        mainTable.setHorizontalAlignment(Element.ALIGN_LEFT);
        return mainTable;
    }

    private GroupAttributeBean createGroupAttributeBean(StudentGroup group) {
        GroupAttributeBean bean = new GroupAttributeBean();
        Specialization specialization = group.getSpecialization();
        bean.setSpecialization(specialization.getName());
        bean.setDegree(specialization.getDegree().getName());
        bean.setSpeciality(specialization.getSpeciality().getName());
        bean.setAcademicGroup(group.getName());
        return bean;
    }

    private PdfPTable createMainTable(Font font, List<Student> list) throws DocumentException {
        PdfPTable mainTable = new PdfPTable(4);
        mainTable.setSpacingBefore(10);
        mainTable.setWidths(new float[]{1f, 10f, 2f, 10f});
        mainTable.setWidthPercentage(100);
        mainTable.addCell(createCell("№ з/п", font, 20));
        mainTable.addCell(createCell("ПІБ здобувача", font, 30));
        mainTable.addCell(createCell("№ залікової книжки", font, 20));
        mainTable.addCell(createAchievementsTable(font));


        return mainTable;
    }

    private PdfPCell createCell(String text, Font font, int paddingTop) {
        PdfPCell cell = new PdfPCell();
        Paragraph paragraph = new Paragraph(text, font);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(paragraph);
        cell.setPaddingTop(paddingTop);
        return cell;
    }

    private PdfPCell createAchievementsTable(Font font) throws DocumentException {
        PdfPCell coverForAchievements = new PdfPCell();
        coverForAchievements.setPadding(0);
        PdfPTable achievementsTable = new PdfPTable(2);
        achievementsTable.setWidths(new float[]{3f, 0.9f});
        achievementsTable.setWidthPercentage(100);
        achievementsTable.addCell(createAchievementsCell("Навчальні досягнення здобувача", font, 2,
                PdfPCell.NO_BORDER, 25));
        PdfPTable grades = new PdfPTable(3);
        grades.setWidthPercentage(100);
        PdfPCell coverForGrades = new PdfPCell();
        coverForGrades.setPadding(0);
        grades.addCell(createAchievementsCell("Розподіл оцінок", font, 3, PdfPCell.NO_BORDER, 25));
        grades.addCell(createCell("Відмінно,   %", font, 0));
        grades.addCell(createCell("Добре,      %", font, 0));
        grades.addCell(createCell("Задовільно, %", font, 0));
        coverForGrades.addElement(grades);
        achievementsTable.addCell(coverForGrades);
        achievementsTable.addCell(createCell("Середній бал", font, 1));
        coverForAchievements.addElement(achievementsTable);
        return coverForAchievements;
    }

    private PdfPCell createAchievementsCell(String text, Font font, int colspan, int border, int fixedHeight) {
        PdfPCell achievementCell = createCell(text, font, 0);
        achievementCell.setColspan(colspan);
        achievementCell.setBorder(border);
        achievementCell.setFixedHeight(fixedHeight);
        return achievementCell;
    }
}
