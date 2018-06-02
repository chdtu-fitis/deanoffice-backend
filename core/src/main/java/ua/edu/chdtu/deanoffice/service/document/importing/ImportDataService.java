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
import ua.edu.chdtu.deanoffice.service.*;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.util.StringUtil;

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
    private final FacultyService facultyService;
    private final SpecialityService specialityService;
    private final SpecializationService specializationService;

    @Autowired
    public ImportDataService(DocumentIOService documentIOService, StudentService studentService, StudentDegreeService studentDegreeService,
                             DegreeService degreeService, SpecialityService specialityService, SpecializationService specializationService,
                             FacultyService facultyService) {
        this.documentIOService = documentIOService;
        this.studentService = studentService;
        this.studentDegreeService = studentDegreeService;
        this.degreeService = degreeService;
        this.specialityService = specialityService;
        this.specializationService = specializationService;
        this.facultyService = facultyService;
    }

    public ImportReport getStudentsFromStream(InputStream xlsxInputStream) throws IOException, Docx4JException {
        return getStudents(xlsxInputStream);
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

    private Student fetchStudent(ImportedData data) throws NullPointerException {
        String errorMsg = "Failed to fetch data. ";
        requireNonNull(data, errorMsg + "Param \"data\" cannot be null!");
        Student student = new Student();
        DateFormat formatter = new SimpleDateFormat("M/dd/yy H:mm");
        Date birthDate = null;
        try {
            birthDate = formatter.parse(data.getBirthday());
        } catch (ParseException e) {
            log.debug(e.getMessage());
        }

        if (Strings.isNullOrEmpty(data.getFirstName()) || Strings.isNullOrEmpty(data.getLastName()) || birthDate == null) {
            throw new IllegalArgumentException(errorMsg + "Param \"student\" is empty!");
        }

        List<Student> existsStudentList = studentService.searchByFullName(data.getFirstName(), data.getLastName(), data.getMiddleName());

        if (existsStudentList.size() > 0) {
            student = existsStudentList.get(0);
        }

        student.setBirthDate(birthDate);
        student.setName(data.getFirstName());
        student.setSurname(data.getLastName());
        student.setPatronimic(data.getMiddleName());
        student.setNameEng(StringUtil.firstNotNullNotEmpty(data.getFirstNameEn(),student.getNameEng()));
        student.setSurnameEng(StringUtil.firstNotNullNotEmpty(data.getLastNameEn(), student.getSurnameEng()));
        student.setPatronimicEng(StringUtil.firstNotNullNotEmpty(data.getMiddleNameEn(), student.getPatronimicEng()));
        student.setSex("Чоловіча".equals(data.getPersonsSexName()) ? Sex.MALE : Sex.FEMALE);
        student.setSchool(StringUtil.firstNotNullNotEmpty(data.getDocumentIssued2(),student.getSchool()));

        return student;
    }

    private StudentDegree fetchStudentDegree(ImportedData data, Student student) throws NullPointerException, IllegalArgumentException {
        String errorMsg = "Failed to fetch data. ";
        requireNonNull(data, errorMsg + "Param \"data\" cannot be null!");
        requireNonNull(student, errorMsg + "Param \"student\" cannot be null!");
        StudentDegree studentDegree = fetchStudentDegree(data);
        if (studentDegree == null)
            return null;
        studentDegree.setStudent(student);

        StudentDegree existingStudentDegree = null;
        if (student.getId() > 0) {
            for (StudentDegree sDegree : studentDegreeService.getAllActiveByStudent(student.getId())) {
                if (studentDegree.getSpecialization().getId() == sDegree.getSpecialization().getId()) {
                    existingStudentDegree = sDegree;
                    break;
                }
            }
        }
        if(existingStudentDegree != null && studentDegree != null) {
            existingStudentDegree.setAdmissionOrderNumber(StringUtil.firstNotNullNotEmpty(studentDegree.getAdmissionOrderNumber(), existingStudentDegree.getAdmissionOrderNumber()));
            existingStudentDegree.setAdmissionOrderDate(ObjectUtils.firstNonNull(studentDegree.getAdmissionOrderDate(), existingStudentDegree.getAdmissionOrderDate()));
            existingStudentDegree.setAdmissionDate(ObjectUtils.firstNonNull(studentDegree.getAdmissionDate(), existingStudentDegree.getAdmissionDate()));
            existingStudentDegree.setPayment(ObjectUtils.firstNonNull(studentDegree.getPayment(), existingStudentDegree.getPayment()));
            existingStudentDegree.setPreviousDiplomaNumber(StringUtil.firstNotNullNotEmpty(studentDegree.getPreviousDiplomaNumber(), existingStudentDegree.getPreviousDiplomaNumber()));
            existingStudentDegree.setPreviousDiplomaDate(ObjectUtils.firstNonNull(studentDegree.getPreviousDiplomaDate(), existingStudentDegree.getPreviousDiplomaDate()));
            existingStudentDegree.setPreviousDiplomaType(ObjectUtils.firstNonNull(studentDegree.getPreviousDiplomaType(), existingStudentDegree.getPreviousDiplomaType()));
            existingStudentDegree.setPreviousDiplomaIssuedBy(ObjectUtils.firstNonNull(studentDegree.getPreviousDiplomaIssuedBy(), existingStudentDegree.getPreviousDiplomaIssuedBy()));
            existingStudentDegree.setSupplementNumber(StringUtil.firstNotNullNotEmpty(studentDegree.getSupplementNumber(), existingStudentDegree.getSupplementNumber()));
        }

        return existingStudentDegree == null? studentDegree : existingStudentDegree;
    }

    private StudentDegree fetchStudentDegree(ImportedData data) throws NullPointerException {
        requireNonNull(data, "Failed to fetch data. Param \"data\" cannot be null!");
        DateFormat formatter = new SimpleDateFormat("M/dd/yy H:mm");
        StudentDegree studentDegree = new StudentDegree();

        Specialization specialization = fetchSpecialization(data.getFullSpecialityName(),data.getFullSpecializationName(), data.getProgramName(),
                                                            data.getQualificationGroupName(), data.getFacultyName());
        if (specialization == null)
            return null;
        studentDegree.setSpecialization(specialization);

        for (EducationDocument eduDocument : EducationDocument.values()) {
            if (eduDocument.getNameUkr().toLowerCase().equals(data.getPersonDocumentType().toLowerCase())) {
                studentDegree.setPreviousDiplomaType(eduDocument);
                break;
            }
        }
        try {
            Date prevDiplomaDate = formatter.parse(data.getDocumentDateGet2());
            studentDegree.setPreviousDiplomaDate(prevDiplomaDate);
        } catch (ParseException e) {
            log.debug(e.getMessage());
        }
        studentDegree.setPreviousDiplomaNumber(data.getDocumentSeries2() + " № " + data.getDocumentNumbers2());
        studentDegree.setPreviousDiplomaIssuedBy(data.getDocumentIssued2());

        studentDegree.setActive(true);
        studentDegree.setPayment(Objects.equals(data.getPersonEducationPaymentTypeName(), "Контракт") ? Payment.CONTRACT : Payment.BUDGET);
        DateFormat admissionOrderDateFormatter = new SimpleDateFormat("dd.MM.yyyy");
        final String ADMISSION_REGEXP ="Номер[\\s]+наказу[\\s:]+([\\w\\W]+);[\\W\\w]+Дата[\\s]+наказу[\\s:]*([0-9]{2}.[0-9]{2}.[0-9]{4})";
        Pattern admissionPattern = Pattern.compile(ADMISSION_REGEXP);

        try {
            Matcher matcher = admissionPattern.matcher(data.getRefillInfo());
            if (matcher.find()) {
                studentDegree.setAdmissionOrderNumber(matcher.groupCount() > 0 ? matcher.group(1) : null);
                Date admissionOrderDate = matcher.groupCount() > 1 ? admissionOrderDateFormatter.parse(matcher.group(2)) : null;
                studentDegree.setAdmissionOrderDate(admissionOrderDate);
            }
        } catch (ParseException e) {
            log.debug(e.getMessage());
        } catch (IllegalStateException e) {
            log.debug(e.getMessage());
        }
        try {
            Date admissionDate = new SimpleDateFormat("M/dd/yy H:mm").parse(data.getEducationDateBegin());
            studentDegree.setAdmissionDate(admissionDate);
        }catch (ParseException e) {
            log.debug(e.getMessage());
        }
        studentDegree.setSupplementNumber(data.getEducationId());

        return studentDegree;
    }

    private Specialization fetchSpecialization(String specialityString, String specializationString, String programString, String degreeName, String facultyName) {
        final String SPECIALITY_REGEXP_OLD = "([\\d\\.]+)[\\s]([\\w\\W]+)";
        final String SPECIALITY_REGEXP_NEW = "([\\d\\d\\d])[\\s]([\\w\\W]+)";
        final String SPECIALIZATION_REGEXP = "([\\d+.\\d+])[\\s]([\\w\\W]+)";
        Pattern specialityPattern;
        try {
            if (specialityString.matches(SPECIALITY_REGEXP_OLD)) {
                specialityPattern = Pattern.compile(SPECIALITY_REGEXP_OLD);
            } else {
                specialityPattern = Pattern.compile(SPECIALITY_REGEXP_NEW);
            }
            Matcher matcher = specialityPattern.matcher(specialityString);

            if (matcher.matches() && matcher.groupCount() > 1) {
                String code = matcher.group(1);
                String name = StringUtils.capitalize(matcher.group(2)).trim();
                String specializationName;
                if (!Strings.isNullOrEmpty(programString))
                    specializationName = programString;
                else
                    if (!Strings.isNullOrEmpty(specializationString)) {
                        Pattern specializationPattern = Pattern.compile(SPECIALIZATION_REGEXP);
                        Matcher spMatcher = specializationPattern.matcher(specializationString);
                        specializationName = spMatcher.group(1);
                    }
                    else
                        specializationName = "";
                Specialization specialization = getExistingOrSavedSpecialization(name, code, specializationName , degreeName, facultyName);
                return specialization;
            }
        } catch (Exception e) {
            log.debug(e.getMessage());
        }

        return null;
    }

    private Specialization getExistingOrSavedSpecialization(String specialityName, String specialityCode, String specializationName, String degreeName, String facultyName) {
        Degree degree = degreeService.getByName(degreeName);
        Faculty faculty = facultyService.getByName(facultyName);
        Speciality existingSpeciality = specialityService.getSpecialityByCode(specialityCode);
        if (existingSpeciality == null) {
            Speciality newSpeciality = new Speciality();
            newSpeciality.setName(specialityName);
            newSpeciality.setCode(specialityCode);
            newSpeciality.setActive(true);
            existingSpeciality = specialityService.save(newSpeciality);
        }
        Specialization existingSpecialization = specializationService.getByNameAndDegreeAndSpecialityAndFaculty(specializationName, degree.getId(), existingSpeciality.getId(), faculty.getId());
        if (existingSpecialization == null) {
            existingSpecialization = specializationService.getByNameAndDegreeAndSpecialityAndFaculty(specialityName, degree.getId(), existingSpeciality.getId(), faculty.getId());
        }
        if (existingSpecialization == null) {
            if (specialityCode.matches("\\d\\.\\d+") || (specialityCode.matches("\\d\\d\\d") && !Strings.isNullOrEmpty(specializationName))) {
                Specialization specialization = new Specialization();
                specialization.setName(specializationName);
                specialization.setSpeciality(existingSpeciality);
                specialization.setDegree(degree);
                specialization.setFaculty(faculty);
                specialization.setActive(true);
                existingSpecialization = specializationService.save(specialization);
            }
        }

        return existingSpecialization;
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
//                importReport.fail(fetchStudentDegree(data));
                continue;
            }

            try {
                studentDegree = fetchStudentDegree(data, student);
//                student = studentDegree.getStudent();

//                if (Strings.isNullOrEmpty(student.getName()) || Strings.isNullOrEmpty(student.getSurname()) || student.getBirthDate() == null) {
//                    importReport.fail(studentDegree);
//                    continue;
//                }
                if (studentDegree == null && student.getId() == 0)
                    importReport.fail(student);
                if (studentDegree.getId() > 0) {
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

    public void saveImport(ImportReport importReport){
        List<StudentDegree> sdInsert = importReport.getInsertData();
        for (StudentDegree sd: sdInsert) {
            Student st = sd.getStudent();
            if (st.getId() == 0)
                studentService.save(st);
            studentDegreeService.save(sd);
        }

        List<StudentDegree> sdUpdate = importReport.getUpdateData();
        studentDegreeService.update(sdUpdate);
        for (Student student: importReport.getFailData()){
            studentService.save(student);
        }
    }
}