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
//    private Set<Integer> semesters;
//    private int maxSemesterForBachelor;

    private final DegreeService degreeService;
    private final StudentGroupService studentGroupService;
    private final StudentDegreeService studentDegreeService;
    private final StudentExpelService studentExpelService;
    private final CurrentYearService currentYearService;

    @Autowired
    public SessionReportService(DegreeService degreeService,
                                StudentGroupService studentGroupService,
                                StudentDegreeService studentDegreeService,
                                StudentExpelService studentExpelService,
                                CurrentYearService currentYearService) {
        this.degreeService = degreeService;
        this.studentGroupService = studentGroupService;
        this.studentDegreeService = studentDegreeService;
        this.studentExpelService = studentExpelService;
        this.currentYearService = currentYearService;
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

        List<Cell> totalCellsPerSemester = new ArrayList<>();
        Map<Integer, List<Cell>> totalInFaculty = new HashMap<>();
        Set<Integer> semesters = dataAboutSemesters.getSemesters();

        for (Integer semester : semesters) {

            Cell numberOfCourse = sheet.createRow(numberOfRow).createCell(0);
            int yearOfStudy;

            if (semester > maxSemesterForBachelor) {
                semester = semester - maxSemesterForBachelor;

                if (previousSemester == 0) {//я в 1-му семестрі
                    yearOfStudy = semester / 2 + 1;
                } else {//я в 2-му семестрі
                    yearOfStudy = semester / 2;
                }
                numberOfCourse.setCellValue("Магістри курс " + yearOfStudy + ", сем. " + semester);

                if (!isMasterDegree) {
                    degreeId = degreeIdOfMaster;
                    isMasterDegree = true;
                }
            } else {
                if (previousSemester == 0) {//я в 1-му семестрі
                    yearOfStudy = semester / 2 + 1;
                } else {//я в 2-му семестрі
                    yearOfStudy = semester / 2;
                }
                numberOfCourse.setCellValue("Бакалаври курс " + yearOfStudy + ", сем. " + semester);
            }

            setCellStyleAndFontForCell(numberOfCourse, workbook, HorizontalAlignment.CENTER,
                    VerticalAlignment.CENTER, 8, false);

            List<StudentGroup> groups = studentGroupService.getGroupsByDegreeAndYearAndTuitionForm(degreeId, yearOfStudy, facultyId, tuitionForm);
            numberOfRow++;
            numberOfRow = addDataForOneCourse(groups, numberOfRow, workbook, sessionStartDate, yearOfStudy - 1, semester);
        }

        Row totalOnFacultyRowPart1 = sheet.createRow(numberOfRow);
        Row totalOnFacultyRowPart2 = sheet.createRow(numberOfRow + 1);
        Cell totalOnFacultyCell = totalOnFacultyRowPart1.createCell(0);
        sheet.addMergedRegion(new CellRangeAddress(numberOfRow, numberOfRow + 1, 0, 0));
        totalOnFacultyCell.setCellValue("Усього по факультету:");
        setCellStyleAndFontForCell(totalOnFacultyCell, workbook, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, 10, false);

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
            //Cell budgetColorCellPart2 = paymentTypesRowPart1.createCell(3);
        budgetColorCell.setCellStyle(workbook.createCellStyle());
        sheet.addMergedRegion(new CellRangeAddress(numberOfRow, numberOfRow, 2, 3));
        setCellColorForPaymentCells(FillPatternType.SOLID_FOREGROUND, IndexedColors.GREY_25_PERCENT, Collections.singletonList(budgetColorCell));

        Cell contractColorCell = paymentTypesRowPart2.createCell(2);
        contractColorCell.setCellStyle(workbook.createCellStyle());
        sheet.addMergedRegion(new CellRangeAddress(numberOfRow + 1, numberOfRow + 1, 2, 3));
        setCellColorForPaymentCells(FillPatternType.NO_FILL, IndexedColors.WHITE, Collections.singletonList(contractColorCell));

        numberOfRow += 3;



    }

    private int addDataForOneCourse(List<StudentGroup> groups, int numberOfRow, Workbook workbook, LocalDate sessionStartDate, int numberOfCourse, int semester) {
        Sheet sheet = workbook.getSheet(SHEET_NAME);

        Map<String, List<Cell>> dataAboutBudgetStudents = createMapWithNeedLists();
        Map<String, List<Cell>> dataAboutContractStudents = createMapWithNeedLists();

        for (StudentGroup studentGroup : groups) {
            Row dataAboutOneStudentGroupPart1 = sheet.createRow(numberOfRow);
            dataAboutOneStudentGroupPart1.setHeightInPoints((float) 12.75);

            Row dataAboutOneStudentGroupPart2 = sheet.createRow(numberOfRow + 1);
            dataAboutOneStudentGroupPart2.setHeightInPoints((float) 12.75);

            Cell groupName = dataAboutOneStudentGroupPart1.createCell(0);
            sheet.addMergedRegion(new CellRangeAddress(numberOfRow, numberOfRow + 1, 0, 0));
            groupName.setCellValue(studentGroup.getName());
            setCellStyleAndFontForCell(groupName, workbook, HorizontalAlignment.LEFT, VerticalAlignment.CENTER, 10, true);

            calculateDataOnOneGroupByPayment(dataAboutOneStudentGroupPart1, dataAboutBudgetStudents, Payment.BUDGET,
                    studentGroup.getId(), sessionStartDate, semester);
            calculateDataOnOneGroupByPayment(dataAboutOneStudentGroupPart2, dataAboutContractStudents, Payment.CONTRACT,
                    studentGroup.getId(), sessionStartDate, semester);

            numberOfRow += 2;
        }

        List<Cell> budgedCells = new ArrayList<>();
        budgedCells.addAll(dataAboutBudgetStudents.get("studentsOnSessionStart"));
        budgedCells.addAll(dataAboutBudgetStudents.get("studentsOnSessionStartWhoHaveAcademicVacation"));
        budgedCells.addAll(dataAboutBudgetStudents.get("studentsOnSessionStartWhoHaveNotAcademicVacation"));
        budgedCells.addAll(dataAboutBudgetStudents.get("studentsWhoPassAllExamOnTime"));
        budgedCells.addAll(dataAboutBudgetStudents.get("studentsThatDidNotComeToExamsWithAnImportantReason"));
        budgedCells.addAll(dataAboutBudgetStudents.get("studentsThatDidNotComeToExamsWithANotImportantReason"));
        budgedCells.addAll(dataAboutBudgetStudents.get("studentsThatPassedAllCourses"));

        List<Cell> contractCells = new ArrayList<>();
        contractCells.addAll(dataAboutContractStudents.get("studentsOnSessionStart"));
        contractCells.addAll(dataAboutContractStudents.get("studentsOnSessionStartWhoHaveAcademicVacation"));
        contractCells.addAll(dataAboutContractStudents.get("studentsOnSessionStartWhoHaveNotAcademicVacation"));
        contractCells.addAll(dataAboutContractStudents.get("studentsWhoPassAllExamOnTime"));
        contractCells.addAll(dataAboutContractStudents.get("studentsThatDidNotComeToExamsWithAnImportantReason"));
        contractCells.addAll(dataAboutContractStudents.get("studentsThatDidNotComeToExamsWithANotImportantReason"));
        contractCells.addAll(dataAboutContractStudents.get("studentsThatPassedAllCourses"));

        setCellStyleAndFontForCells(budgedCells, workbook, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, 10, false);
        setCellStyleAndFontForCells(contractCells, workbook, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, 10, false);

        setCellColorForPaymentCells(FillPatternType.SOLID_FOREGROUND, IndexedColors.GREY_25_PERCENT, budgedCells);
        setCellColorForPaymentCells(FillPatternType.NO_FILL, IndexedColors.WHITE, contractCells);

        //-------------------------------------------------------------
        return calculateDataForOneYearOfStudy(numberOfRow, numberOfCourse, workbook,
                dataAboutBudgetStudents, dataAboutContractStudents);
    }

    private Map<String, List<Cell>> createMapWithNeedLists() {
        Map<String, List<Cell>> informationAboutStudentsByPayment = new HashMap<>();
        informationAboutStudentsByPayment.put("studentsOnSessionStart", new ArrayList<>());
        informationAboutStudentsByPayment.put("studentsOnSessionStartWhoHaveAcademicVacation", new ArrayList<>());
        informationAboutStudentsByPayment.put("studentsOnSessionStartWhoHaveNotAcademicVacation", new ArrayList<>());
        informationAboutStudentsByPayment.put("studentsWhoPassAllExamOnTime", new ArrayList<>());
        informationAboutStudentsByPayment.put("studentsThatDidNotComeToExamsWithAnImportantReason", new ArrayList<>());
        informationAboutStudentsByPayment.put("studentsThatDidNotComeToExamsWithANotImportantReason", new ArrayList<>());
        informationAboutStudentsByPayment.put("studentsThatPassedAllCourses", new ArrayList<>());

        return informationAboutStudentsByPayment;
    }

    private void calculateDataOnOneGroupByPayment(Row row, Map<String, List<Cell>> map, Payment payment,
                                                  int studentGroupId, LocalDate sessionStartDate, int semester) {
        int countBudgetStudentsOnSessionStart =
                studentDegreeService.getCountAllActiveStudentsByBeforeSessionStartDateAndStudentGroupIdAndPayment(studentGroupId, sessionStartDate, payment) +
                        studentExpelService.getCountStudentsInStudentGroupIdWhoExpelAfterSessionStartDateAndByPayment(studentGroupId, sessionStartDate, payment);
        createCellAndSetHereValueAndAddToList(row, 1, countBudgetStudentsOnSessionStart, map.get("studentsOnSessionStart"));

        int countBudgetStudentsOnSessionStartAndWhoHaveAcademicVacation =
                studentDegreeService.getCountAllActiveStudentsBeforeSessionStartDateWhoHaveAcademicVacationAndByStudentGroupIdAndPayment(
                        studentGroupId, sessionStartDate, payment) +
                        studentExpelService.getCountStudentsInStudentGroupWhoExpelAfterSessionStartDateAndHaveAcademicVacationAndByPayment(
                                studentGroupId, sessionStartDate, payment);
        createCellAndSetHereValueAndAddToList(
                row, 2, countBudgetStudentsOnSessionStartAndWhoHaveAcademicVacation, map.get("studentsOnSessionStartWhoHaveAcademicVacation"));

        Cell countBudgetStudentsOnSessionStartAndWhoHaveNotAcademicVacationCell = row.createCell(3);
        countBudgetStudentsOnSessionStartAndWhoHaveNotAcademicVacationCell.setCellFormula(
                row.getCell(1).getAddress().toString() +
                "-" +
                row.getCell(2).getAddress().toString()
        );
        map.get("studentsOnSessionStartWhoHaveNotAcademicVacation").add(countBudgetStudentsOnSessionStartAndWhoHaveNotAcademicVacationCell);
        //цей код може бути використаний для підрахунку кількості студентів, що вчасно все здали
            /*int countBudgetStudentsWhoWasPassExamInTime = studentDegreeService.getCountAllStudentsInStudentGroupWhoWerePassExamOnTime(
                    studentGroup.getId(), semester, payment
            );
            createCellAndSetHereValueAndAddToList(
                    dataAboutOneStudentGroupPart1, 4, countBudgetStudentsWhoWasPassExamInTime, budgetWhoPassAllExamOnTimeList);*/

        Cell countBudgetStudentsWhoWasPassExamInTime = row.createCell(4);
        countBudgetStudentsWhoWasPassExamInTime.setCellFormula(row.getCell(3).getAddress().toString());
        map.get("studentsWhoPassAllExamOnTime").add(countBudgetStudentsWhoWasPassExamInTime);

        map.get("studentsThatDidNotComeToExamsWithAnImportantReason").add(row.createCell(5));

        Cell countBudgetThatDidNotComeToExamWithNotImportantReasonCell = row.createCell(6);
        countBudgetThatDidNotComeToExamWithNotImportantReasonCell.setCellFormula(
                row.getCell(1).getAddress().toString() +
                "-" +
                row.getCell(4).getAddress().toString()
        );
        map.get("studentsThatDidNotComeToExamsWithANotImportantReason").add(countBudgetThatDidNotComeToExamWithNotImportantReasonCell);

        int countBudgetThatPassedAllCourses =
                studentDegreeService.getCountAllStudentsInGroupThatPassedAllExamBySemesterAndPayment(
                        studentGroupId, semester, payment
                );

        createCellAndSetHereValueAndAddToList(
                row, 7, countBudgetThatPassedAllCourses, map.get("studentsThatPassedAllCourses")
        );
    }

    private int calculateDataForOneYearOfStudy(int numberOfRow, int numberOfCourse, Workbook workbook,
                                               Map<String, List<Cell>> budgetMap, Map<String, List<Cell>> contractMap) {
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
                budgetMap.get("studentsOnSessionStart"),
                contractMap.get("studentsOnSessionStart"),
                totalBudgetCells, totalContractCells
        );

        calculateTotalCountOfBudgedAndContractStudentsInColumnForOneCourse(
                rowPart1, rowPart2, 2,
                budgetMap.get("studentsOnSessionStartWhoHaveAcademicVacation"),
                contractMap.get("studentsOnSessionStartWhoHaveAcademicVacation"),
                totalBudgetCells, totalContractCells
        );

        Cell budgetTotalCountStudentsWhoHaveNotAcademicVacation = rowPart1.createCell(3);
        budgetTotalCountStudentsWhoHaveNotAcademicVacation.setCellFormula(
                rowPart1.getCell(1).getAddress().toString() +
                "-" +
                rowPart1.getCell(2).getAddress().toString()
        );
        totalBudgetCells.add(budgetTotalCountStudentsWhoHaveNotAcademicVacation);

        Cell contractTotalCountStudentsWhoHaveNotAcademicVacation = rowPart2.createCell(3);
        contractTotalCountStudentsWhoHaveNotAcademicVacation.setCellFormula(
                rowPart2.getCell(1).getAddress().toString() +
                "-" +
                rowPart2.getCell(2).getAddress().toString()
        );
        totalContractCells.add(contractTotalCountStudentsWhoHaveNotAcademicVacation);

        calculateTotalCountOfBudgedAndContractStudentsInColumnForOneCourse(
                rowPart1, rowPart2, 4,
                budgetMap.get("studentsWhoPassAllExamOnTime"),
                contractMap.get("studentsWhoPassAllExamOnTime"),
                totalBudgetCells, totalContractCells
        );

        totalBudgetCells.add(rowPart1.createCell(5));
        totalContractCells.add(rowPart2.createCell(5));

        Cell totalBudgetThatDidNotComeToExamWithNotImportantReasonCell = rowPart1.createCell(6);
        totalBudgetThatDidNotComeToExamWithNotImportantReasonCell.setCellFormula(
                rowPart1.getCell(1).getAddress().toString() +
                "-" +
                rowPart1.getCell(4).getAddress().toString()
        );
        totalBudgetCells.add(totalBudgetThatDidNotComeToExamWithNotImportantReasonCell);

        Cell totalContractThatDidNotComeToExamWithNotImportantReasonCell = rowPart2.createCell(6);
        totalContractThatDidNotComeToExamWithNotImportantReasonCell.setCellFormula(
                rowPart2.getCell(1).getAddress().toString() +
                "-" +
                rowPart2.getCell(4).getAddress().toString()
        );
        totalContractCells.add(totalContractThatDidNotComeToExamWithNotImportantReasonCell);

        calculateTotalCountOfBudgedAndContractStudentsInColumnForOneCourse(
                rowPart1, rowPart2, 7,
                budgetMap.get("studentsThatPassedAllCourses"),
                contractMap.get("studentsThatPassedAllCourses"),
                totalBudgetCells, totalContractCells
        );

        setCellStyleAndFontForCells(totalBudgetCells, workbook, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, 10, true);
        setCellStyleAndFontForCells(totalContractCells, workbook, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, 10, true);

        setCellColorForPaymentCells(FillPatternType.SOLID_FOREGROUND, IndexedColors.GREY_25_PERCENT, totalBudgetCells);
        setCellColorForPaymentCells(FillPatternType.NO_FILL, IndexedColors.WHITE, totalContractCells);

        return numberOfRow += 2;
    }

    private void setCellColorForPaymentCells(FillPatternType fillPatternType, IndexedColors color, List<Cell> cells) {
        for (Cell cell : cells) {
            CellStyle cellStyle = cell.getCellStyle();
            cellStyle.setFillPattern(fillPatternType);
            cellStyle.setFillForegroundColor(color.getIndex());
        }
    }

    private void calculateTotalCountOfBudgedAndContractStudentsInColumnForOneCourse(
            Row rowPart1, Row rowPart2, int columnNumber,
            List<Cell> budgedCells, List<Cell> contractCell, List<Cell> totalBudgetCells, List<Cell> totalContractCells
    ) {
        Cell totalCountBudgetStudents = rowPart1.createCell(columnNumber);
        createFormulaForCell(budgedCells, totalCountBudgetStudents);
        totalBudgetCells.add(totalCountBudgetStudents);
        Cell totalCountContractStudents = rowPart2.createCell(columnNumber);
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

        formulaForStudentsByPayment.deleteCharAt(formulaForStudentsByPayment.length() - 1);
        totalCountCell.setCellFormula(formulaForStudentsByPayment.toString());
    }

}
