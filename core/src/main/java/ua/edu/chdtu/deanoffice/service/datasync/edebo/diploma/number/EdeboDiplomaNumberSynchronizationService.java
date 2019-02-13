package ua.edu.chdtu.deanoffice.service.datasync.edebo.diploma.number;

import com.google.common.base.Strings;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorkbookPart;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorksheetPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xlsx4j.org.apache.poi.ss.usermodel.DataFormatter;
import org.xlsx4j.sml.Cell;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.Worksheet;

import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.datasync.edebo.diploma.number.beans.DiplomaAndStudentSynchronizedDataBean;
import ua.edu.chdtu.deanoffice.service.datasync.edebo.diploma.number.beans.MissingDataBean;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EdeboDiplomaNumberSynchronizationService {
    private final String HONOR_OF_DIPLOMA = "з відзнакою";
    private static Logger log = LoggerFactory.getLogger(EdeboDiplomaNumberSynchronizationService.class);
    private final DocumentIOService documentIOService;
    private final StudentDegreeService studentDegreeService;

    @Autowired
    public EdeboDiplomaNumberSynchronizationService(DocumentIOService documentIOService, StudentDegreeService studentDegreeService) {
        this.documentIOService = documentIOService;
        this.studentDegreeService = studentDegreeService;
    }

    public EdeboDiplomaNumberSynchronizationReport getEdeboDiplomaNumberSynchronizationReport(InputStream xlsxInputStream, String facultyName) throws Exception {
        try {
            List<DiplomaImportData> diplomaImportData = getStudentDegreesFromStream(xlsxInputStream);
            EdeboDiplomaNumberSynchronizationReport diplomaSynchronizationReport = new EdeboDiplomaNumberSynchronizationReport();

            for (DiplomaImportData importData : diplomaImportData) {
                addSynchronizationReportForDiplomaImportedData(importData, diplomaSynchronizationReport, facultyName);
            }

            sortingDiplomaAndStudentSynchronizedData(diplomaSynchronizationReport);

            return diplomaSynchronizationReport;
        } catch (Docx4JException | IOException e) {
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
                log.debug("importing row: " + r.getR());
                for (Cell c : r.getC()) {
                    cellValue = "";
                    try {
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

    public void addSynchronizationReportForDiplomaImportedData(DiplomaImportData importData,
                                                               EdeboDiplomaNumberSynchronizationReport diplomaSynchronizationReport,
                                                               String facultyName) {
        if (!importData.getFacultyName().toUpperCase().equals(facultyName.toUpperCase())) {
            return;
        }

        if (!isCriticalDataAvailable(importData)) {
            String message = "Недостатньо даних для синхронізації";
            diplomaSynchronizationReport.addBeanToMissingDataList(
                    new MissingDataBean(
                            message,
                            new DiplomaAndStudentSynchronizedDataBean(importData)
                    )
            );
        }

        StudentDegree studentDegreefromDb = studentDegreeService.getBySupplementNumber(
                importData.getEducationId()
        );
        if (studentDegreefromDb == null) {
            String message = "Даного студента не існує в базі даних";
            diplomaSynchronizationReport.addBeanToMissingDataList(
                    new MissingDataBean(
                            message,
                            new DiplomaAndStudentSynchronizedDataBean(importData)
                    )
            );
        } else {
            boolean diplomaHonor = false;
            if (importData.getAwardTypeId().toLowerCase().equals(HONOR_OF_DIPLOMA)){
                diplomaHonor = true;
            }
            diplomaSynchronizationReport.addBeanToSynchronizedList(
                    new DiplomaAndStudentSynchronizedDataBean(
                            studentDegreefromDb,
                            importData.getDocumentSeries() + " № " + importData.getDocumentNumber(),
                            diplomaHonor
                    )
            );
        }
    }

    private boolean isCriticalDataAvailable(DiplomaImportData importData) {
        List<String> dataOnCheck = new ArrayList();
        dataOnCheck.add(importData.getLastName() + " " + importData.getFirstName() + " " + importData.getMiddleName());
        dataOnCheck.add(importData.getSpecialityName());
        dataOnCheck.add(importData.getDocumentNumber());
        dataOnCheck.add(importData.getDocumentSeries());
        dataOnCheck.add(importData.getEducationId());
        for (String data : dataOnCheck) {
            if (Strings.isNullOrEmpty(data)) {
                return false;
            }
        }
        return true;
    }

    private void sortingDiplomaAndStudentSynchronizedData(EdeboDiplomaNumberSynchronizationReport edeboDataSyncronizationReport){
        edeboDataSyncronizationReport.setDiplomaAndStudentSynchronizedDataGreen(
                edeboDataSyncronizationReport.getDiplomaAndStudentSynchronizedDataGreen()
                        .stream().sorted((sd1,sd2) -> (
                                sd1.getGroupName() + " "
                                + sd1.getSurname() + " "
                                + sd1.getName() + " "
                                + sd1.getPatronimic())
                        .compareTo(
                                sd1.getGroupName() + " "
                                        + sd1.getSurname() + " "
                                        + sd1.getName() + " "
                                        + sd1.getPatronimic()))
                        .collect(Collectors.toList())
        );
    }
}
