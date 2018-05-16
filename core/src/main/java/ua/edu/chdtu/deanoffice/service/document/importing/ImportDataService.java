package ua.edu.chdtu.deanoffice.service.document.importing;

import com.google.common.base.Strings;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
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
import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.entity.superclasses.Sex;
import ua.edu.chdtu.deanoffice.service.DegreeService;
import ua.edu.chdtu.deanoffice.service.SpecialityService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.StudentService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

@Service
public class ImportDataService {
    private static Logger log = LoggerFactory.getLogger(ImportDataService.class);
    private final DocumentIOService documentIOService;
    private final StudentService studentService;
    private final StudentDegreeService studentDegreeService;
    private final DegreeService degreeService;
    private final SpecialityService specialityService;

    @Autowired
    public ImportDataService(DocumentIOService documentIOService, StudentService studentService,
                             StudentDegreeService studentDegreeService, DegreeService degreeService, SpecialityService specialityService) {
        this.documentIOService = documentIOService;
        this.studentService = studentService;
        this.studentDegreeService = studentDegreeService;
        this.degreeService = degreeService;
        this.specialityService = specialityService;
    }

    public ImportReport getStudentsFromStream(InputStream xlsxInputStream) throws IOException, Docx4JException {
        return getStudents(xlsxInputStream);
    }

    public ImportReport getStudentsFromFile(String fileName) throws IOException, Docx4JException {
        return getStudents(fileName);
    }

    private ImportReport getStudents(Object source) throws IOException, Docx4JException {
        requireNonNull(source);
        SpreadsheetMLPackage xlsxPkg;

        if (source instanceof String) {
            xlsxPkg = documentIOService.loadSpreadsheetDocument((String) source);
        } else {
            xlsxPkg = documentIOService.loadSpreadsheetDocument((InputStream) source);
        }

        List<ImportedData> importedData = importStudents(xlsxPkg);
        return doImport(importedData);
    }

    private List<ImportedData> importStudents(SpreadsheetMLPackage xlsxPkg) throws NullPointerException {
        requireNonNull(xlsxPkg, "Failed to import data. Param \"xlsxPkg\" cannot be null!");

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

    private Student fetchStudent(ImportedData data) throws NullPointerException {
        String errorMsg = "Failed to fetch data. ";
        requireNonNull(data, errorMsg + "Param \"data\" cannot be null!");
        Student student = new Student();
        DateFormat formatter = new SimpleDateFormat("M/dd/yy H:mm");

        try {
            Date birthDate = formatter.parse(data.getBirthday());
            student.setBirthDate(birthDate);
        } catch (ParseException e) {
            log.debug(e.getMessage());
        }

        if (Strings.isNullOrEmpty(data.getFirstName()) || Strings.isNullOrEmpty(data.getLastName()) || data.getBirthday() == null) {
            throw new IllegalArgumentException(errorMsg + "Param \"student\" is empty!");
        }

        List<Student> existsStudentList = studentService.searchByFullName(data.getFirstName(), data.getLastName(), data.getMiddleName());

        if (existsStudentList.size() > 0) {
            student = existsStudentList.get(0);
        }

        student.setName(data.getFirstName());
        student.setSurname(data.getLastName());
        student.setPatronimic(data.getMiddleName());
        student.setNameEng(ObjectUtils.firstNonNull(student.getNameEng(), data.getFirstNameEn()));
        student.setSurnameEng(ObjectUtils.firstNonNull(student.getSurnameEng(), data.getLastNameEn()));
        student.setPatronimicEng(ObjectUtils.firstNonNull(student.getPatronimicEng(), data.getMiddleNameEn()));
        student.setSex("Чоловіча".equals(data.getPersonsSexName()) ? Sex.MALE : Sex.FEMALE);
        student.setSchool(ObjectUtils.firstNonNull(student.getSchool(), data.getDocumentIssued()));

        return student;
    }

    private StudentDegree fetchStudentDegree(ImportedData data) throws NullPointerException {
        requireNonNull(data, "Failed to fetch data. Param \"data\" cannot be null!");
        DateFormat formatter = new SimpleDateFormat("M/dd/yy H:mm");
        StudentDegree studentDegree = new StudentDegree();

        for (DegreeEnum degreeEnum : DegreeEnum.values()) {
            if (degreeEnum.getNameUkr().equals(data.getQualificationGroupName())) {
                studentDegree.setDegree(degreeService.getDegreeById(degreeEnum.getId()));
                break;
            }
        }

        for (EducationDocument eduDocument : EducationDocument.values()) {
            if (eduDocument.getNameUkr().equals(data.getPersonDocumentType())) {
                studentDegree.setPreviousDiplomaType(eduDocument);
                break;
            }
        }

        studentDegree.setActive(true);
        studentDegree.setPreviousDiplomaNumber(data.getDocumentSeries2() + data.getDocumentNumbers2());
        studentDegree.setPayment(Objects.equals(data.getPersonEducationPaymentTypeName(), "Контракт") ? Payment.CONTRACT : Payment.BUDGET);
        DateFormat admissionDateFormatter = new SimpleDateFormat("dd.MM.yyyy");
        final String ADMISSION_REGEXP ="Номер[\\s]+наказу[\\s:]+([\\w\\W]+);[\\W\\w]+Дата[\\s]+наказу[\\s:]*([0-9]{2}.[0-9]{2}.[0-9]{4})";
        Pattern admissionPattern = Pattern.compile(ADMISSION_REGEXP);

        try {
            Matcher matcher = admissionPattern.matcher(data.getRefillInfo());
            studentDegree.setAdmissionOrderNumber(matcher.matches() && matcher.groupCount() > 0 ? matcher.group(1) : null);
            Date admissionOrderDate = matcher.matches() && matcher.groupCount() > 1 ? admissionDateFormatter.parse(matcher.group(2)) : null;
            studentDegree.setAdmissionOrderDate(admissionOrderDate);
        } catch (ParseException e) {
            log.debug(e.getMessage());
        }

        try {
            Date prevDiplomaDate = formatter.parse(data.getDocumentDateGet2());
            studentDegree.setPreviousDiplomaDate(prevDiplomaDate);
        } catch (ParseException e) {
            log.debug(e.getMessage());
        }

        return studentDegree;
    }

    private Speciality fetchSpeciality(String specialityString) {
        final String SPECIALITY_REGEXP = "([\\d.]+)[\\s]([\\w\\W]+)";
        Pattern specialityPattern = Pattern.compile(SPECIALITY_REGEXP);

        try {
            Matcher matcher = specialityPattern.matcher(specialityString);

            if (matcher.matches() && matcher.groupCount() > 1) {
                String code = matcher.group(1);
                String name = StringUtils.capitalize(matcher.group(2)).trim();
                Speciality speciality = getExistsSpeciality(name, code);

                if (speciality == null) {
                    speciality = new Speciality();
                    speciality.setName(name);
                    speciality.setCode(code);
                }

                return speciality;
            }
        } catch (Exception e) {
            log.debug(e.getMessage());
        }

        return null;
    }

    private Speciality getExistsSpeciality(String name, String code) {
        Speciality existsSpeciality = specialityService.getSpecialityByCode(code);
        return existsSpeciality == null ? specialityService.getSpecialityByName(name) : existsSpeciality;
    }

    private StudentDegree fetchStudentDegree(ImportedData data, Student student) throws NullPointerException, IllegalArgumentException {
        String errorMsg = "Failed to fetch data. ";
        requireNonNull(data, errorMsg + "Param \"data\" cannot be null!");
        requireNonNull(student, errorMsg + "Param \"student\" cannot be null!");
        StudentDegree studentDegree = fetchStudentDegree(data);
        studentDegree.setStudent(student);

        if (student.getId() > 0) {
            for (StudentDegree sDegree : studentDegreeService.getStudentDegree(student.getId())) {
                if (studentDegree.getAdmissionOrderDate() != null && studentDegree.getAdmissionOrderDate() == sDegree.getAdmissionOrderDate()) {
                    return sDegree;
                }
            }
        }

        return studentDegree;
    }

    private ImportReport doImport(List<ImportedData> importedData) throws NullPointerException {
        Objects.requireNonNull(importedData);
        ImportReport importReport = new ImportReport();

        for (ImportedData data : importedData) {
            Student student;
            StudentDegree studentDegree;

            try {
                student = fetchStudent(data);
            } catch (Exception e) {
                log.error(e.getMessage());
                importReport.fail(fetchStudentDegree(data));
                continue;
            }

            try {
                studentDegree = fetchStudentDegree(data, student);
                student = studentDegree.getStudent();

                if (Strings.isNullOrEmpty(student.getName()) || Strings.isNullOrEmpty(student.getSurname()) || student.getBirthDate() == null) {
                    importReport.fail(studentDegree);
                    continue;
                }

                if (studentDegree.getStudent().getId() > 0) {
                    importReport.update(studentDegree);
                } else {
                    importReport.insert(studentDegree);
                }
            } catch (IllegalArgumentException | NullPointerException e) {
                log.error(e.getMessage());
            }
        }

        return importReport;
    }
}