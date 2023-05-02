package ua.edu.chdtu.deanoffice.service.datasync.edebo.student;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorkbookPart;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorksheetPart;
import org.springframework.stereotype.Service;
import org.xlsx4j.org.apache.poi.ss.usermodel.DataFormatter;
import org.xlsx4j.sml.Cell;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.Worksheet;
import ua.edu.chdtu.deanoffice.service.DegreeService;
import ua.edu.chdtu.deanoffice.service.FacultyService;
import ua.edu.chdtu.deanoffice.service.SpecialityService;
import ua.edu.chdtu.deanoffice.service.SpecializationService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.StudentService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*Outdated class - legacy code for previous import approach, not tested*/
public class EdeboStudentDataSynchronizationServiceXlsx extends EdeboStudentDataSynchronizationServiceImpl {

    public EdeboStudentDataSynchronizationServiceXlsx(DocumentIOService documentIOService, StudentService studentService, StudentDegreeService studentDegreeService,
                                                     DegreeService degreeService, SpecialityService specialityService, SpecializationService specializationService,
                                                     FacultyService facultyService) {
        super(documentIOService, studentService, studentDegreeService, degreeService, specialityService, specializationService, facultyService);
    }

    protected List<ImportedData> getStudentDegreesFromStream(InputStream inputStream) throws IOException {
        return getEdeboStudentDegreesInfo(inputStream);
    }

    private List<ImportedData> getEdeboStudentDegreesInfo(Object source) throws IOException {
        SpreadsheetMLPackage xlsxPkg;
        try {
            if (source instanceof String) {
                xlsxPkg = documentIOService.loadSpreadsheetDocument((String) source);
            } else {
                xlsxPkg = documentIOService.loadSpreadsheetDocument((InputStream) source);
            }
        } catch (Docx4JException e) {
            throw new IOException(e);
        }
        return getImportedDataFromXlsxPkg(xlsxPkg);
    }

    private List<ImportedData> getImportedDataFromXlsxPkg(SpreadsheetMLPackage xlsxPkg) {
        try {
            WorkbookPart workbookPart = xlsxPkg.getWorkbookPart();
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
                        if (c.getF() != null)
                            cellValue = c.getF().getValue();
                        else
                            cellValue = StringUtil.replaceSingleQuotes(formatter.formatCellValue(c));
                    } catch (Exception e) {
                        log.debug(e.getMessage());
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
            log.error(e.getMessage());
            return Collections.emptyList();
        }
    }
}
