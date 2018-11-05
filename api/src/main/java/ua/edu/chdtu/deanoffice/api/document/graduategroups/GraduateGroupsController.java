package ua.edu.chdtu.deanoffice.api.document.graduategroups;

import com.google.common.util.concurrent.AtomicDouble;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.api.document.groupgrade.GroupGradeReportController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.exception.UnauthorizedFacultyDataException;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


@RestController
@RequestMapping("/documents/graduate-groups")
public class GraduateGroupsController extends DocumentResponseController{

    private CourseForGroupService courseForGroupService;

    @Autowired
    public GraduateGroupsController(CourseForGroupService courseForGroupService) {
        this.courseForGroupService = courseForGroupService;
    }

    @GetMapping("/{group_id}/subjects")
    public ResponseEntity generateListOfSubjectForGraduationGroups(
            @PathVariable("group_id") Integer groupId,
            @CurrentUser ApplicationUser user) {
        try {
            List<CourseForGroup> courseForGroups = courseForGroupService.getCoursesForOneGroup(groupId);
            validateBody(courseForGroups);
            verifyAccess(user, courseForGroups.get(0).getStudentGroup());
            // Попередньо форматуємо таблицю
            String groupName = courseForGroups.get(0).getStudentGroup().getName();
            Map<KnowledgeControl, List<CourseForGroup>> tableMap = new TreeMap<>(Comparator.comparingInt(BaseEntity::getId));
            courseForGroups.forEach(courseForGroup -> {
                KnowledgeControl currentKnowledgeControl = courseForGroup.getCourse().getKnowledgeControl();
                if (tableMap.containsKey(currentKnowledgeControl)) {
                    tableMap.get(currentKnowledgeControl).add(courseForGroup);
                } else {
                    tableMap.put(currentKnowledgeControl, new ArrayList<>(Collections.singletonList(courseForGroup)));
                }
            });
            tableMap.forEach((knowledgeControl, courseForGroups1) -> {
                courseForGroups1.sort((o1, o2) -> {
                    if (!o1.getCourse().getSemester().equals(o2.getCourse().getSemester())) {
                        return o1.getCourse().getSemester().compareTo(o2.getCourse().getSemester());
                    } else {
                        return o1.getCourse().getCourseName().getName().compareTo(o2.getCourse().getCourseName().getName());
                    }
                });
            });
            File file = createDocument(groupName, tableMap);
            return buildDocumentResponseEntity(file, file.getName(), MEDIA_TYPE_PDF);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private String getJavaTempDirectory() {
        return System.getProperty("java.io.tmpdir");
    }

    private String getFileCreationDateAndTime() {
        return new SimpleDateFormat(" dd-MM-yyyy HH-mm").format(new Date());
    }

    private String cleanFileName(final String fileName) {
        return fileName
                .replaceAll(" +", " ")
                .replaceAll("[^a-zA-Z0-9_]+", "");
    }

    private File createDocument(String groupName, Map<KnowledgeControl, List<CourseForGroup>> tableMap) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        String filePath = getJavaTempDirectory() + "/" + cleanFileName(groupName) + getFileCreationDateAndTime() + ".pdf";
        File file = new File(filePath);
        PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(file));
        BaseFont baseFont = BaseFont.createFont("c:/Windows/Fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font font = new Font(baseFont);
        document.open();
        PdfPTable table = new PdfPTable(5);
        table.setWidths(new int[]{1, 1, 8, 2, 2});
        table.setWidthPercentage(100);

        // Templates
        final PdfPCell numberOfRowHeaderCell = createHeaderCell("№", font);
        final PdfPCell semesterHeaderCell = createHeaderCell("Сем.", font);
        final PdfPCell hoursHeaderCell = createHeaderCell("Години", font);
        final PdfPCell creditsHeaderCell = createHeaderCell("Кредити", font);
        //Header
        {
            PdfPCell headerCell = new PdfPCell(new Phrase("Група: " + groupName, font));
            headerCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            headerCell.setColspan(5);
            table.addCell(headerCell);
        }
        // End of header
        // Counter
        AtomicInteger hoursSum = new AtomicInteger(0);
        AtomicDouble creditsSum = new AtomicDouble(0);
        // Main table
        tableMap.forEach((knowledgeControl, courseForGroups) -> {
            // Add subtitle
            table.addCell(numberOfRowHeaderCell);
            table.addCell(semesterHeaderCell);
            PdfPCell knowledgeControlHeader = new PdfPCell(new Phrase(knowledgeControl.getName(), font));
            knowledgeControlHeader.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(knowledgeControlHeader);
            table.addCell(hoursHeaderCell);
            table.addCell(creditsHeaderCell);
            AtomicInteger index = new AtomicInteger(1);
            // end of subtitle
            // Main data
            courseForGroups.forEach(courseForGroup -> {
                Course currentCourse = courseForGroup.getCourse();
                // number cell
                table.addCell(createDataCenterCell(index.toString(), font));
                // semester cell
                table.addCell(createDataCenterCell(currentCourse.getSemester().toString(), font));
                // course name cell
                PdfPCell courseNameDataCell = new PdfPCell(new Phrase(currentCourse.getCourseName().getName(), font));
                table.addCell(courseNameDataCell);
                // hours cell
                table.addCell(createDataCenterCell(currentCourse.getHours().toString(), font));
                // credits
                table.addCell(createDataCenterCell(currentCourse.getCredits().toPlainString(), font));
                // counters
                hoursSum.addAndGet(currentCourse.getHours());
                creditsSum.addAndGet(currentCourse.getCredits().doubleValue());
                index.getAndIncrement();
            });
            // end of Main data
            index.set(1);
        });
        // Footer
        {
            PdfPCell sumCell = new PdfPCell(new Phrase("Всього: ", font));
            sumCell.setColspan(3);
            table.addCell(sumCell);
            table.addCell(createDataCenterCell(hoursSum.toString(), font));
            table.addCell(createDataCenterCell(creditsSum.toString(), font));
        }
        // End of footer
        document.add(table);
        document.close();
        return file;
    }

    private PdfPCell createDataCenterCell(String text, Font font) {
        PdfPCell semesterDataCell = new PdfPCell(new Phrase(text, font));
        semesterDataCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        return semesterDataCell;
    }

    private PdfPCell createHeaderCell(String text, Font font) {
        PdfPCell numberOfRowHeaderCell = new PdfPCell(new Phrase(text, font));
        numberOfRowHeaderCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        numberOfRowHeaderCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        return numberOfRowHeaderCell;
    }

    private void verifyAccess(ApplicationUser user, StudentGroup studentGroup) throws UnauthorizedFacultyDataException {
        if (user.getFaculty().getId() != studentGroup.getSpecialization().getFaculty().getId()) {
            throw new UnauthorizedFacultyDataException("Група знаходить в недоступному факультеті для поточного користувача");
        }
    }

    private void validateBody(List<CourseForGroup> courseForGroups) throws OperationCannotBePerformedException {
        if (courseForGroups.size() == 0) {
            String exceptionMessage = "Групу не було знайдено";
            throw new OperationCannotBePerformedException(exceptionMessage);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, GroupGradeReportController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
