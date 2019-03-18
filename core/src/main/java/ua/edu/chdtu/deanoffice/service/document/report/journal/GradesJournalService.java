package ua.edu.chdtu.deanoffice.service.document.report.journal;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;

import org.springframework.core.io.Resource;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;
import ua.edu.chdtu.deanoffice.service.document.TemplateUtil;
import ua.edu.chdtu.deanoffice.service.document.diploma.supplement.DiplomaSupplementService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import static ua.edu.chdtu.deanoffice.service.document.DocumentIOService.TEMPLATES_PATH;
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.*;

@Service
public class GradesJournalService {
    private static final String TEMPLATE = TEMPLATES_PATH + "CourseList.docx";
    private static final Integer THE_LAST_CELL_OF_ROW = 6;
    private static Logger log = LoggerFactory.getLogger(DiplomaSupplementService.class);

    private DocumentIOService documentIOService;
    private static final int MAX_CHARS_NUMBER_IN_COURSE_NAME_FOR_BIGGER_FONT = 30;
  
    private StudentGroupService studentGroupService;
    private CourseForGroupService courseForGroupService;
    @Value(value = "classpath:fonts/arial/arial.ttf")
    private Resource ttf;

    public GradesJournalService(StudentGroupService studentGroupService,
                                CourseForGroupService courseForGroupService,
                                DocumentIOService documentIOService) {
        this.studentGroupService = studentGroupService;
        this.courseForGroupService = courseForGroupService;
        this.documentIOService = documentIOService;
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
            return file;
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
        table1.setWidths(new int[]{30, 2});
        table1.setWidthPercentage(100);
        PdfPTable table2 = new PdfPTable(2);
        table2.setWidths(new int[]{30, 2});
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
        for (StudentGroup studentGroup : studentGroups) {
            List<StudentDegree> studentDegrees = studentGroup.getStudentDegrees();
            PdfPCell groupNameCell = new PdfPCell(new Phrase(studentGroup.getName(), font));
            groupNameCell.setFixedHeight(28);
            groupNameCell.setPadding(5);
            groupNameCell.setBorder(0);
            PdfPCell emptyCell = new PdfPCell();
            emptyCell.setFixedHeight(28);
            emptyCell.setBorder(0);
            if (oneOrTwo) {
                sumOfCellsInTheTable1 = addCellToStudentsTable(table1, groupNameCell, emptyCell, sumOfCellsInTheTable1);
            } else {
                sumOfCellsInTheTable2 = addCellToStudentsTable(table2, groupNameCell, emptyCell, sumOfCellsInTheTable2);
            }
            for (StudentDegree studentDegree : studentDegrees) {
                Phrase studentText = new Phrase(studentDegree.getStudent().getSurname() + " "
                        + studentDegree.getStudent().getName() + " "
                        + studentDegree.getStudent().getPatronimic(), font);
                PdfPCell studentCell = new PdfPCell(studentText);
                studentCell.setFixedHeight(28);
                studentCell.setBorderWidthRight(0);
                PdfPCell isContractCell = new PdfPCell();
                isContractCell.setFixedHeight(28);
                isContractCell.setBorderWidthLeft(0);
                if (studentDegree.getPayment() == Payment.CONTRACT) {
                    isContractCell.addElement(new Phrase("к", boldFont));
                }
                if (oneOrTwo) {
                    sumOfCellsInTheTable1 = addCellToStudentsTable(table1, studentCell, isContractCell, sumOfCellsInTheTable1);
                } else {
                    sumOfCellsInTheTable2 = addCellToStudentsTable(table2, studentCell, isContractCell, sumOfCellsInTheTable2);
                }
            }
            oneOrTwo = sumOfCellsInTheTable1 <= sumOfCellsInTheTable2;
        }
    }

    private int addCellToStudentsTable(PdfPTable table, PdfPCell cell1, PdfPCell cell2, int sumOfCellsInTheTable) throws DocumentException {
        table.addCell(cell1);
        table.addCell(cell2);
        return ++sumOfCellsInTheTable;
    }

    private String getFileCreationDateAndTime() {
        return new SimpleDateFormat(" dd-MM-yyyy HH-mm").format(new Date());
    }

    public File createCoursesListsPdf(int degreeId, int year, int facultyId) throws IOException, DocumentException {
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
            return file;
        }
        return null;
    }

    private PdfPCell createPdfPCellForTableCoursesMain() {
        PdfPCell pdfPCell = new PdfPCell();
        pdfPCell.setBorder(0);
        pdfPCell.setPadding(0);
        return pdfPCell;
    }

    private PdfPTable createPdfPTableForCellsOfTableCoursesMain() throws DocumentException {
        PdfPTable pdfPTable = new PdfPTable(2);
        pdfPTable.setKeepTogether(true);
        pdfPTable.setWidths(new int[]{1, 10});
        pdfPTable.setWidthPercentage(100);
        return pdfPTable;
    }

    private PdfPTable addCoursesOnTable(List<StudentGroup> studentGroups, Document document, int year) throws DocumentException, IOException {
        BaseFont baseFont = BaseFont.createFont(ttf.getURI().getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font font1 = new Font(baseFont, 9);
        Font font2 = new Font(baseFont, 8);
        PdfPTable tableMain = new PdfPTable(6);
        tableMain.setLockedWidth(true);
        tableMain.setTotalWidth(562);
        int finishedCells = 0;
        for (StudentGroup studentGroup : studentGroups) {
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
            for (int j = 0; j < 2; j++) {
                table.addCell(emptyCell);
                PdfPCell semesterCell = new PdfPCell(new Phrase("Семестр " + semester, font1));
                semesterCell.setBorder(0);
                semesterCell.setFixedHeight(14);
                table.addCell(semesterCell);

                List<CourseForGroup> courseForGroups = courseForGroupService.getCoursesForGroupBySemester(studentGroup.getId(), semester);
                SortCourseForGroup sortCourseForGroup = new SortCourseForGroup();
                courseForGroups.sort(sortCourseForGroup);
                for (CourseForGroup courseForGroup : courseForGroups) {
                    PdfPCell knowledgeControl = new PdfPCell(new Phrase(
                            getKnowledgeControlNameById(courseForGroup.getCourse().getKnowledgeControl().getId()), font2));
                    knowledgeControl.setFixedHeight(28);
                    knowledgeControl.setPadding(0);
                    knowledgeControl.setHorizontalAlignment(Element.ALIGN_CENTER);
                    knowledgeControl.setRotation(270);
                    table.addCell(knowledgeControl);
                    String courseName = courseForGroup.getCourse().getCourseName().getName();
                    PdfPCell courseNameCell = new PdfPCell(new Phrase(courseName, courseName.length() < MAX_CHARS_NUMBER_IN_COURSE_NAME_FOR_BIGGER_FONT ? font1 : font2));
                    courseNameCell.setFixedHeight(28);
                    table.addCell(courseNameCell);
                }
                semester++;
            }
            cell.addElement(table);
            tableMain.addCell(cell);
            finishedCells++;
        }

        for (int i = 0; i < 6 - finishedCells % 6; i++) {
            PdfPCell emptyCellForTableMain = createPdfPCellForTableCoursesMain();
            tableMain.addCell(emptyCellForTableMain);
        }

        return tableMain;
    }

    private String getKnowledgeControlNameById(int id) {
        switch (id) {
            case 1:
                return "іспит";
            case 2:
                return "залік";
            case 3:
                return "КР";
            case 4:
                return "КП";
            case 5:
                return "ДЗ";
            case 6:
                return "ДІ";
            case 7:
                return "атест.";
            case 8:
                return "практ.";
            case 9:
                return "практ.";
        }
        return null;
    }

    private class SortCourseForGroup implements Comparator<CourseForGroup> {
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
            } else {
                return -1;
            }
        }
    }

    public synchronized File createCoursesListsDocx(int degreeId, int year, int facultyId) throws Docx4JException, IOException {
        List<StudentGroup> studentGroups = studentGroupService.getGroupsByDegreeAndYear(degreeId, year, facultyId);
        if (studentGroups != null && studentGroups.size() != 0) {
            return documentIOService.saveDocumentToTemp(prepareTemplate(TEMPLATE, studentGroups, year),
                    "Predmeti-dlya-zhurnalu -" + year +
                            "kurs_" + getFileCreationDateAndTime() + ".docx", FileFormatEnum.DOCX);
        }
        return null;
    }

    private WordprocessingMLPackage prepareTemplate(String templateName, List<StudentGroup> studentGroups, int year) throws IOException, Docx4JException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(templateName);
        formTable(template, studentGroups, year);
        return template;
    }

    private void formTable(WordprocessingMLPackage template, List<StudentGroup> studentGroups, int year) {
        Tbl tempTable = (Tbl) getAllElementsFromObject(template.getMainDocumentPart(), Tbl.class).get(0);
        if (tempTable == null) {
            return;
        }
        List<Object> patternTableRows = getAllElementsFromObject(tempTable, Tr.class);

        Tr patternGroupNameRow = (Tr) patternTableRows.get(0);
        Tr patternSemesterRow = (Tr) patternTableRows.get(1);
        Tr patternCourseRow = (Tr) patternTableRows.get(2);

        int numberRowBlocksForGroups = studentGroups.size() / THE_LAST_CELL_OF_ROW + ((studentGroups.size() % THE_LAST_CELL_OF_ROW != 0) ? 1 : 0);

        int rowToAddIndex = 3;
        for (int i = 0; i < numberRowBlocksForGroups; i++) {
            addRowToTable(tempTable, patternGroupNameRow, rowToAddIndex);
            rowToAddIndex++;
            int semester = year * 2 - 1;
            for (int j = 0; j < 2; j++) {
                addRowToTable(tempTable, patternSemesterRow, rowToAddIndex);
                rowToAddIndex++;

                int maximumNumberOfCourses = determineTheMaximumNumberOfCourses(studentGroups, semester, i);

                for (int k = 0; k < maximumNumberOfCourses; k++) {
                    addRowToTable(tempTable, patternCourseRow, rowToAddIndex);
                    rowToAddIndex++;
                }
                semester++;
            }
        }
        tempTable.getContent().remove(patternGroupNameRow);
        tempTable.getContent().remove(patternSemesterRow);
        tempTable.getContent().remove(patternCourseRow);

        fillTable(tempTable, studentGroups, year);
    }

    private void fillTable(Tbl tempTable, List<StudentGroup> studentGroups, int year) {
        List<Object> tableRows = getAllElementsFromObject(tempTable, Tr.class);

        int numberOfRow = 0;
        int numberOfCell = 0;
        int endOfGroup = 0;
        int count = 0;
        int maximumNumberOfCourses;

        for (StudentGroup studentGroup : studentGroups) {
            numberOfRow = numberOfRow - endOfGroup;
            endOfGroup = 0;
            replaceTextInCell(tableRows, numberOfRow, numberOfCell, "Name", studentGroup.getName());
            numberOfRow++;
            endOfGroup++;
            int semester = year * 2 - 1;

            for (int i = 0; i < 2; i++) {
                replaceTextInCell(tableRows, numberOfRow, numberOfCell, "Semester", "Семестер № " + String.valueOf(semester));
                numberOfRow++;
                endOfGroup++;
                List<CourseForGroup> courseForGroups = courseForGroupService.getCoursesForGroupBySemester(studentGroup.getId(), semester);
                SortCourseForGroup sortCourseForGroup = new SortCourseForGroup();
                courseForGroups.sort(sortCourseForGroup);
                maximumNumberOfCourses = determineTheMaximumNumberOfCourses(studentGroups, semester, count);

                for (int j = 0; j < maximumNumberOfCourses; j++) {
                    replaceTextInCell(tableRows, numberOfRow, numberOfCell * 2, "kc", j >= courseForGroups.size() ? "" : getKnowledgeControlNameById(courseForGroups.get(j).getCourse().getKnowledgeControl().getId()));
                    replaceTextInCell(tableRows, numberOfRow, numberOfCell * 2 + 1, "Course", j >= courseForGroups.size() ? "" : courseForGroups.get(j).getCourse().getCourseName().getName());
                    numberOfRow++;
                    endOfGroup++;
                }
                semester++;
            }
            numberOfCell++;

            if (numberOfCell == THE_LAST_CELL_OF_ROW) {
                numberOfCell = 0;
                numberOfRow += 1 + 2 + determineTheMaximumNumberOfCourses(studentGroups, year * 2 - 1, count) +
                        determineTheMaximumNumberOfCourses(studentGroups, year * 2, count);
                count++;
            }
        }

        if (studentGroups.size() % 6 != 0) {
            addEmptyCellInTable(studentGroups, tableRows, numberOfRow, endOfGroup);
        }
    }

    private void replaceTextInCell(List<Object> tableRows, int numberOfRow, int numberOfCell, String patternText, String text) {
        Tr row = (Tr) tableRows.get(numberOfRow);
        List<Object> cellsOfRow = getAllElementsFromObject(row, Tc.class);
        Tc cell = (Tc) cellsOfRow.get(numberOfCell);
        Map<String, String> replace = new HashMap<>();
        replace.put(patternText, text);
        replaceInCell(cell, replace);
    }

    private void addEmptyCellInTable(List<StudentGroup> studentGroups, List<Object> tableRows,
                                     int numberOfRow, int endOfGroup) {
        numberOfRow = numberOfRow - endOfGroup;
        for (int j = numberOfRow; j < numberOfRow + endOfGroup; j++) {
            Tr row = (Tr) tableRows.get(j);
            List<Object> cellsOfRow = getAllElementsFromObject(row, Tc.class);

            for (int i = studentGroups.size() % THE_LAST_CELL_OF_ROW * cellsOfRow.size() / THE_LAST_CELL_OF_ROW; i < cellsOfRow.size(); i++) {
                Tc cell = (Tc) cellsOfRow.get(i);
                TemplateUtil.emptyTableCell(cell);
            }
        }
    }

    private int determineTheMaximumNumberOfCourses(List<StudentGroup> studentGroups, int semester, int currentRowOfGroups) {
        currentRowOfGroups = (currentRowOfGroups + 1) * THE_LAST_CELL_OF_ROW;
        int max = 0;

        for (int i = currentRowOfGroups - THE_LAST_CELL_OF_ROW; i < currentRowOfGroups; i++) {
            try {
                StudentGroup studentGroup = studentGroups.get(i);
                List<CourseForGroup> courseForGroups = courseForGroupService.getCoursesForGroupBySemester(studentGroup.getId(), semester);

                if (max < courseForGroups.size()) {
                    max = courseForGroups.size();
                }
            } catch (Exception e) {
                return max;
            }
        }

        return max;
    }
}
