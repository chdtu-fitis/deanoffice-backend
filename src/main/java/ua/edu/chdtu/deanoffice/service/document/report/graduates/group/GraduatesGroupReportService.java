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
import ua.edu.chdtu.deanoffice.entity.Grade;
import ua.edu.chdtu.deanoffice.entity.Specialization;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.service.GradeService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;
import ua.edu.chdtu.deanoffice.service.document.diploma.supplement.StudentSummary;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ua.edu.chdtu.deanoffice.util.DocumentUtil.getFileCreationDateAndTime;
import static ua.edu.chdtu.deanoffice.util.DocumentUtil.getJavaTempDirectory;

@Service
public class GraduatesGroupReportService {
    private StudentGroupService studentGroupService;
    private GradeService gradeService;
    private final int ROWS_PER_PAGE = 15;
    private final int POSSIBLE_ROWS_ON_SECOND_PAGE = 20;
    private final float NUMBER_CELL_WIDTH = 1f;
    private final float PIP_CELL_WIDTH = 8.5f;
    private final float STUDENT_BOOK_CELL_WIDTH = 3f;
    @Value(value = "classpath:fonts/arial/arial.ttf")
    private Resource ttf;

    public GraduatesGroupReportService(StudentGroupService studentGroupService, GradeService gradeService) {
        this.studentGroupService = studentGroupService;
        this.gradeService = gradeService;
    }

    public File createGraduatesReportForGroupPdf(int groupId) throws IOException, DocumentException {
        StudentGroup group = studentGroupService.getById(groupId);
        Document document = new Document(PageSize.A4, 28f, 28f, 28f, 28f);
        String filePath = getJavaTempDirectory() + "/" + "vidomist_vypusknykiv_" + group.getName() + "_" + getFileCreationDateAndTime() + ".pdf";
        File file = new File(filePath);
        PdfWriter.getInstance(document, new FileOutputStream(file));
        try {
            document.open();
            BaseFont baseFont = BaseFont.createFont(ttf.getURI().getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font font = new Font(baseFont, 14);
            document.add(createCenterAlignedParagraph("ЧЕРКАСЬКИЙ ДЕРЖАВНИЙ ТЕХНОЛОГІЧНИЙ УНІВЕРСИТЕТ", font, 0));
            document.add(createCenterAlignedParagraph(group.getSpecialization().getFaculty().getName().toUpperCase(), font, 50));
            document.add(createCenterAlignedParagraph("ВІДОМІСТЬ", new Font(baseFont, 14, Font.BOLD), 0));
            document.add(createCenterAlignedParagraph("навчальних досягнень здобувачів вищої освіти,", font, 0));
            document.add(createCenterAlignedParagraph("які виконали усі вимоги навчального плану освітньої програми", font, 0));
            document.add(createCenterAlignedParagraph("за період навчання", font, 30));
            document.add(createGroupAttributeTable(font, createGroupAttributeBean(group)));
            document.add(createMainTable(baseFont));
            document.add(fillTable(baseFont, group));
            createAssignment(baseFont, document);
        } finally {
            if (document != null)
                document.close();
        }
        return file;
    }

    private Paragraph createCenterAlignedParagraph(String text, Font font, int spacingAfter) {
        Paragraph element = new Paragraph(text, font);
        element.setAlignment(Element.ALIGN_CENTER);
        element.setSpacingAfter(spacingAfter);
        return element;
    }

    private PdfPTable createGroupAttributeTable(Font font, GroupAttributeBean bean) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setTotalWidth(new float[]{1f, 3f});
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
            table.addCell(cell);
        }
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        return table;
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

    private PdfPTable createMainTable(BaseFont baseFont) throws DocumentException {
        Font font = new Font(baseFont, 10);
        PdfPTable table = new PdfPTable(4);
        table.setSpacingBefore(10);
        table.setWidths(new float[]{NUMBER_CELL_WIDTH, PIP_CELL_WIDTH, STUDENT_BOOK_CELL_WIDTH, 10f});
        table.setWidthPercentage(100);
        table.addCell(createCell("№ з/п", font, 20));
        table.addCell(createCell("ПІБ здобувача", font, 30));
        table.addCell(createCell("№ залікової книжки", font, 20));
        table.addCell(createAchievementsTable(font));
        return table;
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
        grades.addCell(createCell("Добре,       %", font, 0));
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

    private PdfPTable fillTable(BaseFont baseFont, StudentGroup group) throws DocumentException {
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{NUMBER_CELL_WIDTH, PIP_CELL_WIDTH, STUDENT_BOOK_CELL_WIDTH, 2.57f, 2.57f, 2.57f, 2.299f});
        Font font = new Font(baseFont, 12);
        int count = 1, studentsCount = 0;
        List<StudentDegree> studentDegrees = group.getStudentDegrees();
        for (StudentDegree studentDegree : studentDegrees) {
            List<List<Grade>> grades = gradeService.getGradesByStudentDegreeIdWithSelective(studentDegree.getId());
            if (!isStudentDebtor(grades)) {
                Student student = studentDegree.getStudent();
                table.addCell(createCell(count + "", font, 0));
                String studentName = student.getSurname() + " " + student.getName() + " " + student.getPatronimic();
                table.addCell(new PdfPCell(new Paragraph(studentName, font)));
                table.addCell(createCell(studentDegree.getRecordBookNumber(), font, 0));
                StudentSummary studentSummary = new StudentSummary(studentDegree, grades);
                StudentSummary.StudentGradesSummary gradesStatistic = studentSummary.getStudentGradesSummary();
                table.addCell(createCell(String.format("%.2f", gradesStatistic.getGrade5()), font, 0));
                table.addCell(createCell(String.format("%.2f", gradesStatistic.getGrade4()), font, 0));
                table.addCell(createCell(String.format("%.2f", gradesStatistic.getGrade3()), font, 0));
                table.addCell(createCell(String.format("%.2f", gradesStatistic.getGradeAverage()), font, 0));
                count++;
                studentsCount++;
            }
        }
        if (studentsCount >= ROWS_PER_PAGE) {
            for (int i = 0; i < POSSIBLE_ROWS_ON_SECOND_PAGE - (studentsCount - ROWS_PER_PAGE); i++) {
                table.addCell(createCell(count + "", font, 0));
                for (int j = 1; j < 7; j++) {
                    table.addCell(createCell("", font, 0));
                }
                count++;
            }
        }
        return table;
    }

    private boolean isStudentDebtor(List<List<Grade>> grades) {
        for (int i = 0; i < grades.size() - 1; i++) {
            for (Grade grade : grades.get(i)) {
                Integer points = grade.getPoints();
                if (points == null || points < 60) return true;
            }
        }
        return false;
    }

    private void createAssignment(BaseFont baseFont, Document document) throws DocumentException {
        Font font14 = new Font(baseFont, 14);
        Font font10 = new Font(baseFont, 10);
        Paragraph textParagraph = new Paragraph("   Декан факультету ______________ ______________", font14);
        textParagraph.setSpacingBefore(20f);
        textParagraph.setSpacingAfter(0);
        document.add(textParagraph);
        Paragraph signParagraph = new Paragraph("підпис", font10);
        signParagraph.setIndentationLeft(175f);
        signParagraph.add("                                ПІБ");
        document.add(signParagraph);
        Paragraph dateParagraph = new Paragraph("   «___»_______20 __ р.", font14);
        dateParagraph.setSpacingBefore(5f);
        document.add(dateParagraph);
        Paragraph mpParagraph = new Paragraph("           МП", font14);
        mpParagraph.setSpacingBefore(10f);
        document.add(mpParagraph);
    }
}
