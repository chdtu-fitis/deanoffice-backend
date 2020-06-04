package ua.edu.chdtu.deanoffice.service.document.sessionreport;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

@Service
public class SessionReportService {

    private final String TEMP_DiRECTORY = System.getProperty("java.io.tmpdir") + "/";
    private final String FILE_NAME = "session-report.xlsx";
    private static final String GLOBAL_FONT_NAME = "Arial Cyr";
    private static final Short GLOBAL_FONT_COLOR = IndexedColors.BLACK.getIndex();
    private static final Short GLOBAL_BORDERS_COLOR = IndexedColors.BLACK.getIndex();
    private static final BorderStyle GLOBAL_BORDER_STYLE = BorderStyle.THIN;



    public File createSessionReportInXLSX() throws Exception {
        try (OutputStream outputStream = new FileOutputStream(TEMP_DiRECTORY + FILE_NAME)) {
            Workbook wb = new XSSFWorkbook();
            Sheet sheet = wb.createSheet("Session report");

            setColumnWidth(sheet);
            addMergeRegions(sheet);
            createHead(sheet, wb);

            wb.write(outputStream);
        }

        return new File(TEMP_DiRECTORY + FILE_NAME);
    }

    private void setColumnsWidth(Sheet sheet) {
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

}
