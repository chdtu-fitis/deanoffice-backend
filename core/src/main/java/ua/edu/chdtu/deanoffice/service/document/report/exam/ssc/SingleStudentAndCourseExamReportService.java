package ua.edu.chdtu.deanoffice.service.document.report.exam.ssc;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;

import java.io.*;
import java.util.List;

@Service
public class SingleStudentAndCourseExamReportService {
    public static final float PAGE_MARGIN = 36f;
    public static final float FONT_SIZE_14 = 14f;
    public static final float FONT_SIZE_12 = 12f;
    public static final float FONT_SIZE_10 = 10f;

    @Value(value = "classpath:fonts/timesnewroman/times.ttf")
    private Resource ttf;

    @Autowired
    CourseForGroupService courseForGroupService;
    @Autowired
    private CurrentYearService currentYearService;

    public File formDocument(List<StudentCourse> studentCourses) throws IOException, DocumentException {
        Document document = new Document(PageSize.A4, PAGE_MARGIN, PAGE_MARGIN, PAGE_MARGIN, PAGE_MARGIN);
        String filePath = getJavaTempDirectory() + "/" + "name" +".pdf";
        File file = new File(filePath);
        PdfWriter.getInstance(document, new FileOutputStream(file));
        try {
            document.open();
            fillDocument(document,studentCourses);
        } finally {
            if (document != null)
                document.close();
        }
        return file;
    }

    private void fillDocument(Document document, List<StudentCourse> studentCourses) throws DocumentException, IOException {
        int pagesCount = studentCourses.size() / 4;
        if (studentCourses.size() % 4 != 0){
            pagesCount++;
        }
        document.setMargins(25, 25, 2, 2);
        for (int i = 0; i < pagesCount; i++) {
            document.newPage();
            for (int j = 0; j < 4; j++) {
                int k = i*4 + j;
                if (k >= studentCourses.size())
                    break;
                addFrontForm(document, studentCourses.get(k));
            }
            document.newPage();
            for (int j = 0; j < 4; j++) {
                int k = i*4 + j;
                if (k >= studentCourses.size())
                    break;
                addBackForm(document, studentCourses.get(j));
            }
        }
    }

    private Paragraph createCenterAlignedParagraph(String text, Font font, int spacingAfter) {
        Paragraph element = new Paragraph(text, font);
        element.setAlignment(Element.ALIGN_CENTER);
        element.setSpacingAfter(spacingAfter);
        return element;
    }

    private PdfPTable addFrontForm(Document document, StudentCourse studentCourse) throws DocumentException, IOException {
        StudentDegree student = studentCourse.getStudentDegree();
        Course course = studentCourse.getCourse();
        CourseForGroup courseForGroup = studentCourse.getCourseForGroup();

        BaseFont baseFont = BaseFont.createFont(ttf.getURI().getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font font = new Font(baseFont, 14);
        Font boldFont = new Font(baseFont,14, Font.BOLD);
        Font underlineFont = new Font(baseFont, 8);

        document.add(createCenterAlignedParagraph("ЧЕРКАСЬКИЙ ДЕРЖАВНИЙ ТЕХНОЛОГІЧНИЙ УНІВЕРСИТЕТ", font, 0));
        PdfPTable facultyTable = createFacultyTable(student, font);
        PdfPTable infoTable = createInfoTable(student, course, font);
        PdfPTable studentTable = createStudentTable(student, font);
        PdfPTable courseTable = createCourseTable(course, font);
        PdfPTable teacherTable = createTeacherTable(courseForGroup, font);

        facultyTable.setHorizontalAlignment(Element.ALIGN_LEFT);
        document.add(facultyTable);
        document.add(infoTable);
        document.add(createCenterAlignedParagraph("АРКУШ УСПІШНОСТІ № _____", boldFont, 0));
        document.add(studentTable);
        document.add(createCenterAlignedParagraph("прізвище та ініціали студента", underlineFont, 0));
        document.add(courseTable);
        document.add(createCenterAlignedParagraph("назва навчальної дисципліни", underlineFont, 0));
        document.add(teacherTable);
        document.add(createCenterAlignedParagraph("вчене звання, прізвище та ініціали", underlineFont, 3));

        return facultyTable;
    }

    private PdfPTable createTeacherTable(CourseForGroup courseForGroup, Font font) throws DocumentException {
        PdfPTable teacherTable = new PdfPTable(2);
        teacherTable.setWidthPercentage(100);
        teacherTable.setTotalWidth(new float[]{1f,7f});

        PdfPCell teacherCell = new PdfPCell();
        teacherCell.addElement(new Paragraph(FrontSideConfig.TEACHER, font));
        teacherCell.setBorder(PdfPCell.NO_BORDER);

        PdfPCell teacherInfoCell = new PdfPCell();
//        teacherInfoCell.addElement(new Paragraph((courseForGroup.getTeacher() != null ? courseForGroup.getTeacher().getInitialsUkr() : ""), font));
        teacherInfoCell.setBorder(PdfPCell.BOTTOM);

        teacherTable.addCell(teacherCell);
        teacherTable.addCell(teacherInfoCell);
        return teacherTable;
    }

    private PdfPTable createCourseTable(Course course, Font font) throws DocumentException {
        PdfPTable courseTable = new PdfPTable(2);
        courseTable.setWidthPercentage(100);
        courseTable.setTotalWidth(new float[]{3f,7f});

        PdfPCell courseCell = new PdfPCell();
        courseCell.addElement(new Paragraph(FrontSideConfig.COURSE, font));
        courseCell.setBorder(PdfPCell.NO_BORDER);

        PdfPCell courseInfoCell = new PdfPCell();
        courseInfoCell.addElement(new Paragraph(course.getCourseName().getName(), font));
        courseInfoCell.setBorder(PdfPCell.BOTTOM);

        courseTable.addCell(courseCell);
        courseTable.addCell(courseInfoCell);
        return courseTable;
    }

    private PdfPTable createStudentTable(StudentDegree student, Font font) throws DocumentException {
        PdfPTable studentTable = new PdfPTable(2);
        studentTable.setWidthPercentage(100);
        studentTable.setTotalWidth(new float[]{1f,7f});

        PdfPCell studentCell = new PdfPCell();
        studentCell.addElement(new Paragraph(FrontSideConfig.SRUDENT, font));
        studentCell.setBorder(PdfPCell.NO_BORDER);

        PdfPCell studentInfoCell = new PdfPCell();
        studentInfoCell.addElement(new Paragraph(student.getStudent().getFullNameUkr(), font));
        studentInfoCell.setBorder(PdfPCell.BOTTOM);

        studentTable.addCell(studentCell);
        studentTable.addCell(studentInfoCell);
        return studentTable;
    }

    private PdfPTable createInfoTable(StudentDegree student, Course course, Font font) throws DocumentException {
        PdfPTable infoTable = new PdfPTable(8);
        infoTable.setWidthPercentage(100);
        infoTable.setTotalWidth(new float[]{0.8f, 0.2f, 1.5f, 0.2f, 1.4f, 1.5f, 2.7f, 4f});

        PdfPCell yearInfoCell = new PdfPCell();
        yearInfoCell.addElement(new Paragraph(FrontSideConfig.YEAR, font));
        yearInfoCell.setBorder(PdfPCell.NO_BORDER);

        PdfPCell yearDataInfoCell = new PdfPCell();
        yearDataInfoCell.addElement(new Paragraph(String.valueOf(currentYearService.get().getCurrYear() - student.getStudentGroup().getCreationYear() + 1), font));
        yearDataInfoCell.setBorder(PdfPCell.BOTTOM);

        PdfPCell semesterInfoCell = new PdfPCell();
        Paragraph semesterInfoText = new Paragraph(FrontSideConfig.SEMESTER, font);
        semesterInfoText.setAlignment(Element.ALIGN_RIGHT);
        semesterInfoCell.addElement(semesterInfoText);
        semesterInfoCell.setBorder(PdfPCell.NO_BORDER);

        PdfPCell semesterDataInfoCell = new PdfPCell();
        semesterDataInfoCell.addElement(new Paragraph(dividesByTwo(course.getSemester().intValue()), font));
        semesterDataInfoCell.setBorder(PdfPCell.BOTTOM);

        PdfPCell groupInfoCell = new PdfPCell();
        Paragraph groupInfoText = new Paragraph(FrontSideConfig.GROUP, font);
        groupInfoText.setAlignment(Element.ALIGN_RIGHT);
        groupInfoCell.addElement(groupInfoText);
        groupInfoCell.setBorder(PdfPCell.NO_BORDER);

        PdfPCell groupDataInfoCell = new PdfPCell();
        groupDataInfoCell.addElement(new Paragraph(student.getStudentGroup().getName(), font));
        groupDataInfoCell.setBorder(PdfPCell.BOTTOM);

        PdfPCell knowlegeControlInfoCell = new PdfPCell();
        knowlegeControlInfoCell.addElement(new Paragraph(FrontSideConfig.KNOWLEDGE_CONTROL, font));
        knowlegeControlInfoCell.setBorder(PdfPCell.NO_BORDER);

        PdfPCell knowlegeControlDataInfoCell = new PdfPCell();
        knowlegeControlDataInfoCell.addElement(new Paragraph(course.getKnowledgeControl().getName(), font));
        knowlegeControlDataInfoCell.setBorder(PdfPCell.BOTTOM);

        infoTable.addCell(yearInfoCell);
        infoTable.addCell(yearDataInfoCell);
        infoTable.addCell(semesterInfoCell);
        infoTable.addCell(semesterDataInfoCell);
        infoTable.addCell(groupInfoCell);
        infoTable.addCell(groupDataInfoCell);
        infoTable.addCell(knowlegeControlInfoCell);
        infoTable.addCell(knowlegeControlDataInfoCell);
        return infoTable;
    }

    private PdfPTable createFacultyTable(StudentDegree student, Font font) throws DocumentException {
        PdfPTable facultyTable = new PdfPTable(4);
        facultyTable.setWidthPercentage(100);
        facultyTable.setTotalWidth(new float[]{1.3f, 5.5f, 2f, 1f});

        Paragraph paragraph = new Paragraph(FrontSideConfig.CHDTU_NAME, font);
        paragraph.setAlignment(Element.ALIGN_CENTER);

        PdfPCell facultyCell = new PdfPCell();
        facultyCell.addElement(new Paragraph(FrontSideConfig.FACULTY, font));
        facultyCell.setBorder(PdfPCell.NO_BORDER);

        PdfPCell facultyNameCell = new PdfPCell();
        facultyNameCell.addElement(new Paragraph(student.getSpecialization().getFaculty().getName(), font));
        facultyNameCell.setBorder(PdfPCell.BOTTOM);

        PdfPCell yearCell = new PdfPCell();
        yearCell.addElement(new Paragraph(FrontSideConfig.STUDY_YEAR, font));
        yearCell.setBorder(PdfPCell.NO_BORDER);

        PdfPCell yearNameCell = new PdfPCell();
        yearNameCell.addElement(new Paragraph(String.valueOf(currentYearService.get().getCurrYear())+"/"+String.valueOf(currentYearService.get().getCurrYear()+1).substring(2,4), font));
        yearNameCell.setBorder(PdfPCell.BOTTOM);

        facultyTable.addCell(facultyCell);
        facultyTable.addCell(facultyNameCell);
        facultyTable.addCell(yearCell);
        facultyTable.addCell(yearNameCell);
        return facultyTable;
    }

    private PdfPTable addBackForm(Document document, StudentCourse studentCourse) throws DocumentException, IOException {
        StudentDegree student = studentCourse.getStudentDegree();

        BaseFont baseFont = BaseFont.createFont(ttf.getURI().getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font font = new Font(baseFont, 14);
        Font reasonFont = new Font(baseFont, 12);
        Font underlineFont = new Font(baseFont, 8);

        PdfPTable reasonTable = createReasonTable(reasonFont);
        PdfPTable durationTable = createDurationTable(reasonFont);
        PdfPTable deanTable = createDeanTable(student, reasonFont);
        PdfPTable underlineTable = createUnderlineTable(font, underlineFont);

        document.add(createMainTable(baseFont));
        document.add(createDataTable(baseFont,student));
        document.add(reasonTable);
        document.add(durationTable);
        document.add(deanTable);
        document.add(underlineTable);

        return reasonTable;
    }

    private PdfPTable createUnderlineTable(Font font, Font underlineFont) throws DocumentException {
        PdfPTable underlineTable = new PdfPTable(9);
        underlineTable.setWidthPercentage(100);
        underlineTable.setTotalWidth(new float[]{4.4f, 2f, 1.5f, 5f, 1f, 0.5f, 1f, 2.8f, 0.7f });

        PdfPCell underlineInfoCell = new PdfPCell();
        underlineInfoCell.addElement(new Paragraph(" ", font));
        underlineInfoCell.setBorder(PdfPCell.NO_BORDER);

        PdfPCell underlineInitialsCell = new PdfPCell();
        underlineInitialsCell.addElement(new Paragraph("підпис", underlineFont));
        underlineInitialsCell.setBorder(PdfPCell.NO_BORDER);

        PdfPCell emptyUnderlineInitialsCell = new PdfPCell();
        emptyUnderlineInitialsCell.addElement(new Paragraph(" ", underlineFont));
        emptyUnderlineInitialsCell.setBorder(PdfPCell.NO_BORDER);

        PdfPCell underlineNameCell = new PdfPCell();
        underlineNameCell.addElement(new Paragraph("прізвище та ініціали", underlineFont));
        underlineNameCell.setBorder(PdfPCell.NO_BORDER);

        PdfPCell underlineDateCell = new PdfPCell();
        underlineDateCell.addElement(new Paragraph(" ", font));
        underlineDateCell.setBorder(PdfPCell.NO_BORDER);

        PdfPCell emptyUnderlineDateCell = new PdfPCell();
        emptyUnderlineDateCell.addElement(new Paragraph(" ", font));
        emptyUnderlineDateCell.setBorder(PdfPCell.NO_BORDER);

        PdfPCell underlineSecondDateCell = new PdfPCell();
        underlineSecondDateCell.addElement(new Paragraph(" ", font));
        underlineSecondDateCell.setBorder(PdfPCell.NO_BORDER);

        PdfPCell underlineDeanYearCell = new PdfPCell();
        underlineDeanYearCell.addElement(new Paragraph(" ", font));
        underlineDeanYearCell.setBorder(PdfPCell.NO_BORDER);

        PdfPCell underlineEmptyCell = new PdfPCell();
        underlineEmptyCell.addElement(new Paragraph(" ", font));
        underlineEmptyCell.setBorder(PdfPCell.NO_BORDER);

        underlineTable.addCell(underlineInfoCell);
        underlineTable.addCell(underlineInitialsCell);
        underlineTable.addCell(emptyUnderlineInitialsCell);
        underlineTable.addCell(underlineNameCell);
        underlineTable.addCell(underlineDateCell);
        underlineTable.addCell(emptyUnderlineDateCell);
        underlineTable.addCell(underlineSecondDateCell);
        underlineTable.addCell(underlineDeanYearCell);
        underlineTable.addCell(underlineEmptyCell);
        return underlineTable;
    }

    private PdfPTable createDeanTable(StudentDegree student, Font reasonFont) throws DocumentException {
        PdfPTable deanTable = new PdfPTable(9);
        deanTable.setWidthPercentage(100);
        deanTable.setTotalWidth(new float[]{3.5f, 2f, 0.5f, 5f, 1f, 0.5f, 1f, 2.8f, 0.7f });

        PdfPCell infoCell = new PdfPCell();
        infoCell.addElement(new Paragraph(BackSideConfig.DEAN, reasonFont));
        infoCell.setBorder(PdfPCell.NO_BORDER);

        PdfPCell initialsCell = new PdfPCell();
        initialsCell.addElement(new Paragraph(" ", reasonFont));
        initialsCell.setBorder(PdfPCell.BOTTOM);

        PdfPCell emptyInitialsCell = new PdfPCell();
        emptyInitialsCell.addElement(new Paragraph(" ", reasonFont));
        emptyInitialsCell.setBorder(PdfPCell.NO_BORDER);

        PdfPCell nameCell = new PdfPCell();
        nameCell.addElement(new Paragraph(convertFullNameToIntials(student.getSpecialization().getFaculty().getDean()), reasonFont));
        nameCell.setBorder(PdfPCell.BOTTOM);

        PdfPCell dateCell = new PdfPCell();
        dateCell.addElement(new Paragraph("     "+ BackSideConfig.BRACKET_OPEN, reasonFont));
        dateCell.setBorder(PdfPCell.NO_BORDER);

        PdfPCell emptyDateCell = new PdfPCell();
        emptyDateCell.addElement(new Paragraph( " ", reasonFont));
        emptyDateCell.setBorder(PdfPCell.BOTTOM);

        PdfPCell secondDateCell = new PdfPCell();
        secondDateCell.addElement(new Paragraph(BackSideConfig.BRACKET_CLOSE, reasonFont));
        secondDateCell.setBorder(PdfPCell.NO_BORDER);

        PdfPCell deanYearCell = new PdfPCell();
        deanYearCell.addElement(new Paragraph(BackSideConfig.DEANYEAR, reasonFont));
        deanYearCell.setBorder(PdfPCell.NO_BORDER);

        PdfPCell emptyCell = new PdfPCell();
        emptyCell.addElement(new Paragraph(BackSideConfig.YEAR_MARK, reasonFont));
        emptyCell.setBorder(PdfPCell.NO_BORDER);

        deanTable.addCell(infoCell);
        deanTable.addCell(initialsCell);
        deanTable.addCell(emptyInitialsCell);
        deanTable.addCell(nameCell);
        deanTable.addCell(dateCell);
        deanTable.addCell(emptyDateCell);
        deanTable.addCell(secondDateCell);
        deanTable.addCell(deanYearCell);
        deanTable.addCell(emptyCell);
        return deanTable;
    }

    private PdfPTable createDurationTable(Font reasonFont) throws DocumentException {
        PdfPTable durationTable = new PdfPTable(6);
        durationTable.setWidthPercentage(100);
        durationTable.setTotalWidth(new float[]{5f, 1f, 1f, 2f, 1f, 4f });

        PdfPCell durationCell = new PdfPCell();
        durationCell.addElement(new Paragraph(BackSideConfig.SHEET, reasonFont));
        durationCell.setBorder(PdfPCell.NO_BORDER);

        PdfPCell underlineCell = new PdfPCell();
        underlineCell.addElement(new Paragraph("____", reasonFont));
        underlineCell.setBorder(PdfPCell.NO_BORDER);

        PdfPCell dayCell = new PdfPCell();
        dayCell.addElement(new Paragraph(BackSideConfig.SHEET_END, reasonFont));
        dayCell.setBorder(PdfPCell.NO_BORDER);

        PdfPCell monthCell = new PdfPCell();
        monthCell.addElement(new Paragraph(BackSideConfig.YEAR, reasonFont));
        monthCell.setBorder(PdfPCell.NO_BORDER);

        PdfPCell yearCell = new PdfPCell();
        yearCell.addElement(new Paragraph(BackSideConfig.YEAR_MARK, reasonFont));
        yearCell.setBorder(PdfPCell.NO_BORDER);

        PdfPCell halfCell = new PdfPCell();
        halfCell.addElement(new Paragraph(" ", reasonFont));
        halfCell.setBorder(PdfPCell.NO_BORDER);

        durationTable.addCell(durationCell);
        durationTable.addCell(underlineCell);
        durationTable.addCell(dayCell);
        durationTable.addCell(monthCell);
        durationTable.addCell(yearCell);
        durationTable.addCell(halfCell);
        return durationTable;
    }

    private PdfPTable createReasonTable(Font reasonFont) throws DocumentException {
        PdfPTable reasonTable = new PdfPTable(2);
        reasonTable.setWidthPercentage(100);
        reasonTable.setTotalWidth(new float[]{5f, 5f });

        PdfPCell reasonCell = new PdfPCell();
        reasonCell.addElement(new Paragraph(BackSideConfig.REASON, reasonFont));
        reasonCell.setBorder(PdfPCell.NO_BORDER);

        PdfPCell reasonInfoCell = new PdfPCell();
        reasonInfoCell.addElement(new Paragraph(" ", reasonFont));
        reasonInfoCell.setBorder(PdfPCell.BOTTOM);

        reasonTable.addCell(reasonCell);
        reasonTable.addCell(reasonInfoCell);
        return reasonTable;
    }

    private String dividesByTwo(int a){
        if (a%2==0){
            return "2";
        } else {
            return "1";
        }
    }

    private PdfPTable createMainTable(BaseFont baseFont) throws DocumentException {
        Font font = new Font(baseFont, 10);
        PdfPTable table = new PdfPTable(5);
        table.setSpacingBefore(10);
        table.setWidths(new float[]{FONT_SIZE_10, 6f, FONT_SIZE_14, 3f, 5f});
        table.setWidthPercentage(100);

        table.addCell(createCell("ПІБ студента", font, 10));
        table.addCell(createCell("Номер залікової книжки", font, 10));
        table.addCell(createAchievementsTable(font));
        table.addCell(createCell("Дата", font, 10));
        table.addCell(createCell("Підпис викладача", font, 10));

        return table;
    }

    private PdfPCell createAchievementsTable(Font font) throws DocumentException {
        PdfPCell coverForAchievements = new PdfPCell();
        coverForAchievements.setPadding(0);
        PdfPTable achievementsTable = new PdfPTable(3);
        achievementsTable.setWidths(new float[]{3f, 2f, 1.2f});
        achievementsTable.setWidthPercentage(100);
        achievementsTable.addCell(createAchievementsCell("Оцінка", font, 3, PdfPCell.NO_BORDER, 25));
        achievementsTable.addCell(createCell("за національною шкалою", font, 0));
        achievementsTable.addCell(createCell("100-бальна шкала", font, 0));
        achievementsTable.addCell(createCell("ЄКТС", font, 0));

        coverForAchievements.addElement(achievementsTable);
        return coverForAchievements;
    }

    private PdfPTable createDataTable(BaseFont baseFont, StudentDegree student) throws DocumentException {
        Font font = new Font(baseFont, 10);
        PdfPTable table = new PdfPTable(5);
        table.setWidths(new float[]{FONT_SIZE_10, 6f, FONT_SIZE_14, 3f, 5f});
        table.setWidthPercentage(100);

        table.addCell(createCell(student.getStudent().getInitialsUkr(), font, 10));
        table.addCell(createCell(student.getRecordBookNumber(), font, 10));
        table.addCell(createInnerDataTable(baseFont));
        table.addCell(createCell(" ", font, 0));
        table.addCell(createCell(" ", font, 0));

        return table;
    }

    private PdfPCell createInnerDataTable(BaseFont baseFont) throws DocumentException {
        PdfPCell coverForAchievements = new PdfPCell();
        Font font = new Font(baseFont, 10);
        coverForAchievements.setPadding(0);
        PdfPTable achievementsTable = new PdfPTable(3);
        achievementsTable.setWidths(new float[]{3f, 2f, 1.2f});
        achievementsTable.setWidthPercentage(100);

        PdfPCell nationalCell = new PdfPCell();
        nationalCell.addElement(new Paragraph(" ", font));

        PdfPCell hundredCell = new PdfPCell();
        nationalCell.addElement(new Paragraph(" ", font));

        PdfPCell ectslCell = new PdfPCell();
        nationalCell.addElement(new Paragraph(" ", font));


        achievementsTable.addCell(nationalCell);
        achievementsTable.addCell(hundredCell);
        achievementsTable.addCell(ectslCell);

        coverForAchievements.addElement(achievementsTable);
        return coverForAchievements;
    }

    private PdfPCell createCell(String text, Font font, int paddingTop) {
        PdfPCell cell = new PdfPCell();
        Paragraph paragraph = new Paragraph(text, font);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(paragraph);
        cell.setPaddingTop(paddingTop);
        return cell;
    }

    private PdfPCell createAchievementsCell(String text, Font font, int colspan, int border, int fixedHeight) {
        PdfPCell achievementCell = createCell(text, font, 0);
        achievementCell.setColspan(colspan);
        achievementCell.setBorder(border);
        achievementCell.setFixedHeight(fixedHeight);
        return achievementCell;
    }

    private String getJavaTempDirectory() {
        return System.getProperty("java.io.tmpdir");
    }

    private String convertFullNameToIntials(String fullName){
        String[] nameParts = fullName.split(" ");
        char middleInitial = nameParts[1].charAt(0);
        char lastInitial = nameParts[2].charAt(0);
        return nameParts[0] + " " + middleInitial+"." + lastInitial+".";
    }
}
