package ua.edu.chdtu.deanoffice.service.document.report.exam;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Grade;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.service.GradeService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;
import ua.edu.chdtu.deanoffice.service.document.diploma.supplement.StudentSummary;
import ua.edu.chdtu.deanoffice.util.PersonUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static ua.edu.chdtu.deanoffice.util.DocumentUtil.getFileCreationDateAndTime;
import static ua.edu.chdtu.deanoffice.util.DocumentUtil.getJavaTempDirectory;

@Service
public class QualificationWorkReportService {
    private StudentGroupService studentGroupService;
    private GradeService gradeService;

    private final int ROWS_PER_PAGE = 21;

    private final float NUMBER_CELL_WIDTH = 0.3f;
    private final float PIP_CELL_WIDTH = 1.7f;
    private final float AVERAGE_MARK = 0.4f;
    private final float ZV_WIDTH = 0.4f;
    private final float GRADE_CELL = 1f;
    private final float NATIONAL_GRADE = 1f;
    private final float HUNDRED_GRADE = 1f;
    private final float PROTOKOL_NUMBER_WIDTH = 1f;
    private final float SIGNATURE_WIDTH = 0.6f;

    @Value(value = "classpath:fonts/arial/arial.ttf")
    private Resource ttf;

    @Autowired
    public QualificationWorkReportService(StudentGroupService studentGroupService, GradeService gradeService) {
        this.studentGroupService = studentGroupService;
        this.gradeService = gradeService;
    }

    public File createQualificationWorkStatementForGroup(int groupId) throws IOException, DocumentException {
        StudentGroup studentGroup = studentGroupService.getById(groupId);
        Document document = new Document(PageSize.A4, 28f, 28f, 28f, 28f);
        String filePath = getJavaTempDirectory() + "/" + "vidomist_qalificaciinoi_roboti" +
                studentGroup.getName() +
                getFileCreationDateAndTime() + ".pdf";
        File file = new File(filePath);
        PdfWriter.getInstance(document, new FileOutputStream(file));
        try {
            document.open();
            BaseFont baseFont = BaseFont.createFont(ttf.getURI().toString(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font font = new Font(baseFont, 14);
            Font boldFont = new Font(baseFont, 14, Font.BOLD);
            String facultyName = studentGroup.getSpecialization().getFaculty().getName();
            String groupName = studentGroup.getSpecialization().getDegree().getName().toUpperCase();
            document.add(createCenterAlignedParagraph("Черкаський державний технологічний університет", boldFont, 0));
            document.add(createCenterAlignedParagraph(
                    facultyName.substring(0, 1).toUpperCase() + facultyName.substring(1).toLowerCase(),
                    font, 0));
            document.add(createCenterAlignedParagraph("Відомість кваліфікаційної роботи " + groupName.toLowerCase() + "а", font, 0));
            document.add(createCenterAlignedParagraph("група " + studentGroup.getName(), boldFont, 0));
            document.add(createTableWithHeaders(baseFont));
            document.add(fillTable(baseFont, studentGroup));
            Paragraph textBottom = new Paragraph("Декан " + studentGroup.getSpecialization().getFaculty().getAbbr()
                    + "                                                             "
                    + PersonUtil.makeNameThenSurnameInCapital(studentGroup.getSpecialization().getFaculty().getDean()), font);
            textBottom.setSpacingBefore(50f);
            textBottom.setIndentationLeft(24f);
            document.add(textBottom);
        } finally {
            if (document != null)
                document.close();
        }
        return file;
    }

    private PdfPTable fillTable(BaseFont baseFont, StudentGroup studentGroup) throws DocumentException {
        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{NUMBER_CELL_WIDTH, PIP_CELL_WIDTH, AVERAGE_MARK, ZV_WIDTH, NATIONAL_GRADE - 0.5f, HUNDRED_GRADE - 0.5f, PROTOKOL_NUMBER_WIDTH, SIGNATURE_WIDTH});
        Font font = new Font(baseFont, 12);

        int count = 1, studentsCount = 0;
        List<StudentDegree> studentDegrees = studentGroup.getStudentDegrees();
        for (StudentDegree studentDegree : studentDegrees) {
            List<List<Grade>> grades = gradeService.getGradesByStudentDegreeIdWithSelective(studentDegree.getId());
            String grade = "";

            if (!isStudentDebtor(grades)) {
                StudentSummary studentSummary = new StudentSummary(studentDegree, grades);
                StudentSummary.StudentGradesSummary gradesStatistic = studentSummary.getStudentGradesSummary();
                grade = String.format("%.2f", gradesStatistic.getGradeAverage());
            }

            Student student = studentDegree.getStudent();
            table.addCell(createCell(count + "", font, 0));
            count++;
            String studentFullName =
                    student.getSurname() +
                    " " +
                    student.getName() +
                    " " +
                    student.getPatronimic();

            table.addCell(new PdfPCell(new Paragraph(studentFullName, font)));
            table.addCell(new PdfPCell(createCell(grade, font, 0)));

            for (int i = 0; i < 5; i++) {
                table.addCell(new PdfPCell(new Paragraph()));
            }

            if (studentsCount / ROWS_PER_PAGE == 1) {
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 8; j++) {
                        PdfPCell cell = new PdfPCell(createCell(" ", font, 0));
                            cell.setBorderColor(BaseColor.WHITE);
                        table.addCell(cell);
                    }
                }
                studentsCount = 0;
            }
            studentsCount++;
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

    private PdfPTable createTableWithHeaders(BaseFont baseFont) throws DocumentException {
        Font font = new Font(baseFont, 10);
        PdfPTable table = new PdfPTable(7);
        table.setSpacingBefore(10);
        table.setWidths(new float[]{NUMBER_CELL_WIDTH, PIP_CELL_WIDTH, AVERAGE_MARK, ZV_WIDTH, GRADE_CELL, PROTOKOL_NUMBER_WIDTH, SIGNATURE_WIDTH});
        table.setWidthPercentage(100);
        table.addCell(createCell("№ з/п", font, 0));
        table.addCell(createCell("Прізвище та ініціали студентів", font, 30));
        table.addCell(createCell("Середній бал", font, 0));
        table.addCell(createCell("ЗВ", font, 40));
        table.addCell(createGradeTable(font));
        table.addCell(createCell("Дата і номер протоколу екзаменаційної комісії", font, 0));
        table.addCell(createCell("Підпис зав.каф", font, 0));
        return table;
    }

    private PdfPCell createGradeTable(Font font) throws DocumentException {
        PdfPCell gradeCell = new PdfPCell();
        gradeCell.setPadding(0);
        PdfPTable gradeTable = new PdfPTable(2);
        gradeTable.setWidths(new float[]{NATIONAL_GRADE, HUNDRED_GRADE});
        gradeTable.setWidthPercentage(100);
        gradeTable.addCell(createGradeCell("Оцінка", font, 3,
                PdfPCell.NO_BORDER, 25));
        gradeTable.addCell(createCell("за традицій ною шкалою", font, 0));
        gradeTable.addCell(createCell("кількість балів за 100 бальною шкалою", font, 0));
        gradeCell.addElement(gradeTable);
        return gradeCell;
    }

    private PdfPCell createGradeCell(String text, Font font, int colspan, int border, int fixedHeight) {
        PdfPCell gradeCell = createCell(text, font, 0);
        gradeCell.setColspan(colspan);
        gradeCell.setBorder(border);
        gradeCell.setFixedHeight(fixedHeight);
        return gradeCell;
    }

    private PdfPCell createCell(String text, Font font, int paddingTop) {
        PdfPCell cell = new PdfPCell();
        Paragraph paragraph = new Paragraph(text, font);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(paragraph);
        cell.setPaddingTop(paddingTop);
        return cell;
    }

    private Paragraph createCenterAlignedParagraph(String text, Font font, int spacingAfter) {
        Paragraph element = new Paragraph(text, font);
        element.setAlignment(Element.ALIGN_CENTER);
        element.setSpacingAfter(spacingAfter);
        return element;
    }
}
