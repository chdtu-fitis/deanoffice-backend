package ua.edu.chdtu.deanoffice.service.document.report.journal;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;

import org.springframework.core.io.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
            Document document = new Document(PageSize.A4, 0f, 0f, 0f, 0f);
            String filePath = getJavaTempDirectory() + "/" + "StudentGroupsList.pdf";
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

    private PdfPTable addContent(List<StudentGroup> studentGroups, Font font) throws DocumentException {
        PdfPTable tableMain = new PdfPTable(2);
        tableMain.setWidthPercentage(100);
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
        for (StudentGroup studentGroup : studentGroups) {
            List<Student> students = studentGroup.getActiveStudents();
            PdfPCell groupName = new PdfPCell(new Phrase(studentGroup.getName(), font));
            groupName.setMinimumHeight(30);
            groupName.setPadding(5);
            groupName.setBorder(0);
            if (oneOrTwo){
                sumOfCellsInTheTable1 = addCellToTable(table1, groupName, sumOfCellsInTheTable1);
            } else {
                sumOfCellsInTheTable2 = addCellToTable(table2, groupName, sumOfCellsInTheTable2);
            }
            for (Student student : students){
                PdfPCell studentCell = new PdfPCell(new Phrase(student.getSurname() + " " + student.getName() + " " + student.getPatronimic(), font));
                studentCell.setMinimumHeight(30);
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
            return sumOfCellsInTheTable++;
    }
}
