package ua.edu.chdtu.deanoffice.service.document;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.entity.Degree;
import ua.edu.chdtu.deanoffice.entity.Speciality;
import ua.edu.chdtu.deanoffice.entity.Specialization;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.entity.superclasses.NameEntity;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;
import ua.edu.chdtu.deanoffice.util.LanguageUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static ua.edu.chdtu.deanoffice.util.DocumentUtil.cleanFileName;
import static ua.edu.chdtu.deanoffice.util.DocumentUtil.getFileCreationDateAndTime;
import static ua.edu.chdtu.deanoffice.util.DocumentUtil.getJavaTempDirectory;

@Service
public class ConsolidatedReportService {
    @Value(value = "classpath:fonts/arial/arial.ttf")
    private Resource ttf;
    private static final int NO_BORDER = 0;
    private static final int ALL_BORDER = 15;

    private final CurrentYearService currentYearService;

    @Autowired
    public ConsolidatedReportService(CurrentYearService currentYearService) {
        this.currentYearService = currentYearService;
    }

    public File formConsolidatedReport(Map<CourseForGroup, List<StudentGroup>> coursesToStudentGroups, ApplicationUser user) throws DocumentException, IOException, OperationCannotBePerformedException {
        if (coursesToStudentGroups.size() == 0) {
            throw new OperationCannotBePerformedException("Для формування документу потрібно передати хоча б один курс");
        }
        if (coursesToStudentGroups.values().stream().anyMatch(Objects::isNull) || coursesToStudentGroups.values().stream().anyMatch(List::isEmpty)) {
            throw new OperationCannotBePerformedException("Для формування документу потрібно, щоб кожному предмету відповідала хоча б одна група");
        }

        Document document = new Document(PageSize.A4);
        File file = getTempFile("3BEDEHA-BIDOMICTb");
        PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();

        try {
            for (Map.Entry<CourseForGroup, List<StudentGroup>> courseToStudentGroups : coursesToStudentGroups.entrySet()) {
                createOneConsolidatedReport(document, courseToStudentGroups.getKey(), courseToStudentGroups.getValue(), user);
            }
        } catch (DocumentException | IOException | OperationCannotBePerformedException e) {
            document.close();
            file.deleteOnExit();
            throw e;
        }
        document.newPage();

        document.close();
        return file;
    }

    private void createOneConsolidatedReport(Document document, CourseForGroup courseForGroup, List<StudentGroup> studentGroups, ApplicationUser user) throws DocumentException, IOException, OperationCannotBePerformedException {
        Degree degree = studentGroups.get(0).getSpecialization().getDegree();
        for (StudentGroup studentGroup : studentGroups) {
            if (studentGroup.getSpecialization().getDegree().getId() != degree.getId()) {
                throw new OperationCannotBePerformedException("В межах одного курсу всі групи повинні мати один ступінь");
            }
        }
        Speciality speciality = studentGroups.get(0).getSpecialization().getSpeciality();
        for (StudentGroup studentGroup : studentGroups) {
            if (studentGroup.getSpecialization().getSpeciality().getId() != speciality.getId()) {
                speciality = null;
                break;
            }
        }
        Specialization specialization = studentGroups.get(0).getSpecialization();
        for (StudentGroup studentGroup : studentGroups) {
            if (studentGroup.getSpecialization().getId() != specialization.getId()) {
                specialization = null;
                break;
            }
        }
        String groupNames = studentGroups.stream().map(NameEntity::getName).collect(Collectors.joining(", "));

        BaseFont baseFont = BaseFont.createFont(ttf.getURI().getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        final Font FONT = new Font(baseFont);
        final Font BOLD_FONT = new Font(baseFont);
        BOLD_FONT.setStyle(Font.BOLD);
        final Font SMALL_FONT = new Font(baseFont);
        SMALL_FONT.setSize(8);
        final PdfPCell EMPTY_CELL_NO_BORDER = new PdfPCell(new Phrase(""));
        EMPTY_CELL_NO_BORDER.setBorder(0);
        final PdfPCell EMPTY_CELL = new PdfPCell(new Phrase(""));
        createTitleOfDocument(BOLD_FONT, document);
        createFacultyHeader(FONT, BOLD_FONT, document, user.getFaculty().getAbbr().toUpperCase());
        createInfoAboutGroup(FONT, BOLD_FONT, EMPTY_CELL_NO_BORDER, document,
                degree.getName(),
                speciality != null ? speciality.getName() : "",
                specialization != null ? specialization.getName() : "",
                "",
                groupNames,
                currentYearService.getYear() + "-" + (currentYearService.getYear() + 1)
        );
        createNameTitle(BOLD_FONT, document);
        createInfoAboutCourse(FONT, SMALL_FONT, EMPTY_CELL_NO_BORDER, document, courseForGroup.getCourse().getCourseName().getName());
        createInfoAboutSemester(FONT, document, courseForGroup.getCourse().getSemester().toString());
        createInfoAboutKnowledgeControl(
                FONT,
                SMALL_FONT,
                EMPTY_CELL_NO_BORDER,
                document,
                courseForGroup.getCourse().getKnowledgeControl().getName(),
                String.valueOf(courseForGroup.getCourse().getHours().intValue())
        );
        createInfoAboutTeacher(FONT, SMALL_FONT, EMPTY_CELL_NO_BORDER, document, courseForGroup.getTeacher().getFullNameUkr());
        createMainTable(FONT, SMALL_FONT, BOLD_FONT, document, studentGroups);

        String deanInitials = user.getFaculty().getDean();
        if (deanInitials != null) {
            String[] strings = deanInitials.split(" ");
            if (strings.length == 3)
                deanInitials = strings[0] + " " + strings[1].substring(0, 1) + "." + strings[2].substring(0, 1) + ".";
        } else {
            deanInitials = "";
        }

        createInfoAboutExamination(FONT, SMALL_FONT, EMPTY_CELL_NO_BORDER, document, deanInitials);
        createTitleOfMarks(FONT, document);
        createMarksTable(FONT, EMPTY_CELL, document);
        createFooter(FONT, SMALL_FONT, EMPTY_CELL_NO_BORDER, document, courseForGroup.getTeacher().getFullNameUkr());
    }

    private void createFooter(Font FONT, Font SMALL_FONT, PdfPCell EMPTY_CELL_NO_BORDER, Document document, String teacherFullName) throws DocumentException {
        PdfPTable footerTable = new PdfPTable(3);
        footerTable.setWidths(new int[]{2, 1, 2});
        footerTable.setWidthPercentage(100);
        footerTable.getDefaultCell().setBorder(0);
        footerTable.addCell(new Phrase("Екзаменатор (викладач)", FONT));
        footerTable.addCell("______________");
        footerTable.addCell(createCellWithUnderline(teacherFullName, FONT));
        footerTable.addCell(EMPTY_CELL_NO_BORDER);
        footerTable.addCell(createCellWithParameters("(підпис)", SMALL_FONT, NO_BORDER, Element.ALIGN_CENTER, Element.ALIGN_TOP, 1, 1));
        footerTable.addCell(createCellWithParameters("(прізвище та ініціали)", SMALL_FONT, NO_BORDER, Element.ALIGN_CENTER, Element.ALIGN_TOP, 1, 1));
        document.add(footerTable);
    }

    private void createMarksTable(Font FONT, PdfPCell EMPTY_CELL, Document document) throws DocumentException {
        PdfPTable marksTable = new PdfPTable(5);
        marksTable.setWidthPercentage(100);
        marksTable.setWidths(new int[]{1, 2, 1, 2, 2});
        marksTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        marksTable.addCell(createCellWithParameters("ВСЬОГО ОЦІНОК", FONT, ALL_BORDER, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, 1, 2));
        marksTable.addCell(createCellWithParameters("СУМА БАЛІВ", FONT, ALL_BORDER, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, 1, 2));
        marksTable.addCell(createCellWithParameters("ОЦІНКА ECTS", FONT, ALL_BORDER, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, 1, 2));
        marksTable.addCell(createCellWithParameters("ОЦІНКА ЗА НАЦІОНАЛЬНОЮ ШКАЛОЮ", FONT, ALL_BORDER, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, 2, 1));
        marksTable.addCell(new Phrase("екзамен", FONT));
        marksTable.addCell(new Phrase("залік", FONT));

        marksTable.addCell(EMPTY_CELL);
        marksTable.addCell("90-100");
        marksTable.addCell("A");
        marksTable.addCell(new Phrase("відмінно", FONT));
        marksTable.addCell(createCellWithParameters("зараховано", FONT, ALL_BORDER, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, 1, 5));

        marksTable.addCell(EMPTY_CELL);
        marksTable.addCell("82-89");
        marksTable.addCell("B");
        marksTable.addCell(createCellWithParameters("добре", FONT, ALL_BORDER, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, 1, 2));

        marksTable.addCell(EMPTY_CELL);
        marksTable.addCell("74-81");
        marksTable.addCell("C");

        marksTable.addCell(EMPTY_CELL);
        marksTable.addCell("64-73");
        marksTable.addCell("D");
        marksTable.addCell(createCellWithParameters("задовільно", FONT, ALL_BORDER, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, 1, 2));

        marksTable.addCell(EMPTY_CELL);
        marksTable.addCell("60-63");
        marksTable.addCell("E");

        marksTable.addCell(EMPTY_CELL);
        marksTable.addCell("35-59");
        marksTable.addCell("FX");
        marksTable.addCell(createCellWithParameters("незадовільно", FONT, ALL_BORDER, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, 1, 2));
        marksTable.addCell(createCellWithParameters("не зараховано", FONT, ALL_BORDER, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, 1, 2));

        marksTable.addCell(EMPTY_CELL);
        marksTable.addCell("1-34");
        marksTable.addCell("F");
        document.add(marksTable);
    }

    private void createTitleOfMarks(Font FONT, Document document) throws DocumentException {
        Font boldFont = new Font(FONT);
        boldFont.setStyle(Font.BOLD);
        Paragraph titleOfFooter = new Paragraph("Підсумки складання екзамену (заліку)\n", boldFont);
        titleOfFooter.setAlignment(Element.ALIGN_CENTER);
        document.add(titleOfFooter);
        document.add(new Paragraph(" "));
    }

    private void createInfoAboutExamination(Font FONT, Font SMALL_FONT, PdfPCell EMPTY_CELL_NO_BORDER, Document document, String facultyDean) throws DocumentException {
        PdfPTable aboutExamination = new PdfPTable(6);
        aboutExamination.setWidthPercentage(100);
        aboutExamination.setWidths(new int[]{3, 1, 3, 1, 3, 1});
        aboutExamination.getDefaultCell().setBorder(0);

        aboutExamination.addCell(createCellWithParameters("Декан факультету", FONT, NO_BORDER, Element.ALIGN_LEFT, Element.ALIGN_BOTTOM, 1, 1));
        aboutExamination.addCell(EMPTY_CELL_NO_BORDER);
        aboutExamination.addCell(createCellWithUnderline("", FONT));
        aboutExamination.addCell(EMPTY_CELL_NO_BORDER);
        aboutExamination.addCell(createCellWithUnderline(facultyDean, FONT));
        aboutExamination.addCell(EMPTY_CELL_NO_BORDER);

        aboutExamination.addCell(EMPTY_CELL_NO_BORDER);
        aboutExamination.addCell(EMPTY_CELL_NO_BORDER);
        aboutExamination.addCell(createCellWithParameters("(підпис)", SMALL_FONT, NO_BORDER, Element.ALIGN_CENTER, Element.ALIGN_TOP, 1, 1));
        aboutExamination.addCell(EMPTY_CELL_NO_BORDER);
        aboutExamination.addCell(createCellWithParameters("(прізвище, ініціали)", SMALL_FONT, NO_BORDER, Element.ALIGN_CENTER, Element.ALIGN_TOP, 1, 1));
        aboutExamination.addCell(EMPTY_CELL_NO_BORDER);

        document.add(aboutExamination);
    }

    private void createMainTable(Font FONT, Font SMALL_FONT, Font BOLD_FONT, Document document, List<StudentGroup> studentGroups) throws DocumentException {
        PdfPTable mainTable = new PdfPTable(8);
        mainTable.setWidthPercentage(100);
        mainTable.setWidths(new int[]{1, 5, 3, 2, 2, 2, 3, 5});

        mainTable.addCell(createCellWithParameters("№\nз/п", SMALL_FONT, ALL_BORDER, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, 1, 2));
        mainTable.addCell(createCellWithParameters("Прізвище та ініціали студентів", SMALL_FONT, ALL_BORDER, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, 1, 2));
        mainTable.addCell(createCellWithParameters("№ залікової книжки (індивідуального навчального плану)", SMALL_FONT, ALL_BORDER, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, 1, 2));

        mainTable.addCell(createCellWithParameters("Оцінка", SMALL_FONT, ALL_BORDER, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, 3, 1));

        mainTable.addCell(createCellWithParameters("Дата", SMALL_FONT, ALL_BORDER, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, 1, 2));
        mainTable.addCell(createCellWithParameters("Підпис викладача", SMALL_FONT, ALL_BORDER, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, 1, 2));

        mainTable.addCell(createCellWithParameters("За національною шкалою", SMALL_FONT, ALL_BORDER, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, 1, 1));
        mainTable.addCell(createCellWithParameters("кількість балів за 100 бальною шкалою", SMALL_FONT, ALL_BORDER, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, 1, 1));
        mainTable.addCell(createCellWithParameters("ECTS", SMALL_FONT, ALL_BORDER, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, 1, 1));

        for (int i = 1; i <= 8; i++) {
            mainTable.addCell(createCellWithParameters(String.valueOf(i), FONT, ALL_BORDER, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, 1, 1));
        }

        int index = 1;
        for (StudentGroup studentGroup : studentGroups) {
            mainTable.addCell(createCellWithParameters(String.valueOf(index++), FONT, ALL_BORDER, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, 1, 1));
            mainTable.addCell(createCellWithParameters(studentGroup.getName(), BOLD_FONT, ALL_BORDER, Element.ALIGN_LEFT, Element.ALIGN_BOTTOM, 1, 1));
            for (int i = 0; i < 6; i++) {
                mainTable.addCell("");
            }
            for (StudentDegree studentDegree : studentGroup.getStudentDegrees()) {
                mainTable.addCell(createCellWithParameters(String.valueOf(index++), FONT, ALL_BORDER, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE, 1, 1));
                mainTable.addCell(createCellWithParameters(studentDegree.getStudent().getInitialsUkr(), FONT, ALL_BORDER, Element.ALIGN_LEFT, Element.ALIGN_BOTTOM, 1, 1));
                mainTable.addCell(createCellWithParameters(studentDegree.getRecordBookNumber(), FONT, ALL_BORDER, Element.ALIGN_LEFT, Element.ALIGN_BOTTOM, 1, 1));
                for (int i = 0; i < 5; i++) {
                    mainTable.addCell("");
                }
            }
        }

        for (; index <= 50;) {
            mainTable.addCell(String.valueOf(index++));
            for (int j = 0; j < 7; j++) {
                mainTable.addCell("");
            }
        }

        document.add(mainTable);
    }

    private void createInfoAboutTeacher(Font FONT, Font SMALL_FONT, PdfPCell EMPTY_CELL_NO_BORDER, Document document, String teacherFullName) throws DocumentException {
        Paragraph emptyParagraph = new Paragraph("");
        document.add(emptyParagraph);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{1, 6});
        table.getDefaultCell().setBorder(0);
        table.addCell(new Phrase("Викладач", FONT));
        table.addCell(createCellWithUnderline(teacherFullName, FONT));
        table.addCell(EMPTY_CELL_NO_BORDER);
        table.addCell(createCellWithParameters("(вчене звання, прізвище та ініціали викладача, який виставляє оцінку)", SMALL_FONT, NO_BORDER, Element.ALIGN_CENTER, Element.ALIGN_TOP, 1, 1));
        document.add(table);
    }

    private void createInfoAboutKnowledgeControl(Font FONT, Font SMALL_FONT, PdfPCell EMPTY_CELL_NO_BORDER, Document document, String knowledgeControl, String hours) throws DocumentException {
        PdfPTable infoAboutCourseTable = new PdfPTable(5);
        infoAboutCourseTable.setWidthPercentage(100);
        infoAboutCourseTable.setWidths(new int[]{5, 3, 1, 4, 1});
        infoAboutCourseTable.getDefaultCell().setBorder(0);

        infoAboutCourseTable.addCell(new Phrase("Форма семестрового контролю", FONT));
        infoAboutCourseTable.addCell(createCellWithUnderline(knowledgeControl, FONT));
        infoAboutCourseTable.addCell(EMPTY_CELL_NO_BORDER);
        infoAboutCourseTable.addCell(createCellWithParameters("Загальна кількість годин:", FONT, 0, Element.ALIGN_RIGHT, Element.ALIGN_BOTTOM, 1,1));
        infoAboutCourseTable.addCell(createCellWithUnderline(hours, FONT));
        infoAboutCourseTable.addCell(EMPTY_CELL_NO_BORDER);
        infoAboutCourseTable.addCell(createCellWithParameters("(іспит, залік, дифзалік)", SMALL_FONT, NO_BORDER, Element.ALIGN_CENTER, Element.ALIGN_TOP, 1, 1));
        infoAboutCourseTable.addCell(EMPTY_CELL_NO_BORDER);
        infoAboutCourseTable.addCell(EMPTY_CELL_NO_BORDER);
        infoAboutCourseTable.addCell(EMPTY_CELL_NO_BORDER);
        document.add(infoAboutCourseTable);
    }

    private void createInfoAboutSemester(Font FONT, Document document, String semester) throws DocumentException {
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(40);
        table.setWidths(new int[]{1, 1, 8});
        table.getDefaultCell().setBorder(0);
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(new Phrase("за ", FONT));
        table.addCell(createCellWithUnderline(semester, FONT));
        table.addCell(new Phrase(" навчальний семестр", FONT));
        document.add(table);
    }

    private void createInfoAboutCourse(Font FONT, Font SMALL_FONT, PdfPCell EMPTY_CELL_NO_BORDER, Document document, String courseName) throws DocumentException {
        PdfPTable courseNameTable = new PdfPTable(2);
        courseNameTable.setWidthPercentage(100);
        courseNameTable.setWidths(new int[]{1, 40});
        courseNameTable.getDefaultCell().setBorder(0);

        courseNameTable.addCell(new Phrase("з ", FONT));
        courseNameTable.addCell(createCellWithUnderline(courseName, FONT));
        courseNameTable.addCell(EMPTY_CELL_NO_BORDER);
        courseNameTable.addCell(createCellWithParameters("(назва навчальної дисципліни)", SMALL_FONT, NO_BORDER, Element.ALIGN_CENTER, Element.ALIGN_TOP, 1, 1));
        document.add(courseNameTable);
    }

    private void createNameTitle(Font BOLD_FONT, Document document) throws DocumentException {
        Paragraph nameOfDocument = new Paragraph("ВІДОМІСТЬ ОБЛІКУ УСПІШНОСТІ №_________", BOLD_FONT);
        nameOfDocument.setAlignment(Element.ALIGN_CENTER);
        document.add(nameOfDocument);
    }

    private void createInfoAboutGroup(
            Font FONT, Font BOLD_FONT, PdfPCell EMPTY_CELL_NO_BORDER, Document document,
            String degree,
            String speciality,
            String specialization,
            String courseNumber,
            String group,
            String studyYears) throws DocumentException {
        PdfPTable dataTable = new PdfPTable(5);
        dataTable.getDefaultCell().setBorder(0);
        dataTable.setWidthPercentage(100);
        dataTable.setWidths(new int[]{2, 3, 1, 1, 6});
        /*row 1*/
        dataTable.addCell(createCellWithParameters(degree.toUpperCase(), BOLD_FONT, NO_BORDER, Element.ALIGN_LEFT, Element.ALIGN_BOTTOM, 2, 1));
        dataTable.addCell(EMPTY_CELL_NO_BORDER);
        dataTable.addCell(createCellWithParameters(speciality, FONT, NO_BORDER, Element.ALIGN_CENTER, Element.ALIGN_BOTTOM, 2, 1));
        /*row 2*/
        dataTable.addCell(createCellWithParameters("Спеціальність", FONT, NO_BORDER, Element.ALIGN_LEFT, Element.ALIGN_BOTTOM, 2, 1));
        dataTable.addCell(EMPTY_CELL_NO_BORDER);
        PdfPCell specializationCell = createCellWithParameters(specialization, FONT, NO_BORDER, Element.ALIGN_CENTER, Element.ALIGN_BOTTOM, 2, 1);
        specializationCell.setBorderWidthBottom(1);
        dataTable.addCell(specializationCell);
        /*row 3*/
        dataTable.addCell(createCellWithParameters("Курс", FONT, 0, Element.ALIGN_LEFT, Element.ALIGN_BOTTOM, 1, 1));
        dataTable.addCell(createCellWithUnderline(courseNumber, FONT));
        dataTable.addCell(EMPTY_CELL_NO_BORDER);
        dataTable.addCell(createCellWithParameters("Група", FONT, 0, Element.ALIGN_RIGHT, Element.ALIGN_BOTTOM, 1, 1));
        dataTable.addCell(createCellWithUnderline(group, FONT));
        /*row 4*/
        dataTable.addCell(createCellWithUnderline(studyYears, FONT));
        dataTable.addCell(new Phrase("навчальний рік", FONT));
        dataTable.addCell(EMPTY_CELL_NO_BORDER);
        dataTable.addCell(EMPTY_CELL_NO_BORDER);
        dataTable.addCell(EMPTY_CELL_NO_BORDER);
        document.add(dataTable);
    }

    private void createFacultyHeader(Font FONT, Font BOLD_FONT, Document document, String faculty) throws DocumentException {
        PdfPTable facultyTable = new PdfPTable(2);
        facultyTable.setHorizontalAlignment(Element.ALIGN_LEFT);
        facultyTable.getDefaultCell().setBorder(0);
        facultyTable.setWidthPercentage(30);
        facultyTable.setWidths(new int[]{1, 1});

        facultyTable.addCell(new Phrase("Факультет", FONT));
        facultyTable.addCell(createCellWithUnderline(faculty, BOLD_FONT));

        document.add(facultyTable);
    }

    private void createTitleOfDocument(Font BOLD_FONT, Document document) throws DocumentException {
        Paragraph nameOfUniversity = new Paragraph("Черкаський державний технологічний університет", BOLD_FONT);
        nameOfUniversity.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(nameOfUniversity);
    }

    private File getTempFile(String fileNamePrefix) {
        String filePath = getJavaTempDirectory()
                + "/" + cleanFileName(LanguageUtil.transliterate(fileNamePrefix))
                + getFileCreationDateAndTime() + ".pdf";
        return new File(filePath);
    }

    private PdfPCell createCellWithUnderline(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(0);
        cell.setBorderWidthBottom(1);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }

    private PdfPCell createCellWithParameters(String text, Font font, int border, int horizontalAlign, int verticalAlign, int colspan, int rowspan) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(border);
        cell.setHorizontalAlignment(horizontalAlign);
        cell.setVerticalAlignment(verticalAlign);
        cell.setColspan(colspan);
        cell.setRowspan(rowspan);
        return cell;
    }
}
