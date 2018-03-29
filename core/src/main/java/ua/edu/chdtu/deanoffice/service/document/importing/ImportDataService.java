package ua.edu.chdtu.deanoffice.service.document.importing;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorkbookPart;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorksheetPart;
import org.xlsx4j.exceptions.Xlsx4jException;
import org.xlsx4j.org.apache.poi.ss.usermodel.DataFormatter;
import org.xlsx4j.sml.Cell;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.Worksheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ImportDataService {


    private static Logger log = LoggerFactory.getLogger(ImportDataService.class);
    private final DocumentIOService documentIOService;

    @Autowired
    public ImportDataService(DocumentIOService documentIOService) {
        this.documentIOService = documentIOService;
    }

    public List<Object> getStudentsFromStream(InputStream xlsxInputStream) throws Docx4JException {
        SpreadsheetMLPackage xlsxPkg = documentIOService.loadSpreadsheetDocument(xlsxInputStream);
        List<ImportedData> importedData = importStudents(xlsxPkg);
        return fetchStudentAndStudentDegree(importedData);
    }

    public List<Object> getStudentsFromFile(String fileName) throws IOException, Docx4JException {
        SpreadsheetMLPackage xlsxPkg = documentIOService.loadSpreadsheetDocument(fileName);
        List<ImportedData> importedData = importStudents(xlsxPkg);
        return fetchStudentAndStudentDegree(importedData);
    }

    private List<ImportedData> importStudents(SpreadsheetMLPackage xlsxPkg) {
        try {
            WorkbookPart workbookPart = Objects.requireNonNull(xlsxPkg).getWorkbookPart();
            WorksheetPart sheetPart = workbookPart.getWorksheet(0);

            Worksheet worksheet = sheetPart.getContents();
            org.xlsx4j.sml.SheetData sheetData = worksheet.getSheetData();

            DataFormatter formatter = new DataFormatter();
            SheetData sd = new SheetData();
            List<ImportedData> importedData = new ArrayList<>();
            String cellValue;

            for (Row r : sheetData.getRow()) {
                log.debug("importing row: " + r.getR());

                for (Cell c : r.getC()) {
                    cellValue = "";

                    try {
                        cellValue = formatter.formatCellValue(c);
                    } catch (Exception e) {
                        log.debug(e.getMessage());
                    }

                    if (r.getR() == 1) {
                        sd.assignHeader(cellValue, c.getR());
                    } else {
                        sd.setCellData(c.getR(), cellValue);
                    }

                }

                importedData.add(sd.getStudentData());
                sd.cleanStudentData();
            }

            return importedData;
        } catch (Docx4JException | Xlsx4jException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private List<Student> fetchStudent(List<ImportedData> importedData) {
        throw new UnsupportedOperationException("Not implemented, yet");
    }

    private List<StudentDegree> fetchStudentDegree(List<ImportedData> importedData) {
        throw new UnsupportedOperationException("Not implemented, yet");
    }


    private List<Object> fetchStudentAndStudentDegree(List<ImportedData> importedData) {
        throw new UnsupportedOperationException("Not implemented, yet");
    }
}
