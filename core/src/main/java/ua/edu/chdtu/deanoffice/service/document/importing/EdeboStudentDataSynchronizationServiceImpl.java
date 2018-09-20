package ua.edu.chdtu.deanoffice.service.document.importing;

import com.google.common.base.Strings;
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

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

//Use Strings.IsNUllOrEmpty

@Service
public class EdeboStudentDataSynchronizationServiceImpl implements EdeboStudentDataSyncronizationService {
    private static final String SPECIALIZATION_REGEXP = "([\\d]+)\\.[\\d]+\\s([\\w\\W])+";
    private static final String SPECIALITY_REGEXP_OLD = "([\\d]\\.[\\d]+)\\s([\\w\\W])+";
    private static final String SPECIALITY_REGEXP_NEW = "([\\d]{3})\\s([\\w\\W]+)";
    private static Logger log = LoggerFactory.getLogger(EdeboStudentDataSynchronizationServiceImpl.class);
    private final DocumentIOService documentIOService;
    private final StudentService studentService;
    private final StudentDegreeService studentDegreeService;
    private final DegreeService degreeService;
    private final FacultyService facultyService;
    private final SpecialityService specialityService;
    private final SpecializationService specializationService;

    @Autowired
    public EdeboStudentDataSynchronizationServiceImpl(DocumentIOService documentIOService, StudentService studentService, StudentDegreeService studentDegreeService,
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

    private List<ImportedData> getStudentDegreesFromStream(InputStream xlsxInputStream) throws IOException, Docx4JException {
        return getStudentDegrees(xlsxInputStream);
    }

    private List<ImportedData> getStudentDegrees(Object source) throws IOException, Docx4JException {
        requireNonNull(source);
        SpreadsheetMLPackage xlsxPkg;

        if (source instanceof String) {
            xlsxPkg = documentIOService.loadSpreadsheetDocument((String) source);
        } else {
            xlsxPkg = documentIOService.loadSpreadsheetDocument((InputStream) source);
        }
        return getImportedDataFromXlsxPkg(xlsxPkg);
    }

    private List<ImportedData> getImportedDataFromXlsxPkg(SpreadsheetMLPackage xlsxPkg) throws NullPointerException {
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

    @Override
    public EdeboDataSyncronizationReport getSyncronizationReport(InputStream xlsxInputStream) throws NullPointerException {
        try {
            List<ImportedData> importedData = getStudentDegreesFromStream(xlsxInputStream);
            Objects.requireNonNull(importedData);
            EdeboDataSyncronizationReport edeboDataSyncronizationReport = new EdeboDataSyncronizationReport();
            for (ImportedData data : importedData) {
                if (isCriticalDataAvailable(data)) {
                    Student student = getStudentFromData(data);
                    StudentDegree studentDegree = getStudentDegreeFromData(data);
                    studentDegree.setStudent(student);
                    addStudentDegreeToSyncronizationReport(studentDegree);
                }
            }
            return edeboDataSyncronizationReport;
        } catch (Docx4JException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean isCriticalDataAvailable(ImportedData data) throws RuntimeException {
        List<String> necessaryNotEmptyStudentDegreeData = new ArrayList<>();
        String specializationName = data.getFullSpecializationName();
        String specialityName = data.getFullSpecialityName();
        String qualificationName = data.getProgramName();
        necessaryNotEmptyStudentDegreeData.add(data.getLastName());
        necessaryNotEmptyStudentDegreeData.add(data.getFirstName());
        necessaryNotEmptyStudentDegreeData.add(data.getMiddleName());
        necessaryNotEmptyStudentDegreeData.add(data.getBirthday());
        necessaryNotEmptyStudentDegreeData.add(data.getQualificationGroupName());
        necessaryNotEmptyStudentDegreeData.add(data.getFacultyName());
        for (String s : necessaryNotEmptyStudentDegreeData) {
            if (Strings.isNullOrEmpty(s)) {
                return false;
            }
        }
        if (Strings.isNullOrEmpty(specialityName)) {
            return false;
        }
        Pattern specialityPattern = Pattern.compile(SPECIALITY_REGEXP_NEW);
        Matcher specialityMatcher = specialityPattern.matcher(specialityName);
        if (specialityMatcher.matches() && Strings.isNullOrEmpty(specializationName) && !Strings.isNullOrEmpty(qualificationName)) {
            return true;
        } else {
            Pattern specializationPattern = Pattern.compile(SPECIALIZATION_REGEXP);
            Matcher specializationMatcher = specializationPattern.matcher(specializationName);
            if (specialityMatcher.matches() && specializationMatcher.matches() && !Strings.isNullOrEmpty(qualificationName)) {
                return true;
            } else {
                specialityPattern = Pattern.compile(SPECIALITY_REGEXP_OLD);
                specialityMatcher = specialityPattern.matcher(specialityName);
                if (specialityMatcher.matches() &&
                        Strings.isNullOrEmpty(specializationName) && !Strings.isNullOrEmpty(qualificationName)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public Student getStudentFromData(ImportedData data) {
        Student student = new Student();
        Date birthDate = formatFileBirthdayDateToDbBirthdayDate(data.getBirthday());
        student.setBirthDate(birthDate);
        student.setName(data.getFirstName());
        student.setSurname(data.getLastName());
        student.setPatronimic(data.getMiddleName());
        student.setNameEng(data.getFirstNameEn());
        student.setSurnameEng(data.getLastNameEn());
        student.setPatronimicEng(data.getMiddleNameEn());
        student.setSex("Чоловіча".equals(data.getPersonsSexName()) ? Sex.MALE : Sex.FEMALE);
        student.setSchool(data.getDocumentIssued2());
        return student;
    }

    @Override
    public Speciality getSpecialityFromData(ImportedData data) {
        Speciality speciality = new Speciality();
        String specialityNameWithCode = data.getFullSpecialityName();
        String specialityCode = "";
        String specialityName = "";
        Pattern specialityPattern;
        if (specialityNameWithCode.matches(SPECIALITY_REGEXP_OLD)) {
            specialityPattern = Pattern.compile(SPECIALITY_REGEXP_OLD);
        } else {
            specialityPattern = Pattern.compile(SPECIALITY_REGEXP_NEW);
        }
        Matcher matcher = specialityPattern.matcher(specialityNameWithCode);
        if (matcher.matches() && matcher.groupCount() > 1) {
            specialityCode = matcher.group(1).trim();
            specialityName = StringUtils.capitalize(matcher.group(2)).trim();
        }
        speciality.setCode(specialityCode);
        speciality.setName(specialityName);
        return speciality;
    }

    @Override
    public Specialization getSpecializationFromData(ImportedData data) {
        Specialization specialization = new Specialization();
        specialization.setSpeciality(getSpecialityFromData(data));
        Faculty faculty = new Faculty();
        String fullSpecializationName = data.getFullSpecializationName();
        if (!Strings.isNullOrEmpty(fullSpecializationName)) {
            Pattern specializationPattern = Pattern.compile(SPECIALIZATION_REGEXP);
            Matcher spMatcher = specializationPattern.matcher(fullSpecializationName);
            if (spMatcher.matches() && spMatcher.groupCount() > 1) {
                specialization.setCode(spMatcher.group(0));
                specialization.setName(spMatcher.group(1));
            }
        }
        specialization.setNameEng(data.getProgramNameEn());
        specialization.setDegree(getDegreeFromFileByName(data.getQualificationGroupName()));
//        specialization.set(data.getQualificationGroupName());
//        specialization.set(data.getBaseQualificationName());
        faculty.setName(data.getFacultyName());
        specialization.setFaculty(faculty);
        return specialization;
    }

    private Degree getDegreeFromFileByName(String degreeName) {
        DegreeEnum degreeEnum = DegreeEnum.BACHELOR;
        degreeName = degreeName.toUpperCase();
        switch (degreeName) {
            case "Бакалавр":
                degreeEnum = DegreeEnum.BACHELOR;
                break;
            case "Магістр":
                degreeEnum = DegreeEnum.MASTER;
                break;
            case "Спеціаліст":
                degreeEnum = DegreeEnum.SPECIALIST;
                break;
        }
        return new Degree(degreeEnum.getId(), degreeEnum.getNameUkr());
    }

    private EducationDocument getEducationDocumentByName(String educationDocumentName){
//    EducationDocument educationDocument =EducationDocument.SECONDARY_SCHOOL_CERTIFICATE;
//    educationDocumentName = educationDocumentName.toUpperCase();
//    switch (educationDocumentName){
//        case
//    }
        return null;
    }

    @Override
    public StudentDegree getStudentDegreeFromData(ImportedData data) {
        StudentGroup studentGroup = new StudentGroup();
        Student student = getStudentFromData(data);
        Specialization specialization = getSpecializationFromData(data); //getSpeciality inside
        StudentDegree studentDegree = new StudentDegree();
        studentDegree.setStudent(student);
        studentDegree.setSpecialization(specialization);
        studentDegree.setSupplementNumber(data.getEducationId());
        studentDegree.setAdmissionDate(formatFileBirthdayDateToDbBirthdayDate(data.getEducationDateBegin()));
        studentDegree.setPreviousDiplomaType(getEducationDocumentFromData(data.getBaseQualificationName()));
        studentGroup.setTuitionForm(getTuitionFormFromFile(data.getEducationFormName()));
        studentDegree.setStudentGroup(studentGroup);
        Specialization s = new Specialization();
//        studentDegree.setPreviousDiplomaType(Ed);
        return studentDegree;
    }

    @Override
    public void addStudentDegreeToSyncronizationReport(StudentDegree studentDegree) {

    }

    @Override
    public boolean isSecondaryFieldsMatch(StudentDegree studentDegreeFromFile, StudentDegree studentDegreeFromDb) {
        return false;
    }

    private EducationDocument getEducationDocumentFromData(String baseQualification) {
        switch (baseQualification) {
            case "Бакалавр":
                return EducationDocument.BACHELOR_DIPLOMA;
            case "Магістр":
                return EducationDocument.MASTER_DIPLOMA;
            default:
                return null;
        }
    }

    private Date formatFileBirthdayDateToDbBirthdayDate(String fileDate) {
        try {
            DateFormat dbDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat formatter = new SimpleDateFormat("M/dd/yy H:mm");
            return dbDateFormatter.parse(dbDateFormatter.format(formatter.parse(fileDate)));
        } catch (ParseException e) {
            log.debug(e.getMessage());
        }
        return null;
    }

    private TuitionForm getTuitionFormFromFile(String tuitionFormFromFile) {
        if (tuitionFormFromFile.equals("Денна")) {
            return TuitionForm.FULL_TIME;
        } else {
            return TuitionForm.EXTRAMURAL;
        }
    }

}