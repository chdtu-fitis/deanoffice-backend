package ua.edu.chdtu.deanoffice.service.document.report.qualificationstatement;

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
import ua.edu.chdtu.deanoffice.service.StudentGroupService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static ua.edu.chdtu.deanoffice.util.DocumentUtil.getFileCreationDateAndTime;
import static ua.edu.chdtu.deanoffice.util.DocumentUtil.getJavaTempDirectory;

@Service
public class QualificationWorkService {
    private StudentGroupService studentGroupService;

    private final int ROWS_PER_PAGE = 10;
    private final int POSSIBLE_ROWS_ON_SECOND_PAGE = 20;

    private final float NUMBER_CELL_WIDTH = 1f;
    private final float PIP_CELL_WIDTH = 8.5f;
    private final float AVERAGE_MARK = 1.7f;
    private final float ZV_WIDTH = 2f;
    private final float PROTOKOL_NUMBER_WIDTH = 4.5f;
    private final float SIGNATURE_WIDTH = 3f;

    @Value(value = "classpath:fonts/arial/arial.ttf")
    private Resource ttf;

    @Autowired
    public QualificationWorkService(StudentGroupService studentGroupService) {
        this.studentGroupService = studentGroupService;
    }

    public File createQualificationWorkStatementForGroup(int groupId) throws IOException, DocumentException {
        StudentGroup studentGroup = studentGroupService.getById(groupId);
        Document document = new Document(PageSize.A4, 10f, 28f, 28f, 28f);
        String filePath = getJavaTempDirectory() + "/" + "vidomist_qalificaciinoi_roboti" +
                studentGroup.getName() +
                getFileCreationDateAndTime() + ".pdf";
        File file = new File(filePath);
        PdfWriter.getInstance(document, new FileOutputStream(file));
        try {
            document.open();
            BaseFont baseFont = BaseFont.createFont(ttf.getURI().getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font font = new Font(baseFont, 14);
            Font boldFont = new Font(baseFont, 14, Font.BOLD);
            String facultyName = studentGroup.getSpecialization().getFaculty().getName();
            String groupName = studentGroup.getSpecialization().getDegree().getName().toUpperCase();
            document.add(createCenterAlignedParagraph("Черкаський державний технологічний університет", boldFont, 0));
            document.add(createCenterAlignedParagraph(
                    facultyName.substring(0, 1).toUpperCase() + facultyName.substring(1).toLowerCase(),
                    font, 0));
            document.add(createCenterAlignedParagraph("Відомість кваліфікаційної роботи " + groupName.toLowerCase() + "а", font, 0));
            document.add(createCenterAlignedParagraph("группа " + studentGroup.getName(), boldFont, 0));
            document.add(createTable(baseFont));
            document.add(fillTable(baseFont, studentGroup));
        } finally {
            if (document != null)
                document.close();
        }
        return file;
    }

    private PdfPTable fillTable(BaseFont baseFont, StudentGroup studentGroup) throws DocumentException {
        PdfPTable table = new PdfPTable(9);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{NUMBER_CELL_WIDTH, PIP_CELL_WIDTH, AVERAGE_MARK, ZV_WIDTH, 3.7f, 3f, 2.5f, PROTOKOL_NUMBER_WIDTH, SIGNATURE_WIDTH});
        Font font = new Font(baseFont, 12);
        int count = 1, studentsCount = 0;
        List<StudentDegree> studentDegrees = studentGroup.getStudentDegrees();
        for (StudentDegree studentDegree: studentDegrees) {
            Student student = studentDegree.getStudent();
            table.addCell(createCell(count + "", font, 0));
            count++;
            String studentFullName = student.getSurname() + " " + student.getName() + " " + student.getPatronimic();
            table.addCell(new PdfPCell(new Paragraph(studentFullName, font)));
            table.addCell(new PdfPCell(new Paragraph("", font)));
            table.addCell(new PdfPCell(new Paragraph("", font)));
            table.addCell(new PdfPCell(new Paragraph("", font)));
            table.addCell(new PdfPCell(new Paragraph("", font)));
            table.addCell(new PdfPCell(new Paragraph("", font)));
            table.addCell(new PdfPCell(new Paragraph("", font)));
            table.addCell(new PdfPCell(new Paragraph("", font)));
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

    private PdfPTable createTable(BaseFont baseFont) throws DocumentException {
        Font font = new Font(baseFont, 10);
        PdfPTable table = new PdfPTable(7);
        table.setSpacingBefore(10);
        table.setWidths(new float[]{NUMBER_CELL_WIDTH, PIP_CELL_WIDTH, AVERAGE_MARK, ZV_WIDTH, 10f, PROTOKOL_NUMBER_WIDTH, SIGNATURE_WIDTH});
        table.setWidthPercentage(100);
        table.addCell(createCell("№ з/п", font, 0));
        table.addCell(createCell("Прізвище та ініціали студентів", font, 0));
        table.addCell(createCell("Середній бал", font, 0));
        table.addCell(createCell("ЗВ", font, 5));
        table.addCell(createGradeTable(font));
        table.addCell(createCell("Дата і номер протоколу державної екзаменаційної комісії", font, 0));
        table.addCell(createCell("Підпис зав.каф", font, 0));
        return table;
    }

    private PdfPCell createGradeTable(Font font) throws DocumentException {
        PdfPCell gradeCell = new PdfPCell();
        gradeCell.setPadding(0);
        PdfPTable gradeTable = new PdfPTable(3);
        gradeTable.setWidths(new float[]{3.7f, 3f, 2.5f});
        gradeTable.setWidthPercentage(100);
        gradeTable.addCell(createGradeCell("Оцінка", font, 4,
                PdfPCell.NO_BORDER, 25));
        gradeTable.addCell(createCell("за національною шкалою", font, 0));
        gradeTable.addCell(createCell("кількість балів за 100 бальною шкалою", font, 0));
        gradeTable.addCell(createCell("ECTS", font, 0));
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
