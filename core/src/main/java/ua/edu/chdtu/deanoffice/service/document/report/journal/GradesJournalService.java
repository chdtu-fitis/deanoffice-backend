package ua.edu.chdtu.deanoffice.service.document.report.journal;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;

import org.springframework.core.io.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

@Service
public class GradesJournalService {

    private StudentGroupService studentGroupService;
    private CourseForGroupService courseForGroupService;
    @Value(value = "classpath:fonts/arial/arial.ttf")
    private Resource ttf;

    public GradesJournalService(StudentGroupService studentGroupService, CourseForGroupService courseForGroupService){
        this.studentGroupService = studentGroupService;
        this.courseForGroupService = courseForGroupService;
    }

    private String getJavaTempDirectory() {
        return System.getProperty("java.io.tmpdir");
    }

    public File createStudentsListsPdf(int degreeId, int year, int facultyId) throws IOException, DocumentException {
        List<StudentGroup> studentGroups = studentGroupService.getGroupsByDegreeAndYear(degreeId, year, facultyId);
        if (studentGroups != null && studentGroups.size() != 0) {
            Document document = new Document(PageSize.A4, 28f, 28f, 28f, 28f);
            String filePath = getJavaTempDirectory() + "/" + "StudentyJurnalOtsinok-" + year +
                    "kurs-" + getFileCreationDateAndTime() + ".pdf";
            File file = new File(filePath);
            PdfWriter.getInstance(document, new FileOutputStream(file));
            try {
                document.open();
                document.add(addStudentsContent(studentGroups));
            } finally {
                if (document != null)
                    document.close();
            }
            return  file;
        }
        return null;
    }

    private PdfPTable addStudentsContent(List<StudentGroup> studentGroups) throws DocumentException, IOException {
        PdfPTable tableMain = new PdfPTable(2);
        tableMain.setLockedWidth(true);
        tableMain.setTotalWidth(425);
        PdfPCell pdfPCell1 = new PdfPCell();
        pdfPCell1.setBorder(0);
        PdfPCell pdfPCell2 = new PdfPCell();
        pdfPCell2.setBorder(0);
        PdfPTable table1 = new PdfPTable(2);
        table1.setWidths(new int[] {30,2});
        table1.setWidthPercentage(100);
        PdfPTable table2 = new PdfPTable(2);
        table2.setWidths(new int[] {30,2});
        table2.setWidthPercentage(100);

        addStudentsOnPdfTables(studentGroups, table1, table2);

        pdfPCell1.addElement(table1);
        pdfPCell1.setPadding(0);
        pdfPCell2.addElement(table2);
        pdfPCell2.setPadding(0);
        tableMain.addCell(pdfPCell1);
        tableMain.addCell(pdfPCell2);
        return tableMain;
    }

    private void addStudentsOnPdfTables(List<StudentGroup> studentGroups, PdfPTable table1, PdfPTable table2) throws DocumentException, IOException {
        BaseFont baseFont = BaseFont.createFont(ttf.getURI().getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font font = new Font(baseFont);
        Font boldFont = new Font(baseFont, 12, Font.BOLD);
        boolean oneOrTwo = true;
        int sumOfCellsInTheTable1 = 0;
        int sumOfCellsInTheTable2 = 0;
        for (StudentGroup studentGroup : studentGroups)  {
            List<StudentDegree> studentDegrees = studentGroup.getStudentDegrees();
            PdfPCell groupNameCell = new PdfPCell(new Phrase(studentGroup.getName(), font));
            groupNameCell.setFixedHeight(28);
            groupNameCell.setPadding(5);
            groupNameCell.setBorder(0);
            PdfPCell emptyCell = new PdfPCell();
            emptyCell.setFixedHeight(28);
            emptyCell.setBorder(0);
            if (oneOrTwo){
                sumOfCellsInTheTable1 = addCellToStudentsTable(table1, groupNameCell, emptyCell, sumOfCellsInTheTable1);
            } else {
                sumOfCellsInTheTable2 = addCellToStudentsTable(table2, groupNameCell, emptyCell, sumOfCellsInTheTable2);
            }
            for (StudentDegree studentDegree : studentDegrees){
                Phrase studentText = new Phrase(studentDegree.getStudent().getSurname() + " "
                        + studentDegree.getStudent().getName() + " "
                        + studentDegree.getStudent().getPatronimic(), font);
                PdfPCell studentCell = new PdfPCell(studentText);
                studentCell.setFixedHeight(28);
                studentCell.setBorderWidthRight(0);
                PdfPCell isContractCell = new PdfPCell();
                isContractCell.setFixedHeight(28);
                isContractCell.setBorderWidthLeft(0);
                if (studentDegree.getPayment() == Payment.CONTRACT){
                    isContractCell.addElement(new Phrase("к", boldFont));
                }
                if (oneOrTwo){
                    sumOfCellsInTheTable1 = addCellToStudentsTable(table1, studentCell, isContractCell, sumOfCellsInTheTable1);
                } else {
                    sumOfCellsInTheTable2 = addCellToStudentsTable(table2, studentCell, isContractCell, sumOfCellsInTheTable2);
                }
            }
            oneOrTwo = sumOfCellsInTheTable1 <= sumOfCellsInTheTable2;
        }
    }

    private int addCellToStudentsTable(PdfPTable table, PdfPCell cell1, PdfPCell cell2, int sumOfCellsInTheTable) throws DocumentException{
        table.addCell(cell1);
        table.addCell(cell2);
        return ++sumOfCellsInTheTable;
    }

    private String getFileCreationDateAndTime() {
        return new SimpleDateFormat(" dd-MM-yyyy HH-mm").format(new Date());
    }

    public File createCoursesListsPdf(int degreeId, int year, int facultyId) throws IOException, DocumentException{
        List<StudentGroup> studentGroups = studentGroupService.getGroupsByDegreeAndYear(degreeId, year, facultyId);
        if (studentGroups != null && studentGroups.size() != 0) {
            Document document = new Document(PageSize.A4, 5f, 5f, 28f, 28f);
            String filePath = getJavaTempDirectory() + "/" + "Predmeti-dlya-zhurnalu -" + year +
                    "kurs_" + getFileCreationDateAndTime() + ".pdf";
            File file = new File(filePath);
            PdfWriter.getInstance(document, new FileOutputStream(file));
            try {
                document.open();
                document.add(addCoursesOnTable(studentGroups, document, year));
            } finally {
                if (document != null)
                    document.close();
            }
            return  file;
        }
        return null;
    }

    private PdfPCell createPdfPCellForTableCoursesMain(){
        PdfPCell pdfPCell = new PdfPCell();
        pdfPCell.setBorder(0);
        pdfPCell.setPadding(0);
        return pdfPCell;
    }

    private PdfPTable createPdfPTableForCellsOfTableCoursesMain() throws DocumentException {
        PdfPTable pdfPTable = new PdfPTable(2);
        pdfPTable.setKeepTogether(true);
        pdfPTable.setWidths(new int[] {1,10});
        pdfPTable.setWidthPercentage(100);
        return  pdfPTable;
    }

    private PdfPTable addCoursesOnTable(List<StudentGroup> studentGroups, Document document, int year) throws DocumentException, IOException {
        BaseFont baseFont = BaseFont.createFont(ttf.getURI().getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font font1 = new Font(baseFont, 9);
        Font font2 = new Font(baseFont, 8);
        PdfPTable tableMain = new PdfPTable(6);
        tableMain.setLockedWidth(true);
        tableMain.setTotalWidth(562);
        for (StudentGroup studentGroup: studentGroups){
            PdfPCell cell = createPdfPCellForTableCoursesMain();
            PdfPTable table = createPdfPTableForCellsOfTableCoursesMain();
            PdfPCell emptyCell = new PdfPCell();
            emptyCell.setBorder(0);
            table.addCell(emptyCell);
            PdfPCell groupNameCell = new PdfPCell(new Phrase(studentGroup.getName(), font1));
            groupNameCell.setBorder(0);
            groupNameCell.setFixedHeight(14);
            table.addCell(groupNameCell);
            int semester = year * 2 - 1;
            for (int j = 0; j < 2; j++){
                table.addCell(emptyCell);
                PdfPCell semesterCell = new PdfPCell(new Phrase("Семестр " + semester, font1));
                semesterCell.setBorder(0);
                semesterCell.setFixedHeight(14);
                table.addCell(semesterCell);

                List<CourseForGroup> courseForGroups  = courseForGroupService.getCoursesForGroupBySemester(studentGroup.getId(), semester);
                SortCourseForGroup sortCourseForGroup = new SortCourseForGroup();
                courseForGroups.sort(sortCourseForGroup);
                for (CourseForGroup courseForGroup: courseForGroups){
                    PdfPCell knowledgeControl = new PdfPCell(new Phrase(
                            getKnowledgeControlNameById(courseForGroup.getCourse().getKnowledgeControl().getId()), font2));
                    knowledgeControl.setFixedHeight(28);
                    knowledgeControl.setPadding(0);
                    knowledgeControl.setHorizontalAlignment(Element.ALIGN_CENTER);
                    knowledgeControl.setRotation(270);
                    table.addCell(knowledgeControl);
                    String courseName = courseForGroup.getCourse().getCourseName().getName();
                    PdfPCell courseNameCell = new PdfPCell(new Phrase(courseName, courseName.length()<35 ? font1 : font2));
                    courseNameCell.setFixedHeight(28);
                    table.addCell(courseNameCell);
                }
                semester++;
            }
            cell.addElement(table);
            tableMain.addCell(cell);
            document.newPage();
        }
        return tableMain;
    }

    private String getKnowledgeControlNameById(int id){
        switch (id){
            case 1: return "іспит";
            case 2: return "залік";
            case 3: return "КР";
            case 4: return "КП";
            case 5: return "ДЗ";
            case 6: return "ДІ";
            case 7: return "атест.";
            case 8: return "практ.";
            case 9: return "практ.";
        }
        return null;
    }

    private class SortCourseForGroup implements Comparator<CourseForGroup>{
        public int compare(CourseForGroup course1, CourseForGroup course2) {
            int courseId1 = course1.getCourse().getKnowledgeControl().getId();
            int courseId2 = course2.getCourse().getKnowledgeControl().getId();
            if (courseId1 == courseId2) {
                return 0;
            }
            if (courseId1 == 2 && courseId2 != 2) {
                return -1;
            }
            if (courseId1 > courseId2) {
                return 1;
            }
            else {
                return -1;
            }
        }
    }

}
