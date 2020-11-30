package ua.edu.chdtu.deanoffice.service.document.sessionreport;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.Payment;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.entity.TuitionForm;
import ua.edu.chdtu.deanoffice.service.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.*;

import static ua.edu.chdtu.deanoffice.util.SemesterUtil.getCurrentSemester;

@Service
public class SessionReportService {

    private final String TEMP_DiRECTORY = System.getProperty("java.io.tmpdir") + "/";
    private final String FILE_NAME = "session-report.xlsx";
    private final String SHEET_NAME = "Session report";
    private final String GLOBAL_FONT_NAME = "Arial Cyr";
    private final Short GLOBAL_FONT_COLOR = IndexedColors.BLACK.getIndex();
    private final Short GLOBAL_BORDERS_COLOR = IndexedColors.BLACK.getIndex();
    private final BorderStyle GLOBAL_BORDER_STYLE = BorderStyle.THIN;
    private final String BACHELOR_NAME_ENG_IN_DATABASE = "Bachelor";
    private final String MASTER_NAME_ENG_IN_DATABASE = "Master";

    private final String COLUMN_2 = "studentsOnSessionStart";
    private final String COLUMN_3 = "studentsOnSessionStartWhoHaveAcademicVacation";
    private final String COLUMN_4 = "studentsOnSessionStartWhoHaveNotAcademicVacation";
    private final String COLUMN_5 = "studentsWhoPassAllExamOnTime";
    private final String COLUMN_6 = "studentsThatDidNotComeToExamsWithAnImportantReason";
    private final String COLUMN_7 = "studentsThatDidNotComeToExamsWithANotImportantReason";
    private final String COLUMN_8 = "studentsThatPassedAllCourses";
    private final String COLUMN_9 = "studentsThatPassedAllCoursesOnlyFor5";
    private final String COLUMN_10 = "studentsThatPassedAllCoursesOnlyFor4Or5";
    private final String COLUMN_11 = "studentsThatPassedAllCoursesOnlyFor3Or4Or5";
    private final String COLUMN_12 = "studentsThatPassedAllCoursesOnlyFor3";
    private final String COLUMN_13 = "studentsInStudentGroupWhoHaveOneOrMoreBadGrades";
    private final String COLUMN_14 = "studentsInStudentGroupWhoHaveOneBadGrade";
    private final String COLUMN_15 = "studentsInStudentGroupWhoHaveTwoBadGrades";
    private final String COLUMN_16 = "studentsInStudentGroupWhoHaveThreeBadGrades";
    private final String COLUMN_17 = "absoluteSuccess";
    private final String COLUMN_18 = "studentsWhoHaveCompletedTheTheoreticalCourse";
    private final String COLUMN_19 = "studentsWhoStayForSecondYear";
    private final String COLUMN_20 = "studentsWhoWereExpelled";
    private final String COLUMN_21 = "studentsWhoEnterToNextCourseInExtramural";
    private final String COLUMN_22 = "studentsWhoEnterToNextCourseInFullTime";

    private final DegreeService degreeService;
    private final StudentGroupService studentGroupService;
    private final StudentDegreeService studentDegreeService;
    private final StudentExpelService studentExpelService;
    private final CurrentYearService currentYearService;
    private final FacultyService facultyService;

    @Autowired
    public SessionReportService(DegreeService degreeService,
                                StudentGroupService studentGroupService,
                                StudentDegreeService studentDegreeService,
                                StudentExpelService studentExpelService,
                                CurrentYearService currentYearService,
                                FacultyService facultyService) {
        this.degreeService = degreeService;
        this.studentGroupService = studentGroupService;
        this.studentDegreeService = studentDegreeService;
        this.studentExpelService = studentExpelService;
        this.currentYearService = currentYearService;
        this.facultyService = facultyService;
    }

    public File createSessionReportInXLSX(ApplicationUser user, int dayOfMonth, TuitionForm tuitionForm) throws Exception {
        LocalDate sessionStartDate;
        int currentYear = currentYearService.getYear();

        if (getCurrentSemester() == 0) {
            sessionStartDate = LocalDate.of(currentYear, 6, dayOfMonth);
        } else {
            sessionStartDate = LocalDate.of(currentYear - 1, 12, dayOfMonth);
        }

        try (OutputStream outputStream = new FileOutputStream(TEMP_DiRECTORY + FILE_NAME)) {
            Workbook wb = new XSSFWorkbook();

            Sheet sheet = wb.createSheet(SHEET_NAME);
            sheet.createFreezePane(1, 15);
            setWidthsForColumns(sheet);
            addMergeRegions(sheet);

            DataAboutSemesters dataAboutSemesters = createHead(sheet, user, sessionStartDate, tuitionForm);
            createBody(user.getFaculty().getId(), wb, 15, sessionStartDate, tuitionForm, dataAboutSemesters);

            wb.write(outputStream);
        }

        return new File(TEMP_DiRECTORY + FILE_NAME);
    }

    private void setWidthsForColumns(Sheet sheet) {
        sheet.setColumnWidth(0, 6014);//A - 22.71
        sheet.setColumnWidth(1, 2770);//B - 10.14
        sheet.setColumnWidth(2, 2150);//C - 7.71
        sheet.setColumnWidth(3, 2880);//D - 10.57
        sheet.setColumnWidth(4, 3000);//E - 11
        sheet.setColumnWidth(5, 2350);//F - 8.43
        sheet.setColumnWidth(6, 2350);//G - 8.43
        sheet.setColumnWidth(7, 3220);//H - 11.86
        sheet.setColumnWidth(8, 2100);//I - 7.43
        sheet.setColumnWidth(9, 2200);//J - 7.86
        sheet.setColumnWidth(10, 2130);//K - 7.57
        sheet.setColumnWidth(11, 2450);//L - 8.86
        sheet.setColumnWidth(12, 2500);//M - 9
        sheet.setColumnWidth(13, 1960);//N - 7
        sheet.setColumnWidth(14, 1800);//O - 6.29
        sheet.setColumnWidth(15, 1820);//P - 6.43
        sheet.setColumnWidth(16, 2300);//Q - 8.29
        sheet.setColumnWidth(17, 3100);//R - 11.43
        sheet.setColumnWidth(18, 2220);//S - 8
        sheet.setColumnWidth(19, 2750);//T - 10
        sheet.setColumnWidth(20, 2200);//U - 7.86
        sheet.setColumnWidth(21, 2150);//V - 7.71
    }

    private void addMergeRegions(Sheet sheet) {
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 21));
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 1, 21));
        sheet.addMergedRegion(new CellRangeAddress(4, 4, 1, 21));
        sheet.addMergedRegion(new CellRangeAddress(6, 6, 5, 6));
        sheet.addMergedRegion(new CellRangeAddress(6, 6, 7, 15));
        sheet.addMergedRegion(new CellRangeAddress(8, 8, 5, 6));
        sheet.addMergedRegion(new CellRangeAddress(8, 8, 7, 15));
        sheet.addMergedRegion(new CellRangeAddress(10, 10, 5, 6));
        sheet.addMergedRegion(new CellRangeAddress(10, 10, 7, 11));
        sheet.addMergedRegion(new CellRangeAddress(10, 10, 12, 15));
        sheet.addMergedRegion(new CellRangeAddress(10, 10, 20, 21));
        sheet.addMergedRegion(new CellRangeAddress(11, 11, 20, 21));
    }

    private DataAboutSemesters createHead(Sheet sheet, ApplicationUser user, LocalDate sessionStartDate, TuitionForm tuitionForm) {
        Workbook wb = sheet.getWorkbook();
        List<Cell> similarCells = new ArrayList<>();

        Row row1 = sheet.createRow(1);
        row1.setHeightInPoints((float) 22.5);
        Cell currentCell = createCellForHeadAndSetThisValue(row1, 1, "Денна, заочна форми навчання (підкреслити)");
        setCellStyleAndFontForCell(currentCell, wb, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, 16, true);


        Row row2 = sheet.createRow(2);
        row2.setHeightInPoints(15);
        currentCell = createCellForHeadAndSetThisValue(row2, 1, "Денна, заочна форми навчання (підкреслити)");
        setCellStyleAndFontForCell(currentCell, wb, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, 12, false);

        Row row3 = sheet.createRow(3);
        row3.setHeightInPoints(9);

        Row row4 = sheet.createRow(4);
        row4.setHeightInPoints(27);
        currentCell = createCellForHeadAndSetThisValue(row4, 1, "ВІДОМОСТІ ПРО РЕЗУЛЬТАТИ ЕКЗАМЕНАЦІЙНОЇ СЕСІЇ " +
                sessionStartDate.getYear() + "_/" + (sessionStartDate.getYear() + 1) + "_н.р.");
        setCellStyleAndFontForCell(currentCell, wb, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, 16, true);

        Row row5 = sheet.createRow(5);
        row5.setHeightInPoints(12);

        Row row6 = sheet.createRow(6);
        row6.setHeightInPoints((float) 19.5);
        currentCell = createCellForHeadAndSetThisValue(row6, 5, "Семестри");
        setCellStyleAndFontForCell(currentCell, wb, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, 12, false);
        DataAboutSemesters dataAboutSemesters = getCorrectSemesters(user, tuitionForm);
        currentCell = createCellForHeadAndSetThisValue(row6, 7, dataAboutSemesters.getSemestersForHead());
        setCellStyleAndFontForCell(currentCell, wb, HorizontalAlignment.LEFT, VerticalAlignment.BOTTOM, 10, false);

        Row row7 = sheet.createRow(7);
        row7.setHeightInPoints(12);

        Row row8 = sheet.createRow(8);
        row8.setHeightInPoints(24);
        similarCells.add(createCellForHeadAndSetThisValue(row8, 5, "Факультет"));
        similarCells.add(createCellForHeadAndSetThisValue(row8, 7, user.getFaculty().getName()));
        setCellStyleAndFontForCells(similarCells, wb, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, 14, false);
        similarCells.clear();

        Row row9 = sheet.createRow(9);
        row9.setHeightInPoints((float) 9.75);

        Row row10 = sheet.createRow(10);
        row10.setHeightInPoints((float) 12.75);
        currentCell = createCellForHeadAndSetThisValue(row10, 0, "Курс, спеціальність");
        setCellStyleAndFontForCell(currentCell, wb, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, 8, false);
        similarCells.addAll(
                Arrays.asList(
                        createCellForHeadAndSetThisValue(row10, 1, "Усього"),
                        createCellForHeadAndSetThisValue(row10, 2, "У т.ч."),
                        createCellForHeadAndSetThisValue(row10, 3, "Повинні"),
                        createCellForHeadAndSetThisValue(row10, 4, "Усього"),
                        createCellForHeadAndSetThisValue(row10, 5, "Не з'явилися"),
                        createCellForHeadAndSetThisValue(row10, 7, "Склали"),
                        createCellForHeadAndSetThisValue(row10, 12, "Дістали незадовільну оцінку"),
                        createCellForHeadAndSetThisValue(row10, 16, "Абсолютна"),
                        createCellForHeadAndSetThisValue(row10, 17, "Закінчили"),
                        createCellForHeadAndSetThisValue(row10, 18, "Залишено"),
                        createCellForHeadAndSetThisValue(row10, 19, "Відраховано"),
                        createCellForHeadAndSetThisValue(row10, 20, "Переведено на")
                )
        );

        Row row11 = sheet.createRow(11);
        row11.setHeightInPoints((float) 12.75);
        similarCells.addAll(
                Arrays.asList(
                        createCellForHeadAndSetThisValue(row11, 1, "студентів"),
                        createCellForHeadAndSetThisValue(row11, 2, "в академ."),
                        createCellForHeadAndSetThisValue(row11, 3, "складати"),
                        createCellForHeadAndSetThisValue(row11, 4, "допущено"),
                        createCellForHeadAndSetThisValue(row11, 5, "з поважної"),
                        createCellForHeadAndSetThisValue(row11, 6, "з неповаж-"),
                        createCellForHeadAndSetThisValue(row11, 7, "з усіх"),
                        createCellForHeadAndSetThisValue(row11, 8, "лише"),
                        createCellForHeadAndSetThisValue(row11, 9, "лише"),
                        createCellForHeadAndSetThisValue(row11, 10, "на змішані"),
                        createCellForHeadAndSetThisValue(row11, 11, "лише на"),
                        createCellForHeadAndSetThisValue(row11, 12, "усього"),
                        createCellForHeadAndSetThisValue(row11, 13, "одну"),
                        createCellForHeadAndSetThisValue(row11, 14, "дві"),
                        createCellForHeadAndSetThisValue(row11, 15, "три"),
                        createCellForHeadAndSetThisValue(row11, 16, "успішність"),
                        createCellForHeadAndSetThisValue(row11, 17, "теоретичний"),
                        createCellForHeadAndSetThisValue(row11, 18, "на другий"),
                        createCellForHeadAndSetThisValue(row11, 19, "за"),
                        createCellForHeadAndSetThisValue(row11, 20, "наступний курс")
                )
        );

        Row row12 = sheet.createRow(12);
        row12.setHeightInPoints((float) 12.75);
        similarCells.addAll(
                Arrays.asList(
                        createCellForHeadAndSetThisValue(row12, 1, "на початок"),
                        createCellForHeadAndSetThisValue(row12, 2, "відпустці"),
                        createCellForHeadAndSetThisValue(row12, 3, "екзамени"),
                        createCellForHeadAndSetThisValue(row12, 4, "до"),
                        createCellForHeadAndSetThisValue(row12, 5, "причини"),
                        createCellForHeadAndSetThisValue(row12, 6, "ної"),
                        createCellForHeadAndSetThisValue(row12, 7, "предметів"),
                        createCellForHeadAndSetThisValue(row12, 8, "на"),
                        createCellForHeadAndSetThisValue(row12, 9, "на 'добре'"),
                        createCellForHeadAndSetThisValue(row12, 10, "оцінки"),
                        createCellForHeadAndSetThisValue(row12, 11, "'задовільно'"),
                        createCellForHeadAndSetThisValue(row12, 12, "(сума"),
                        createCellForHeadAndSetThisValue(row12, 16, "гр.8"),
                        createCellForHeadAndSetThisValue(row12, 17, "курс"),
                        createCellForHeadAndSetThisValue(row12, 18, "рік"),
                        createCellForHeadAndSetThisValue(row12, 19, "результатами"),
                        createCellForHeadAndSetThisValue(row12, 20, "на"),
                        createCellForHeadAndSetThisValue(row12, 21, "у т.ч.")
                )
        );

        Row row13 = sheet.createRow(13);
        row13.setHeightInPoints((float) 12.75);
        similarCells.addAll(
                Arrays.asList(
                        createCellForHeadAndSetThisValue(row13, 1, "сесії"),
                        createCellForHeadAndSetThisValue(row13, 3, "(Гр.2-Гр.3)"),
                        createCellForHeadAndSetThisValue(row13, 4, "екзаменів"),
                        createCellForHeadAndSetThisValue(row13, 6, "причини"),
                        createCellForHeadAndSetThisValue(row13, 7, "навчального"),
                        createCellForHeadAndSetThisValue(row13, 8, "'відмінно'"),
                        createCellForHeadAndSetThisValue(row13, 9, "і 'відмінно'"),
                        createCellForHeadAndSetThisValue(row13, 12, "Гр.14,15,16)"),
                        createCellForHeadAndSetThisValue(row13, 16, "Гр.4"),
                        createCellForHeadAndSetThisValue(row13, 17, "навчання"),
                        createCellForHeadAndSetThisValue(row13, 19, "перевідних"),
                        createCellForHeadAndSetThisValue(row13, 20, "заочну"),
                        createCellForHeadAndSetThisValue(row13, 21, "умовно")
                )
        );

        Row row14 = sheet.createRow(14);
        row14.setHeightInPoints((float) 12.75);
        similarCells.addAll(
                Arrays.asList(
                        createCellForHeadAndSetThisValue(row14, 7, "плану"),
                        createCellForHeadAndSetThisValue(row14, 16, "%"),
                        createCellForHeadAndSetThisValue(row14, 19, "екзаменів"),
                        createCellForHeadAndSetThisValue(row14, 20, "ф.н.")
                )
        );
        setCellStyleAndFontForCells(similarCells, wb, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, 8, false);
        similarCells.clear();
        addBordersToHead(sheet);

        return dataAboutSemesters;
    }

    private DataAboutSemesters getCorrectSemesters(ApplicationUser user, TuitionForm tuitionForm) {
        int facultyId = user.getFaculty().getId();
        int previousSemester = getCurrentSemester() == 0 ? 1 : 0;
        int theLastBachelorSemester = degreeService.getMaxSemesterForDegreeByNameEngAndFacultyIdAndTuitionForm(BACHELOR_NAME_ENG_IN_DATABASE, facultyId, tuitionForm);
        int theLastMasterSemester = degreeService.getMaxSemesterForDegreeByNameEngAndFacultyIdAndTuitionForm(MASTER_NAME_ENG_IN_DATABASE, facultyId, tuitionForm);
        int totalMaxSemesterForCurrentDegree = theLastBachelorSemester;
        int StartFromSemester = 0;

        StringBuilder stringBuilder = new StringBuilder();
        Set<Integer> semesters = new TreeSet<>();
        List<String> degreeNames = Arrays.asList("Бакалаври: ", "; Магістри: ");

        for (String degreeName : degreeNames) {

            if (degreeName.equals(degreeNames.get(0)) && previousSemester == 1 && theLastBachelorSemester < 3) {
                continue;
            }

            if (degreeName.equals(degreeNames.get(1)) && previousSemester == 1 && theLastMasterSemester < 3) {
                break;
            }

            stringBuilder.append(degreeName);

            for (int semester = previousSemester + 1; semester <= totalMaxSemesterForCurrentDegree; semester += 2) {
                stringBuilder.append(semester);
                semesters.add(semester + StartFromSemester);
                stringBuilder.append(", ");
            }

            stringBuilder.deleteCharAt(stringBuilder.length() - 2);

            StartFromSemester += theLastBachelorSemester;
            totalMaxSemesterForCurrentDegree = theLastMasterSemester;
        }

        return new DataAboutSemesters(stringBuilder.toString(), semesters, theLastBachelorSemester);
    }

    private class DataAboutSemesters {
        private final String semestersForHead;
        private final Set<Integer> semesters;
        private final int maxSemesterForBachelor;

        public DataAboutSemesters(
                String semestersForHead,
                Set<Integer> semesters,
                int maxSemesterForBachelor
        ) {
            this.semestersForHead = semestersForHead;
            this.semesters = semesters;
            this.maxSemesterForBachelor = maxSemesterForBachelor;
        }

        public String getSemestersForHead() {
            return semestersForHead;
        }

        public Set<Integer> getSemesters() {
            return semesters;
        }

        public int getMaxSemesterForBachelor() {
            return maxSemesterForBachelor;
        }
    }

    private Cell createCellForHeadAndSetThisValue(Row row, int columnNumber, String text) {
        Cell cell = row.createCell(columnNumber);
        cell.setCellValue(text);

        return cell;
    }

    private void setCellStyleAndFontForCells(List<Cell> cells, Workbook wb, HorizontalAlignment horizontalAlignment,
                                             VerticalAlignment verticalAlignment, double fontHeight, boolean isBold) {
        for (Cell cell : cells) {
            setCellStyleAndFontForCell(cell, wb, horizontalAlignment,
                    verticalAlignment, fontHeight, isBold);
        }
    }

    private void setCellStyleAndFontForCell(Cell cell, Workbook wb, HorizontalAlignment horizontalAlignment,
                                            VerticalAlignment verticalAlignment, double fontHeight, boolean isBold) {
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(horizontalAlignment);
        cellStyle.setVerticalAlignment(verticalAlignment);
        cellStyle.setFillBackgroundColor(IndexedColors.WHITE.getIndex());

        Font font = wb.createFont();
        font.setFontName(GLOBAL_FONT_NAME);
        font.setFontHeightInPoints((short) fontHeight);
        font.setColor(GLOBAL_FONT_COLOR);
        font.setBold(isBold);

        cellStyle.setFont(font);

        cell.setCellStyle(cellStyle);
    }

    private void addBordersToHead(Sheet sheet) {
        setBorders(
                new CellRangeAddress(0, 9, 0, 0), sheet,
                false, false, true, false
        );

        setBorders(
                new CellRangeAddress(10, 14, 0, 0), sheet,
                true, false, true, true
        );

        setBorders(
                new CellRangeAddress(10, 14, 1, 1), sheet,
                true, true, true, true
        );

        setBorders(
                new CellRangeAddress(10, 14, 2, 2), sheet,
                true, true, true, true
        );

        setBorders(
                new CellRangeAddress(10, 14, 3, 3), sheet,
                true, true, true, true
        );

        setBorders(
                new CellRangeAddress(10, 14, 4, 4), sheet,
                true, true, true, true
        );

        setBorders(
                new CellRangeAddress(10, 10, 5, 6), sheet,
                true, true, true, true
        );

        setBorders(
                new CellRangeAddress(11, 14, 5, 5), sheet,
                true, true, true, true
        );

        setBorders(
                new CellRangeAddress(11, 14, 6, 6), sheet,
                true, true, true, true
        );

        setBorders(
                new CellRangeAddress(10, 10, 7, 11), sheet,
                true, true, true, true
        );

        setBorders(
                new CellRangeAddress(11, 14, 7, 7), sheet,
                true, true, true, true
        );

        setBorders(
                new CellRangeAddress(11, 14, 8, 8), sheet,
                true, true, true, true
        );

        setBorders(
                new CellRangeAddress(11, 14, 9, 9), sheet,
                true, true, true, true
        );

        setBorders(
                new CellRangeAddress(11, 14, 10, 10), sheet,
                true, true, true, true
        );

        setBorders(
                new CellRangeAddress(11, 14, 11, 11), sheet,
                true, true, true, true
        );

        setBorders(
                new CellRangeAddress(10, 10, 12, 15), sheet,
                true, true, true, true
        );

        setBorders(
                new CellRangeAddress(11, 14, 12, 12), sheet,
                true, true, true, true
        );

        setBorders(
                new CellRangeAddress(11, 14, 13, 13), sheet,
                true, true, true, true
        );

        setBorders(
                new CellRangeAddress(11, 14, 14, 14), sheet,
                true, true, true, true
        );

        setBorders(
                new CellRangeAddress(11, 14, 15, 15), sheet,
                true, true, true, true
        );

        setBorders(
                new CellRangeAddress(10, 12, 16, 16), sheet,
                true, true, true, true
        );

        setBorders(
                new CellRangeAddress(13, 14, 16, 16), sheet,
                true, true, true, true
        );

        setBorders(
                new CellRangeAddress(10, 14, 17, 17), sheet,
                true, true, true, true
        );

        setBorders(
                new CellRangeAddress(10, 14, 18, 18), sheet,
                true, true, true, true
        );

        setBorders(
                new CellRangeAddress(10, 14, 19, 19), sheet,
                true, true, true, true
        );

        setBorders(
                new CellRangeAddress(10, 11, 20, 21), sheet,
                true, true, true, true
        );

        setBorders(
                new CellRangeAddress(12, 14, 20, 20), sheet,
                true, true, true, true
        );

        setBorders(
                new CellRangeAddress(12, 14, 21, 21), sheet,
                true, true, true, true
        );

    }

    private void setBorders(CellRangeAddress cellAddresses, Sheet sheet,
                            boolean isTop, boolean isLeft, boolean isRight, boolean isBottom) {
        if (isTop) {
            RegionUtil.setBorderTop(GLOBAL_BORDER_STYLE, cellAddresses, sheet);
            RegionUtil.setTopBorderColor(GLOBAL_BORDERS_COLOR, cellAddresses, sheet);
        }

        if (isLeft) {
            RegionUtil.setBorderLeft(GLOBAL_BORDER_STYLE, cellAddresses, sheet);
            RegionUtil.setLeftBorderColor(GLOBAL_BORDERS_COLOR, cellAddresses, sheet);
        }

        if (isRight) {
            RegionUtil.setBorderRight(GLOBAL_BORDER_STYLE, cellAddresses, sheet);
            RegionUtil.setRightBorderColor(GLOBAL_BORDERS_COLOR, cellAddresses, sheet);
        }

        if (isBottom) {
            RegionUtil.setBorderBottom(GLOBAL_BORDER_STYLE, cellAddresses, sheet);
            RegionUtil.setBottomBorderColor(GLOBAL_BORDERS_COLOR, cellAddresses, sheet);
        }
    }

    private void createBody(
            int facultyId, Workbook workbook, int numberOfRow,
            LocalDate sessionStartDate, TuitionForm tuitionForm, DataAboutSemesters dataAboutSemesters) {
        Sheet sheet = workbook.getSheet(SHEET_NAME);

        int degreeId = degreeService.getByNameEng(BACHELOR_NAME_ENG_IN_DATABASE).getId();
        int degreeIdOfMaster = degreeService.getByNameEng(MASTER_NAME_ENG_IN_DATABASE).getId();
        boolean isMasterDegree = false;
        int previousSemester = getCurrentSemester() == 0 ? 1 : 0;
        int maxSemesterForBachelor = dataAboutSemesters.getMaxSemesterForBachelor();

        Map<String, List<Cell>> totalDataAboutBudgetStudents = createMapWithNeedLists();
        Map<String, List<Cell>> totalDataAboutContractStudents = createMapWithNeedLists();
        Set<Integer> semesters = dataAboutSemesters.getSemesters();

        for (Integer semester : semesters) {

            Cell numberOfCourse = sheet.createRow(numberOfRow).createCell(0);
            int yearOfStudy;

            if (semester > maxSemesterForBachelor) {
                semester = semester - maxSemesterForBachelor;

                if (previousSemester == 0) {
                    yearOfStudy = semester / 2 + 1;
                } else {
                    yearOfStudy = semester / 2;
                }
                numberOfCourse.setCellValue("Магістри курс " + yearOfStudy + ", сем. " + semester);

                if (!isMasterDegree) {
                    degreeId = degreeIdOfMaster;
                    isMasterDegree = true;
                }
            } else {
                if (previousSemester == 0) {
                    yearOfStudy = semester / 2 + 1;
                } else {
                    yearOfStudy = semester / 2;
                }
                numberOfCourse.setCellValue("Бакалаври курс " + yearOfStudy + ", сем. " + semester);
            }

            setCellStyleAndFontForCell(numberOfCourse, workbook, HorizontalAlignment.CENTER,
                    VerticalAlignment.CENTER, 8, false);

            List<StudentGroup> groups = studentGroupService.getGroupsByDegreeAndYearAndTuitionForm(degreeId, yearOfStudy, facultyId, tuitionForm);
            numberOfRow++;

            if (groups.size() == 0) {
                numberOfCourse.setCellValue("");
                continue;
            }

            numberOfRow = addDataForOneCourse(groups, numberOfRow, workbook, sessionStartDate, yearOfStudy - 1, semester,
                    totalDataAboutBudgetStudents, totalDataAboutContractStudents);
        }

        Row totalOnFacultyRowPart1 = sheet.createRow(numberOfRow);
        Row totalOnFacultyRowPart2 = sheet.createRow(numberOfRow + 1);
        Cell totalOnFacultyCell = totalOnFacultyRowPart1.createCell(0);
        sheet.addMergedRegion(new CellRangeAddress(numberOfRow, numberOfRow + 1, 0, 0));
        totalOnFacultyCell.setCellValue("Усього по факультету:");
        setCellStyleAndFontForCell(totalOnFacultyCell, workbook, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, 10, false);

        List<Cell> totalBudgetCells = new ArrayList<>();
        List<Cell> totalContractCells = new ArrayList<>();

        calculateTotalCountOfBudgedAndContractStudentsInColumnForOneCourse(
                totalOnFacultyRowPart1, totalOnFacultyRowPart2, 1,
                totalDataAboutBudgetStudents.get(COLUMN_2),
                totalDataAboutContractStudents.get(COLUMN_2),
                totalBudgetCells, totalContractCells
        );

        calculateTotalCountOfBudgedAndContractStudentsInColumnForOneCourse(
                totalOnFacultyRowPart1, totalOnFacultyRowPart2, 2,
                totalDataAboutBudgetStudents.get(COLUMN_3),
                totalDataAboutContractStudents.get(COLUMN_3),
                totalBudgetCells, totalContractCells
        );

        totalBudgetCells.add(createCellOfDifference(totalOnFacultyRowPart1, 3, 1,
                2, totalDataAboutBudgetStudents.get(COLUMN_4)));
        totalContractCells.add(createCellOfDifference(totalOnFacultyRowPart2, 3, 1,
                2, totalDataAboutContractStudents.get(COLUMN_4)));

        calculateTotalCountOfBudgedAndContractStudentsInColumnForOneCourse(
                totalOnFacultyRowPart1, totalOnFacultyRowPart2, 4,
                totalDataAboutBudgetStudents.get(COLUMN_5),
                totalDataAboutContractStudents.get(COLUMN_5),
                totalBudgetCells, totalContractCells
        );

        totalBudgetCells.add(totalOnFacultyRowPart1.createCell(5));
        totalContractCells.add(totalOnFacultyRowPart2.createCell(5));

        totalBudgetCells.add(createCellOfDifference(totalOnFacultyRowPart1, 6, 1,
                4, totalDataAboutBudgetStudents.get(COLUMN_7)));

        totalContractCells.add(createCellOfDifference(totalOnFacultyRowPart2, 6, 1,
                4, totalDataAboutContractStudents.get(COLUMN_7)));

        calculateTotalCountOfBudgedAndContractStudentsInColumnForOneCourse(
                totalOnFacultyRowPart1, totalOnFacultyRowPart2, 7,
                totalDataAboutBudgetStudents.get(COLUMN_8),
                totalDataAboutContractStudents.get(COLUMN_8),
                totalBudgetCells, totalContractCells
        );

        calculateTotalCountOfBudgedAndContractStudentsInColumnForOneCourse(
                totalOnFacultyRowPart1, totalOnFacultyRowPart2, 8,
                totalDataAboutBudgetStudents.get(COLUMN_9),
                totalDataAboutContractStudents.get(COLUMN_9),
                totalBudgetCells, totalContractCells
        );

        calculateTotalCountOfBudgedAndContractStudentsInColumnForOneCourse(
                totalOnFacultyRowPart1, totalOnFacultyRowPart2, 9,
                totalDataAboutBudgetStudents.get(COLUMN_10),
                totalDataAboutContractStudents.get(COLUMN_10),
                totalBudgetCells, totalContractCells
        );

        calculateTotalCountOfBudgedAndContractStudentsInColumnForOneCourse(
                totalOnFacultyRowPart1, totalOnFacultyRowPart2, 10,
                totalDataAboutBudgetStudents.get(COLUMN_11),
                totalDataAboutContractStudents.get(COLUMN_11),
                totalBudgetCells, totalContractCells
        );

        calculateTotalCountOfBudgedAndContractStudentsInColumnForOneCourse(
                totalOnFacultyRowPart1, totalOnFacultyRowPart2, 11,
                totalDataAboutBudgetStudents.get(COLUMN_12),
                totalDataAboutContractStudents.get(COLUMN_12),
                totalBudgetCells, totalContractCells
        );

        calculateTotalCountOfBudgedAndContractStudentsInColumnForOneCourse(
                totalOnFacultyRowPart1, totalOnFacultyRowPart2, 13,
                totalDataAboutBudgetStudents.get(COLUMN_14),
                totalDataAboutContractStudents.get(COLUMN_14),
                totalBudgetCells, totalContractCells
        );

        calculateTotalCountOfBudgedAndContractStudentsInColumnForOneCourse(
                totalOnFacultyRowPart1, totalOnFacultyRowPart2, 14,
                totalDataAboutBudgetStudents.get(COLUMN_15),
                totalDataAboutContractStudents.get(COLUMN_15),
                totalBudgetCells, totalContractCells
        );

        calculateTotalCountOfBudgedAndContractStudentsInColumnForOneCourse(
                totalOnFacultyRowPart1, totalOnFacultyRowPart2, 15,
                totalDataAboutBudgetStudents.get(COLUMN_16),
                totalDataAboutContractStudents.get(COLUMN_16),
                totalBudgetCells, totalContractCells
        );

        totalBudgetCells.add(createCellOfSum(totalOnFacultyRowPart1, 12,
                13, 14, 15,
                totalDataAboutBudgetStudents.get(COLUMN_13)));
        totalContractCells.add(createCellOfSum(totalOnFacultyRowPart2, 12,
                13, 14, 15,
                totalDataAboutContractStudents.get(COLUMN_13)));

        totalBudgetCells.add(createCellOfPercent(totalOnFacultyRowPart1, 16,
                7, 3));
        totalContractCells.add(createCellOfPercent(totalOnFacultyRowPart2, 16,
                7, 3));

        totalBudgetCells.add(totalOnFacultyRowPart1.createCell(17));
        totalContractCells.add(totalOnFacultyRowPart2.createCell(17));

        totalBudgetCells.add(totalOnFacultyRowPart1.createCell(18));
        totalContractCells.add(totalOnFacultyRowPart2.createCell(18));

        totalBudgetCells.add(totalOnFacultyRowPart1.createCell(19));
        totalContractCells.add(totalOnFacultyRowPart2.createCell(19));

        totalBudgetCells.add(totalOnFacultyRowPart1.createCell(20));
        totalContractCells.add(totalOnFacultyRowPart2.createCell(20));

        totalBudgetCells.add(totalOnFacultyRowPart1.createCell(21));
        totalContractCells.add(totalOnFacultyRowPart2.createCell(21));

        setCellStyleAndFontForCells(totalBudgetCells, workbook, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, 10, true);
        setCellStyleAndFontForCells(totalContractCells, workbook, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, 10, true);

        setCellColorForPaymentCells(FillPatternType.SOLID_FOREGROUND, IndexedColors.GREY_25_PERCENT, totalBudgetCells);
        setCellColorForPaymentCells(FillPatternType.NO_FILL, IndexedColors.WHITE, totalContractCells);

        numberOfRow += 3;

        Row paymentTypesRowPart1 = sheet.createRow(numberOfRow);
        Row paymentTypesRowPart2 = sheet.createRow(numberOfRow + 1);
        Cell paymentTypesCell = paymentTypesRowPart1.createCell(0);
        sheet.addMergedRegion(new CellRangeAddress(numberOfRow, numberOfRow + 1, 0, 0));
        paymentTypesCell.setCellValue("Форма фінансування");
        setCellStyleAndFontForCell(paymentTypesCell, workbook, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, 10, false);

        Cell budget = paymentTypesRowPart1.createCell(1);
        budget.setCellValue("бюджет");
        setCellStyleAndFontForCell(budget, workbook, HorizontalAlignment.LEFT, VerticalAlignment.CENTER, 10, false);

        Cell contract = paymentTypesRowPart2.createCell(1);
        contract.setCellValue("контракт");
        setCellStyleAndFontForCell(contract, workbook, HorizontalAlignment.LEFT, VerticalAlignment.CENTER, 10, false);

        Cell budgetColorCell = paymentTypesRowPart1.createCell(2);
        budgetColorCell.setCellStyle(workbook.createCellStyle());
        sheet.addMergedRegion(new CellRangeAddress(numberOfRow, numberOfRow, 2, 3));
        setCellColorForPaymentCells(FillPatternType.SOLID_FOREGROUND, IndexedColors.GREY_25_PERCENT, Collections.singletonList(budgetColorCell));

        Cell contractColorCell = paymentTypesRowPart2.createCell(2);
        contractColorCell.setCellStyle(workbook.createCellStyle());
        sheet.addMergedRegion(new CellRangeAddress(numberOfRow + 1, numberOfRow + 1, 2, 3));
        setCellColorForPaymentCells(FillPatternType.NO_FILL, IndexedColors.WHITE, Collections.singletonList(contractColorCell));

        numberOfRow += 3;

        Row deanRowPart1 = sheet.createRow(numberOfRow);
        Cell positionCell = deanRowPart1.createCell(0);
        positionCell.setCellValue("Декан");
        setBorders(new CellRangeAddress(numberOfRow, numberOfRow, 1, 9), sheet,
                false, false, false, true);

        Cell dean = deanRowPart1.createCell(10);
        sheet.addMergedRegion(new CellRangeAddress(numberOfRow, numberOfRow, 10, 15));
        dean.setCellValue(facultyService.getById(facultyId).getDean());

        Cell date = deanRowPart1.createCell(17);
        date.setCellValue("'___',_______________, " + currentYearService.getYear() + "p.");
        setCellStyleAndFontForCells(Arrays.asList(positionCell, dean, date), workbook, HorizontalAlignment.LEFT, VerticalAlignment.CENTER, 14, false);
        sheet.addMergedRegion(new CellRangeAddress(numberOfRow, numberOfRow, 17, 20));

        Row deanRowPart2 = sheet.createRow(++numberOfRow);
        Cell signature = deanRowPart2.createCell(5);
        signature.setCellValue("(Підпис)");
    }

    private int addDataForOneCourse(List<StudentGroup> groups, int numberOfRow, Workbook workbook, LocalDate sessionStartDate, int numberOfCourse, int semester,
                                    Map<String, List<Cell>> totalDataAboutBudgetStudents, Map<String, List<Cell>> totalDataAboutContractStudents) {
        Sheet sheet = workbook.getSheet(SHEET_NAME);

        Map<String, List<Cell>> dataAboutBudgetStudents = createMapWithNeedLists();
        Map<String, List<Cell>> dataAboutContractStudents = createMapWithNeedLists();

        for (StudentGroup studentGroup : groups) {
            Row dataAboutBudgetStudentsInGroup = sheet.createRow(numberOfRow);
            dataAboutBudgetStudentsInGroup.setHeightInPoints((float) 12.75);

            Row dataAboutContractStudentsInGroup = sheet.createRow(numberOfRow + 1);
            dataAboutContractStudentsInGroup.setHeightInPoints((float) 12.75);

            Cell groupName = dataAboutBudgetStudentsInGroup.createCell(0);
            sheet.addMergedRegion(new CellRangeAddress(numberOfRow, numberOfRow + 1, 0, 0));
            groupName.setCellValue(studentGroup.getName());
            setCellStyleAndFontForCell(groupName, workbook, HorizontalAlignment.LEFT, VerticalAlignment.CENTER, 10, true);

            calculateDataOnOneGroupByPayment(dataAboutBudgetStudentsInGroup, dataAboutBudgetStudents, Payment.BUDGET,
                    studentGroup.getId(), sessionStartDate, semester);
            calculateDataOnOneGroupByPayment(dataAboutContractStudentsInGroup, dataAboutContractStudents, Payment.CONTRACT,
                    studentGroup.getId(), sessionStartDate, semester);

            numberOfRow += 2;
        }

        List<Cell> budgedCells = new ArrayList<>();
        for (List<Cell> cells : dataAboutBudgetStudents.values()) {
            budgedCells.addAll(cells);
        }

        List<Cell> contractCells = new ArrayList<>();
        for (List<Cell> cells : dataAboutContractStudents.values()) {
            contractCells.addAll(cells);
        }

        setCellStyleAndFontForCells(budgedCells, workbook, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, 10, false);
        setCellStyleAndFontForCells(contractCells, workbook, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, 10, false);

        setCellColorForPaymentCells(FillPatternType.SOLID_FOREGROUND, IndexedColors.GREY_25_PERCENT, budgedCells);
        setCellColorForPaymentCells(FillPatternType.NO_FILL, IndexedColors.WHITE, contractCells);

        return calculateDataForOneYearOfStudy(numberOfRow, numberOfCourse, workbook,
                dataAboutBudgetStudents, dataAboutContractStudents,
                totalDataAboutBudgetStudents, totalDataAboutContractStudents);
    }

    private Map<String, List<Cell>> createMapWithNeedLists() {
        Map<String, List<Cell>> informationAboutStudentsByPayment = new HashMap<>();

        informationAboutStudentsByPayment.put(COLUMN_2, new ArrayList<>());
        informationAboutStudentsByPayment.put(COLUMN_3, new ArrayList<>());
        informationAboutStudentsByPayment.put(COLUMN_4, new ArrayList<>());
        informationAboutStudentsByPayment.put(COLUMN_5, new ArrayList<>());
        informationAboutStudentsByPayment.put(COLUMN_6, new ArrayList<>());
        informationAboutStudentsByPayment.put(COLUMN_7, new ArrayList<>());
        informationAboutStudentsByPayment.put(COLUMN_8, new ArrayList<>());
        informationAboutStudentsByPayment.put(COLUMN_9, new ArrayList<>());
        informationAboutStudentsByPayment.put(COLUMN_10, new ArrayList<>());
        informationAboutStudentsByPayment.put(COLUMN_11, new ArrayList<>());
        informationAboutStudentsByPayment.put(COLUMN_12, new ArrayList<>());
        informationAboutStudentsByPayment.put(COLUMN_13, new ArrayList<>());
        informationAboutStudentsByPayment.put(COLUMN_14, new ArrayList<>());
        informationAboutStudentsByPayment.put(COLUMN_15, new ArrayList<>());
        informationAboutStudentsByPayment.put(COLUMN_16, new ArrayList<>());
        informationAboutStudentsByPayment.put(COLUMN_17, new ArrayList<>());
        informationAboutStudentsByPayment.put(COLUMN_18, new ArrayList<>());
        informationAboutStudentsByPayment.put(COLUMN_19, new ArrayList<>());
        informationAboutStudentsByPayment.put(COLUMN_20, new ArrayList<>());
        informationAboutStudentsByPayment.put(COLUMN_21, new ArrayList<>());
        informationAboutStudentsByPayment.put(COLUMN_22, new ArrayList<>());

        return informationAboutStudentsByPayment;
    }

    private void calculateDataOnOneGroupByPayment(Row row, Map<String, List<Cell>> map, Payment payment,
                                                  int studentGroupId, LocalDate sessionStartDate, int semester) {
        int countStudentsOnSessionStartByPayment =
                studentDegreeService.getCountAllActiveStudentsByBeforeSessionStartDateAndStudentGroupIdAndPayment(studentGroupId, sessionStartDate, payment) +
                        studentExpelService.getCountStudentsInStudentGroupIdWhoExpelAfterSessionStartDateAndByPayment(studentGroupId, sessionStartDate, payment);
        createCellAndSetHereValueAndAddToList(row, 1, countStudentsOnSessionStartByPayment, map.get(COLUMN_2));

        int countStudentsOnSessionStartAndWhoHaveAcademicVacationByPayment =
                studentDegreeService.getCountAllActiveStudentsBeforeSessionStartDateWhoHaveAcademicVacationAndByStudentGroupIdAndPayment(
                        studentGroupId, sessionStartDate, payment) +
                        studentExpelService.getCountStudentsInStudentGroupWhoExpelAfterSessionStartDateAndHaveAcademicVacationAndByPayment(
                                studentGroupId, sessionStartDate, payment);
        createCellAndSetHereValueAndAddToList(
                row, 2, countStudentsOnSessionStartAndWhoHaveAcademicVacationByPayment, map.get(COLUMN_3));

        createCellOfDifference(row, 3, 1,
                2,map.get(COLUMN_4));

        Cell countStudentsWhoWasPassExamInTimeByPayment = row.createCell(4);
        countStudentsWhoWasPassExamInTimeByPayment.setCellFormula(row.getCell(3).getAddress().toString());
        map.get(COLUMN_5).add(countStudentsWhoWasPassExamInTimeByPayment);

        map.get(COLUMN_6).add(row.createCell(5));

        createCellOfDifference(row, 6, 1,
                4, map.get(COLUMN_7));

        int countStudentsThatPassedAllCoursesByPayment =
                studentDegreeService.getCountAllStudentsInStudentGroupWhoWerePassExamByGrades(
                        studentGroupId, semester, payment, Arrays.asList( 3, 4, 5)
                );

        createCellAndSetHereValueAndAddToList(
                row, 7, countStudentsThatPassedAllCoursesByPayment, map.get(COLUMN_8)
        );

        int countStudentsThatPassedAllCoursesOnlyFor5 =
                studentDegreeService.getCountAllStudentsInStudentGroupWhoWerePassExamByGrades(
                        studentGroupId, semester, payment, Arrays.asList(5)
                );

        createCellAndSetHereValueAndAddToList(
                row, 8, countStudentsThatPassedAllCoursesOnlyFor5, map.get(COLUMN_9)
        );

        int countStudentsThatPassedAllCoursesOnlyFor4Or5 =
                studentDegreeService.getCountAllStudentsInStudentGroupWhoWerePassExamByGrades(
                        studentGroupId, semester, payment, Arrays.asList(4, 5)
                ) - countStudentsThatPassedAllCoursesOnlyFor5;

        createCellAndSetHereValueAndAddToList(
                row, 9, countStudentsThatPassedAllCoursesOnlyFor4Or5, map.get(COLUMN_10)
        );

        int countStudentsThatPassedAllCoursesOnlyFor3 =
                studentDegreeService.getCountAllStudentsInStudentGroupWhoWerePassExamByGrades(
                        studentGroupId, semester, payment, Arrays.asList(3)
                );

        createCellAndSetHereValueAndAddToList(
                row, 11, countStudentsThatPassedAllCoursesOnlyFor3, map.get(COLUMN_12)
        );

        int countStudentsThatPassedAllCoursesOnlyFor3Or4Or5 =
                countStudentsThatPassedAllCoursesByPayment - countStudentsThatPassedAllCoursesOnlyFor5 -
                        countStudentsThatPassedAllCoursesOnlyFor4Or5 - countStudentsThatPassedAllCoursesOnlyFor3;

        createCellAndSetHereValueAndAddToList(
                row, 10, countStudentsThatPassedAllCoursesOnlyFor3Or4Or5, map.get(COLUMN_11)
        );

        int countAllStudentsInStudentGroupWhoHaveOneBadGrade =
                studentDegreeService.getCountAllStudentsInStudentGroupWhoHaveBadGradesByCountOfBadGrades(
                  studentGroupId, semester, payment, 1
                );

        createCellAndSetHereValueAndAddToList(
                row, 13, countAllStudentsInStudentGroupWhoHaveOneBadGrade,
                map.get(COLUMN_14)
        );

        int countAllStudentsInStudentGroupWhoHaveTwoBadGrades =
                studentDegreeService.getCountAllStudentsInStudentGroupWhoHaveBadGradesByCountOfBadGrades(
                        studentGroupId, semester, payment, 2
                );

        createCellAndSetHereValueAndAddToList(
                row, 14, countAllStudentsInStudentGroupWhoHaveTwoBadGrades,
                map.get(COLUMN_15)
        );

        int countAllStudentsInStudentGroupWhoHaveThreeBadGrades =
                studentDegreeService.getCountAllStudentsInStudentGroupWhoHaveBadGradesByCountOfBadGrades(
                        studentGroupId, semester, payment, 3
                );

        createCellAndSetHereValueAndAddToList(
                row, 15, countAllStudentsInStudentGroupWhoHaveThreeBadGrades,
                map.get(COLUMN_16)
        );

        createCellOfSum(row, 12,
                13, 14, 15,
                map.get(COLUMN_13));

        map.get(COLUMN_17).add(createCellOfPercent(row, 16,
                7, 3));

        Cell studentsWhoHaveCompletedTheTheoreticalCourse = row.createCell(17);
        map.get(COLUMN_18).add(studentsWhoHaveCompletedTheTheoreticalCourse);

        Cell studentsWhoStayForSecondYear = row.createCell(18);
        map.get(COLUMN_19).add(studentsWhoStayForSecondYear);

        Cell studentsWhoWereExpelled = row.createCell(19);
        map.get(COLUMN_20).add(studentsWhoWereExpelled);

        Cell studentsWhoEnterToNextCourseInExtramural = row.createCell(20);
        map.get(COLUMN_21).add(studentsWhoEnterToNextCourseInExtramural);

        Cell studentsWhoEnterToNextCourseInFullTime = row.createCell(21);
        map.get(COLUMN_22).add(studentsWhoEnterToNextCourseInFullTime);
    }

    private int calculateDataForOneYearOfStudy(int numberOfRow, int numberOfCourse, Workbook workbook,
                                   Map<String, List<Cell>> budgetMap, Map<String, List<Cell>> contractMap,
                                   Map<String, List<Cell>> totalDataAboutBudgetStudents,
                                   Map<String, List<Cell>> totalDataAboutContractStudents) {
        Sheet sheet = workbook.getSheet(SHEET_NAME);

        Row rowPart1 = sheet.createRow(numberOfRow);
        Row rowPart2 = sheet.createRow(numberOfRow + 1);

        Cell numberOfCourseCell = rowPart1.createCell(0);
        sheet.addMergedRegion(new CellRangeAddress(numberOfRow, numberOfRow + 1, 0, 0));
        numberOfCourseCell.setCellValue("По " + (numberOfCourse + 1) + " курсу");
        setCellStyleAndFontForCell(numberOfCourseCell, workbook, HorizontalAlignment.LEFT, VerticalAlignment.CENTER, 10, true);

        List<Cell> totalBudgetCells = new ArrayList<>();
        List<Cell> totalContractCells = new ArrayList<>();

        calculateTotalCountOfBudgedAndContractStudentsInColumnForOneCourse(
                rowPart1, rowPart2, 1,
                budgetMap.get(COLUMN_2),
                contractMap.get(COLUMN_2),
                totalBudgetCells, totalContractCells
        );
        totalDataAboutBudgetStudents.get(COLUMN_2).add(rowPart1.getCell(1));
        totalDataAboutContractStudents.get(COLUMN_2).add(rowPart2.getCell(1));

        calculateTotalCountOfBudgedAndContractStudentsInColumnForOneCourse(
                rowPart1, rowPart2, 2,
                budgetMap.get(COLUMN_3),
                contractMap.get(COLUMN_3),
                totalBudgetCells, totalContractCells
        );

        totalDataAboutBudgetStudents.get(COLUMN_3).add(rowPart1.getCell(2));
        totalDataAboutContractStudents.get(COLUMN_3).add(rowPart2.getCell(2));

        totalBudgetCells.add(createCellOfDifference(rowPart1, 3, 1,
                2, budgetMap.get(COLUMN_4)));

        totalContractCells.add(createCellOfDifference(rowPart2, 3, 1,
                2, contractMap.get(COLUMN_4)));

        calculateTotalCountOfBudgedAndContractStudentsInColumnForOneCourse(
                rowPart1, rowPart2, 4,
                budgetMap.get(COLUMN_5),
                contractMap.get(COLUMN_5),
                totalBudgetCells, totalContractCells
        );

        totalDataAboutBudgetStudents.get(COLUMN_5).add(rowPart1.getCell(4));
        totalDataAboutContractStudents.get(COLUMN_5).add(rowPart2.getCell(4));

        totalBudgetCells.add(rowPart1.createCell(5));
        totalContractCells.add(rowPart2.createCell(5));

        totalBudgetCells.add(createCellOfDifference(rowPart1, 6, 1,
                4, budgetMap.get(COLUMN_7)));

        totalContractCells.add(createCellOfDifference(rowPart2, 6, 1,
                4, contractMap.get(COLUMN_7)));

        calculateTotalCountOfBudgedAndContractStudentsInColumnForOneCourse(
                rowPart1, rowPart2, 7,
                budgetMap.get(COLUMN_8),
                contractMap.get(COLUMN_8),
                totalBudgetCells, totalContractCells
        );

        totalDataAboutBudgetStudents.get(COLUMN_8).add(rowPart1.getCell(7));
        totalDataAboutContractStudents.get(COLUMN_8).add(rowPart2.getCell(7));

        calculateTotalCountOfBudgedAndContractStudentsInColumnForOneCourse(
                rowPart1, rowPart2, 8,
                budgetMap.get(COLUMN_9),
                contractMap.get(COLUMN_9),
                totalBudgetCells, totalContractCells
        );

        totalDataAboutBudgetStudents.get(COLUMN_9).add(rowPart1.getCell(8));
        totalDataAboutContractStudents.get(COLUMN_9).add(rowPart2.getCell(8));

        calculateTotalCountOfBudgedAndContractStudentsInColumnForOneCourse(
                rowPart1, rowPart2, 9,
                budgetMap.get(COLUMN_10),
                contractMap.get(COLUMN_10),
                totalBudgetCells, totalContractCells
        );

        totalDataAboutBudgetStudents.get(COLUMN_10).add(rowPart1.getCell(9));
        totalDataAboutContractStudents.get(COLUMN_10).add(rowPart2.getCell(9));

        calculateTotalCountOfBudgedAndContractStudentsInColumnForOneCourse(
                rowPart1, rowPart2, 10,
                budgetMap.get(COLUMN_11),
                contractMap.get(COLUMN_11),
                totalBudgetCells, totalContractCells
        );

        totalDataAboutBudgetStudents.get(COLUMN_11).add(rowPart1.getCell(10));
        totalDataAboutContractStudents.get(COLUMN_11).add(rowPart2.getCell(10));

        calculateTotalCountOfBudgedAndContractStudentsInColumnForOneCourse(
                rowPart1, rowPart2, 11,
                budgetMap.get(COLUMN_12),
                contractMap.get(COLUMN_12),
                totalBudgetCells, totalContractCells
        );

        totalDataAboutBudgetStudents.get(COLUMN_12).add(rowPart1.getCell(11));
        totalDataAboutContractStudents.get(COLUMN_12).add(rowPart2.getCell(11));

        totalDataAboutBudgetStudents.get(COLUMN_13).add(rowPart1.getCell(12));
        totalDataAboutContractStudents.get(COLUMN_13).add(rowPart2.getCell(12));

        calculateTotalCountOfBudgedAndContractStudentsInColumnForOneCourse(
                rowPart1, rowPart2, 13,
                budgetMap.get(COLUMN_14),
                contractMap.get(COLUMN_14),
                totalBudgetCells, totalContractCells
        );

        totalDataAboutBudgetStudents.get(COLUMN_14).add(rowPart1.getCell(13));
        totalDataAboutContractStudents.get(COLUMN_14).add(rowPart2.getCell(13));

        calculateTotalCountOfBudgedAndContractStudentsInColumnForOneCourse(
                rowPart1, rowPart2, 14,
                budgetMap.get(COLUMN_15),
                contractMap.get(COLUMN_15),
                totalBudgetCells, totalContractCells
        );

        totalDataAboutBudgetStudents.get(COLUMN_15).add(rowPart1.getCell(14));
        totalDataAboutContractStudents.get(COLUMN_15).add(rowPart2.getCell(14));

        calculateTotalCountOfBudgedAndContractStudentsInColumnForOneCourse(
                rowPart1, rowPart2, 15,
                budgetMap.get(COLUMN_16),
                contractMap.get(COLUMN_16),
                totalBudgetCells, totalContractCells
        );

        totalDataAboutBudgetStudents.get(COLUMN_16).add(rowPart1.getCell(15));
        totalDataAboutContractStudents.get(COLUMN_16).add(rowPart2.getCell(15));

        totalBudgetCells.add(createCellOfSum(rowPart1, 12,
                13, 14, 15,
                budgetMap.get(COLUMN_13)));
        totalContractCells.add(createCellOfSum(rowPart2, 12,
                13, 14, 15,
                contractMap.get(COLUMN_13)));

        totalBudgetCells.add(createCellOfPercent(
                rowPart1, 16,
                7, 3
        ));

        totalContractCells.add(createCellOfPercent(
                rowPart2, 16,
                7, 3
        ));

        totalBudgetCells.add(rowPart1.createCell(17));
        totalContractCells.add(rowPart2.createCell(17));

        totalBudgetCells.add(rowPart1.createCell(18));
        totalContractCells.add(rowPart2.createCell(18));

        totalBudgetCells.add(rowPart1.createCell(19));
        totalContractCells.add(rowPart2.createCell(19));

        totalBudgetCells.add(rowPart1.createCell(20));
        totalContractCells.add(rowPart2.createCell(20));

        totalBudgetCells.add(rowPart1.createCell(21));
        totalContractCells.add(rowPart2.createCell(21));

        setCellStyleAndFontForCells(totalBudgetCells, workbook, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, 10, true);
        setCellStyleAndFontForCells(totalContractCells, workbook, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, 10, true);

        setCellColorForPaymentCells(FillPatternType.SOLID_FOREGROUND, IndexedColors.GREY_25_PERCENT, totalBudgetCells);
        setCellColorForPaymentCells(FillPatternType.NO_FILL, IndexedColors.WHITE, totalContractCells);

        return numberOfRow += 2;
    }

    private Cell createCellOfDifference(Row row, int columnNumberOfDifference,
                                        int columnNumberOfMinuend, int columnNumberOfSubtrahend,
                                        List<Cell> cells) {
        Cell differenceCell = row.createCell(columnNumberOfDifference);
        differenceCell.setCellFormula(
                row.getCell(columnNumberOfMinuend).getAddress().toString() +
                "-" +
                row.getCell(columnNumberOfSubtrahend).getAddress().toString()
        );
        cells.add(differenceCell);

        return differenceCell;
    }

    private Cell createCellOfSum(Row row, int columnNumberOfSum,
                         int columnNumberOfFirstTerm, int columnNumberOfSecondTerm, int columnNumberOfThirdTerm,
                         List<Cell> cells) {
        Cell sumCell = row.createCell(columnNumberOfSum);
        sumCell.setCellFormula(
                row.getCell(columnNumberOfFirstTerm).getAddress().toString() +
                "+" +
                row.getCell(columnNumberOfSecondTerm).getAddress().toString() +
                "+" +
                row.getCell(columnNumberOfThirdTerm).getAddress().toString()
        );
        cells.add(sumCell);

        return sumCell;
    }

    private Cell createCellOfPercent(Row row, int columnNumberOfPercent,
                                     int columnNumberOfDividend, int columnNumberOfDivider) {
        Cell percentCell = row.createCell(columnNumberOfPercent);
        percentCell.setCellFormula(
                row.getCell(columnNumberOfDividend).getAddress().toString() +
                "/" +
                row.getCell(columnNumberOfDivider).getAddress().toString() +
                "* 100"
        );

        return percentCell;
    }

    private void setCellColorForPaymentCells(FillPatternType fillPatternType, IndexedColors color, List<Cell> cells) {
        for (Cell cell : cells) {
            CellStyle cellStyle = cell.getCellStyle();
            cellStyle.setFillPattern(fillPatternType);
            cellStyle.setFillForegroundColor(color.getIndex());
        }
    }

    private void calculateTotalCountOfBudgedAndContractStudentsInColumnForOneCourse(
            Row budgetRow, Row contractRow, int columnNumber,
            List<Cell> budgedCells, List<Cell> contractCell, List<Cell> totalBudgetCells, List<Cell> totalContractCells
    ) {
        Cell totalCountBudgetStudents = budgetRow.createCell(columnNumber);
        createFormulaForCell(budgedCells, totalCountBudgetStudents);
        totalBudgetCells.add(totalCountBudgetStudents);

        Cell totalCountContractStudents = contractRow.createCell(columnNumber);
        createFormulaForCell(contractCell, totalCountContractStudents);
        totalContractCells.add(totalCountContractStudents);
    }

    private void createCellAndSetHereValueAndAddToList(Row row, int columnNumber, int value, List<Cell> list) {
        Cell cell = row.createCell(columnNumber);
        cell.setCellValue(value);
        list.add(cell);
    }

    private void createFormulaForCell(List<Cell> cells, Cell totalCountCell) {
        StringBuilder formulaForStudentsByPayment = new StringBuilder();

        for (Cell cell : cells) {
            formulaForStudentsByPayment.append(cell.getAddress().toString());
            formulaForStudentsByPayment.append("+");
        }

        if (formulaForStudentsByPayment.toString().length() > 2) {
            formulaForStudentsByPayment.deleteCharAt(formulaForStudentsByPayment.length() - 1);
        } else {
            return;
        }
        totalCountCell.setCellFormula(formulaForStudentsByPayment.toString());
    }

}
