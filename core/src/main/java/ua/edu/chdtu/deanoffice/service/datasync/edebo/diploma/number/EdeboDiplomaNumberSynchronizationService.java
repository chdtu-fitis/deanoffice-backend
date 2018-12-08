package ua.edu.chdtu.deanoffice.service.datasync.edebo.diploma.number;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorkbookPart;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorksheetPart;
import org.springframework.beans.factory.annotation.Autowired;
import org.xlsx4j.org.apache.poi.ss.usermodel.DataFormatter;
import org.xlsx4j.sml.Cell;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.Worksheet;

import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EdeboDiplomaNumberSynchronizationService {
    private final DocumentIOService documentIOService;

    @Autowired
    public EdeboDiplomaNumberSynchronizationService(DocumentIOService documentIOService){
        this.documentIOService = documentIOService;
    }

    public EdeboDiplomaNumberSynchronizationReport getEdeboDiplomaNumberSynchronizationReport(InputStream xlsxInputStream, int facultyId) throws Exception{
        if (xlsxInputStream == null)
            throw new Exception("Помилка читання файлу");
        try {
            List<DiplomaImportData> diplomaImportData = getStudentDegreesFromStream(xlsxInputStream);

            EdeboDiplomaNumberSynchronizationReport edeboDiplomaNumberSynchronizationReport = new EdeboDiplomaNumberSynchronizationReport();

            return edeboDiplomaNumberSynchronizationReport;
        } catch (Docx4JException e) {
            e.printStackTrace();
            throw new Exception("Помилка обробки файлу");
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Помилка читання файлу");
        } finally {
            xlsxInputStream.close();
        }
    }

    private List<DiplomaImportData> getStudentDegreesFromStream(InputStream xlsxInputStream) throws IOException, Docx4JException {
        return getEdeboStudentDegreesInfo(xlsxInputStream);
    }

    private List<DiplomaImportData> getEdeboStudentDegreesInfo(Object source) throws IOException, Docx4JException {
        SpreadsheetMLPackage xlsxPkg;
        if (source instanceof String) {
            xlsxPkg = documentIOService.loadSpreadsheetDocument((String) source);
        } else {
            xlsxPkg = documentIOService.loadSpreadsheetDocument((InputStream) source);
        }
        return getImportedDataFromXlsxPkg(xlsxPkg);
    }

    private List<DiplomaImportData> getImportedDataFromXlsxPkg(SpreadsheetMLPackage xlsxPkg) {
        try {
            WorkbookPart workbookPart = xlsxPkg.getWorkbookPart();
            WorksheetPart sheetPart = workbookPart.getWorksheet(0);
            Worksheet worksheet = sheetPart.getContents();
            org.xlsx4j.sml.SheetData sheetData = worksheet.getSheetData();
            DataFormatter formatter = new DataFormatter();
            DiplomaSheetData sd = new DiplomaSheetData();
            List<DiplomaImportData> importedData = new ArrayList<>();
            String cellValue;

            for (Row r : sheetData.getRow()) {
//                log.debug("importing row: " + r.getR());
                for (Cell c : r.getC()) {
                    cellValue = "";
                    try {
                        cellValue = StringUtil.replaceSingleQuotes(formatter.formatCellValue(c));
                    } catch (Exception e) {
//                        log.debug(e.getMessage());
                    }
                    if (r.getR() == 1) {
                        sd.assignHeader(cellValue, c.getR());
                    } else {
                        sd.setCellData(c.getR(), cellValue.trim());
                    }
                }
                if (r.getR() == 1) {
                    continue;
                }
                importedData.add(sd.getStudentData());
                sd.cleanStudentData();
            }
            return importedData;
        } catch (Exception e) {
//            log.error(e.getMessage());
            return Collections.emptyList();
        }
    }
}
