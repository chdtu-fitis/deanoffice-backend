package ua.edu.chdtu.deanoffice.service.document.sessionreport;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.service.DegreeService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.StudentExpelService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;

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
    private Set<Integer> semesters;
    private int maxSemesterForBachelor;

    private final DegreeService degreeService;
    private final StudentGroupService studentGroupService;
    private final StudentDegreeService studentDegreeService;
    private final StudentExpelService studentExpelService;

    @Autowired
    public SessionReportService(DegreeService degreeService,
                                StudentGroupService studentGroupService,
                                StudentDegreeService studentDegreeService,
                                StudentExpelService studentExpelService) {
        this.degreeService = degreeService;
        this.studentGroupService = studentGroupService;
        this.studentDegreeService = studentDegreeService;
        this.studentExpelService = studentExpelService;
    }

    public File createSessionReportInXLSX(ApplicationUser user, LocalDate sessionStartDate) throws Exception {
        try (OutputStream outputStream = new FileOutputStream(TEMP_DiRECTORY + FILE_NAME)) {
            Workbook wb = new XSSFWorkbook();
            Sheet sheet = wb.createSheet(SHEET_NAME);

            sheet.createFreezePane(1, 15);

            setWidthsForComumns(sheet);
            addMergeRegions(sheet);
            createHead(wb, user);
            createBody(user.getFaculty().getId(), wb, 15, sessionStartDate);

            wb.write(outputStream);
        }

        return new File(TEMP_DiRECTORY + FILE_NAME);
    }

    private void setWidthsForComumns(Sheet sheet) {
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

    private void createHead(Workbook wb, ApplicationUser user) {
        Sheet sheet = wb.getSheet(SHEET_NAME);

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
        currentCell = createCellForHeadAndSetThisValue(row4, 1, "ВІДОМОСТІ ПРО РЕЗУЛЬТАТИ ЕКЗАМЕНАЦІЙНОЇ СЕСІЇ #рікПочаток/#рікКінець__н.р.");
        setCellStyleAndFontForCell(currentCell, wb, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, 16, true);

        Row row5 = sheet.createRow(5);
        row5.setHeightInPoints(12);

        Row row6 = sheet.createRow(6);
        row6.setHeightInPoints((float) 19.5);
        currentCell = createCellForHeadAndSetThisValue(row6, 5, "Семестр");
        setCellStyleAndFontForCell(currentCell, wb, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, 12, false);
        currentCell = createCellForHeadAndSetThisValue(row6, 7, getCorrectSemesters(user));
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

    }

    //TODO треба уточнити які можуть бути семестри
    private String getCorrectSemesters(ApplicationUser user) {
        int facultyId = user.getFaculty().getId();
        int previousSemester = getCurrentSemester() == 0 ? 1 : 0;
        int bachelorMaxSemester = degreeService.getMaxSemesterForDegreeByNameEngAndFacultyId(BACHELOR_NAME_ENG_IN_DATABASE, facultyId);
        this.maxSemesterForBachelor = bachelorMaxSemester;
        int masterMaxSemester = degreeService.getMaxSemesterForDegreeByNameEngAndFacultyId(MASTER_NAME_ENG_IN_DATABASE, facultyId);
        int totalMaxSemester = bachelorMaxSemester + masterMaxSemester;

        StringBuilder stringBuilder = new StringBuilder();
        Set<Integer> semesters = new TreeSet<>();

        for (int semester = previousSemester + 1; semester <= totalMaxSemester; semester += 2) {
            stringBuilder.append(semester);
            semesters.add(semester);

            if (semester < totalMaxSemester) {
                stringBuilder.append(", ");
            }
        }

        this.semesters = semesters;

        return stringBuilder.toString();
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

    private void createBody(int facultyId, Workbook workbook, int numberOfRow, LocalDate sessionStartDate) {
        Sheet sheet = workbook.getSheet(SHEET_NAME);

        int degreeId = degreeService.getByNameEng(BACHELOR_NAME_ENG_IN_DATABASE).getId();
        int degreeIdOfMaster = degreeService.getByNameEng(MASTER_NAME_ENG_IN_DATABASE).getId();
        boolean isMasterDegree = false;

        for (Integer semester : semesters) {

            if (semester > maxSemesterForBachelor) {
                semester = semester - maxSemesterForBachelor;

                if (!isMasterDegree) {
                    degreeId = degreeIdOfMaster;
                    isMasterDegree = true;
                }
            }

            int yearOfStudy = semester % 2 == 0 ? semester / 2 : semester / 2 + 1;
            List<StudentGroup> groups = studentGroupService.getGroupsByDegreeAndYear(degreeId, yearOfStudy, facultyId);

            Cell numberOfCourse = sheet.createRow(numberOfRow).createCell(0);
            numberOfCourse.setCellValue(semester);
            setCellStyleAndFontForCell(numberOfCourse, workbook, HorizontalAlignment.CENTER,
                    VerticalAlignment.CENTER, 8, false);
            numberOfRow++;
            addDataForOneCourse(groups, numberOfRow, workbook, sessionStartDate);
        }
    }

    private void addDataForOneCourse(List<StudentGroup> groups, int numberOfRow, Workbook workbook, LocalDate sessionStartDate) {
        for (StudentGroup studentGroup : groups) {
            int countStudentsOnSessionStart =
                    studentDegreeService.getCountAllActiveStudentsByBeforeSessionStartDateAndStudentGroupId(studentGroup.getId(), sessionStartDate) +
                    studentExpelService.getCountStudentsInStudentGroupIdWhoExpelAfterSessionStartDate(studentGroup.getId(), sessionStartDate);
        }
    }
}
