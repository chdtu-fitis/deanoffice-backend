package ua.edu.chdtu.deanoffice.service.document.report.journal;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Payment;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;

import org.springframework.core.io.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class GradesJournalService {

    private StudentGroupService studentGroupService;
    @Value(value = "classpath:fonts/arial/arial.ttf")
    private Resource ttf;

    public GradesJournalService(StudentGroupService studentGroupService){
        this.studentGroupService = studentGroupService;
    }

    private String getJavaTempDirectory() {
        return System.getProperty("java.io.tmpdir");
    }

    public File createStudentsListsPdf(int degreeId, int year, int facultyId) throws IOException, DocumentException {
        List<StudentGroup> studentGroups = studentGroupService.getGroupsByDegreeAndYear(degreeId, year, facultyId);
        if (studentGroups != null && studentGroups.size() != 0) {
            Document document = new Document(PageSize.A4, 28f, 28f, 28f, 28f);
            String filePath = getJavaTempDirectory() + "/" + "StudentGroupsList_" + year +
                    "course_" + getFileCreationDateAndTime() + ".pdf";
            File file = new File(filePath);
            PdfWriter.getInstance(document, new FileOutputStream(file));
            BaseFont baseFont = BaseFont.createFont(ttf.getURI().getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font font = new Font(baseFont);
            document.open();
            document.add(addContent(studentGroups, font));
            document.close();
            return  file;
        }
        return null;
    }

    private PdfPTable addContent(List<StudentGroup> studentGroups, Font font) throws DocumentException, IOException {
        PdfPTable tableMain = new PdfPTable(2);
        tableMain.setLockedWidth(true);
        tableMain.setTotalWidth(425);
        //tableMain.setWidthPercentage(100);
        PdfPCell pdfPCell1 = new PdfPCell();
        pdfPCell1.setBorder(0);
        PdfPCell pdfPCell2 = new PdfPCell();
        pdfPCell2.setBorder(0);
        PdfPTable table1 = new PdfPTable(1);
        table1.setWidthPercentage(100);
        PdfPTable table2 = new PdfPTable(1);
        table2.setWidthPercentage(100);
        boolean oneOrTwo = true;
        int sumOfCellsInTheTable1 = 0;
        int sumOfCellsInTheTable2 = 0;
        for (StudentGroup studentGroup : studentGroups)  {
            List<StudentDegree> studentDegrees = studentGroup.getStudentDegrees();
            //List<Student> students = studentGroup.getActiveStudents();
            PdfPCell groupNameCell = new PdfPCell(new Phrase(studentGroup.getName(), font));
            groupNameCell.setFixedHeight(28);//setMinimumHeight(30);
            groupNameCell.setPadding(5);
            groupNameCell.setBorder(0);
            if (oneOrTwo){
                sumOfCellsInTheTable1 = addCellToTable(table1, groupNameCell, sumOfCellsInTheTable1);
            } else {
                sumOfCellsInTheTable2 = addCellToTable(table2, groupNameCell, sumOfCellsInTheTable2);
            }
            for (StudentDegree studentDegree : studentDegrees){
                Phrase studentText = new Phrase(studentDegree.getStudent().getSurname() + " "
                        + studentDegree.getStudent().getName() + " "
                        + studentDegree.getStudent().getPatronimic(), font);
                if (studentDegree.getPayment() == Payment.CONTRACT){
                    studentText.add(new Chunk(" K", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLDITALIC)));
                }
                PdfPCell studentCell = new PdfPCell(studentText);
                studentCell.setFixedHeight(28);//setMinimumHeight(30);
                if (oneOrTwo){
                    sumOfCellsInTheTable1 = addCellToTable(table1, studentCell, sumOfCellsInTheTable1);
                } else {
                    sumOfCellsInTheTable2 = addCellToTable(table2, studentCell, sumOfCellsInTheTable2);
                }
            }
            if (sumOfCellsInTheTable1 <= sumOfCellsInTheTable2){
                oneOrTwo = true;
            } else {
                oneOrTwo = false;
            }
        }

        pdfPCell1.addElement(table1);
        pdfPCell1.setPadding(0);
        pdfPCell2.addElement(table2);
        pdfPCell2.setPadding(0);
        tableMain.addCell(pdfPCell1);
        tableMain.addCell(pdfPCell2);
        return tableMain;
    }

    private int addCellToTable(PdfPTable table, PdfPCell cell, int sumOfCellsInTheTable) throws DocumentException{
            table.addCell(cell);
            return ++sumOfCellsInTheTable;
    }

    private String getFileCreationDateAndTime() {
        return new SimpleDateFormat(" dd-MM-yyyy HH-mm").format(new Date());
    }
}
