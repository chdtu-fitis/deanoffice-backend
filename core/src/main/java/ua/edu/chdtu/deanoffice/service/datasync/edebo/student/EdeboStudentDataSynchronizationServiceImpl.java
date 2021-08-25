package ua.edu.chdtu.deanoffice.service.datasync.edebo.student;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.entity.superclasses.Sex;
import ua.edu.chdtu.deanoffice.service.DegreeService;
import ua.edu.chdtu.deanoffice.service.FacultyService;
import ua.edu.chdtu.deanoffice.service.SpecialityService;
import ua.edu.chdtu.deanoffice.service.SpecializationService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.StudentService;
import ua.edu.chdtu.deanoffice.service.datasync.edebo.student.beans.StudentDegreePrimaryDataBean;
import ua.edu.chdtu.deanoffice.service.datasync.edebo.student.beans.MissingPrimaryDataRedMessageBean;
import ua.edu.chdtu.deanoffice.service.datasync.edebo.student.beans.StudentDegreePrimaryDataWithGroupBean;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.util.FacultyUtil;
import ua.edu.chdtu.deanoffice.util.comparators.EntityUtil;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class EdeboStudentDataSynchronizationServiceImpl implements EdeboStudentDataSyncronizationService {
    private static final String SPECIALIZATION_REGEXP = "([\\d]+\\.[\\d]+)\\s([\\w\\W]+)";
    private static final String SPECIALITY_REGEXP_OLD = "([\\d]\\.[\\d]+)\\s([\\w\\W]+)";
    private static final String SPECIALITY_REGEXP_NEW = "([\\d]{3})\\s([\\w\\W]+)";
    private static final String ADMISSION_REGEXP = "Номер[\\s]+наказу[\\s:]+([\\w\\W]+);[\\W\\w]+Дата[\\s]+наказу[\\s:]*([0-9]{2}.[0-9]{2}.[0-9]{4})";
    private static final String EXPEL_DATE_REGEXP = "[\\W\\w]+Дата[\\s]+відрахування[\\s:]*([0-9]{2}.[0-9]{2}.[0-9]{4})";
    private static final String STUDENT_PREVIOUS_UNIVERSITY_FIELDS_TO_COMPARE[] = {"universityName", "studyStartDate", "studyEndDate"};
    private static final String SECONDARY_STUDENT_DEGREE_FIELDS_TO_COMPARE[] = {"payment", "tuitionForm", "citizenship", "previousDiplomaNumber", "previousDiplomaDate",
            "previousDiplomaType", "previousDiplomaIssuedBy", "supplementNumber", "admissionDate", "admissionOrderNumber", "admissionOrderDate", "citizenship"};
    private static final String SECONDARY_STUDENT_FIELDS_TO_COMPARE[] = {
            "surnameEng", "nameEng", "sex"};
    protected static Logger log = LoggerFactory.getLogger(EdeboStudentDataSynchronizationServiceImpl.class);
    protected final DocumentIOService documentIOService;
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

    protected abstract List<ImportedData> getStudentDegreesFromStream(InputStream inputStream) throws IOException;

    @Override
    public EdeboStudentDataSynchronizationReport getEdeboDataSynchronizationReport(InputStream inputStream, int facultyId, Map<String, String> selectionParams) throws Exception {
        if (inputStream == null)
            throw new Exception("Помилка читання файлу");
        try {
            List<ImportedData> importedData = getStudentDegreesFromStream(inputStream);
            Objects.requireNonNull(importedData);
            EdeboStudentDataSynchronizationReport edeboDataSyncronizationReport = new EdeboStudentDataSynchronizationReport();
            for (ImportedData data : importedData) {
                addSynchronizationReportForImportedData(data, edeboDataSyncronizationReport, facultyId, selectionParams);
            }
            getAllIdForAbsentInFileStudentDegrees(edeboDataSyncronizationReport, selectionParams, facultyId);
            sortingSyncohronizedDegreeGreen(edeboDataSyncronizationReport);
            sortingAbsentInFileStudentDegreeYellow(edeboDataSyncronizationReport);
            sortingMissingPrimaryDataRed(edeboDataSyncronizationReport);
            sortingNoSuchStudentOrStudentDegreeInDbOrange(edeboDataSyncronizationReport);
            return edeboDataSyncronizationReport;
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Помилка читання файлу");
        } finally {
            inputStream.close();
        }
    }

    @Override
    public boolean isCriticalDataAvailable(StudentDegree studentDegree) throws RuntimeException {
        List<String> necessaryNotEmptyStudentDegreeData = new ArrayList<>();
        Specialization specialization = studentDegree.getSpecialization();
        Student student = studentDegree.getStudent();
        String specialityName = specialization.getSpeciality().getName();
        necessaryNotEmptyStudentDegreeData.add(student.getSurname());
        necessaryNotEmptyStudentDegreeData.add(student.getName());
        necessaryNotEmptyStudentDegreeData.add(specialization.getDegree().getName());
        necessaryNotEmptyStudentDegreeData.add(specialization.getFaculty().getName());
        for (String s : necessaryNotEmptyStudentDegreeData) {
            if (Strings.isNullOrEmpty(s)) {
                return false;
            }
        }
        if (student.getBirthDate() == null || Strings.isNullOrEmpty(specialityName) ||
                studentDegree.getSpecialization().getDegree() == null) {
            return false;
        }
        return true;
    }

    private boolean isSpecialityPatternMatch(ImportedData importedData) {
        String specialityName = importedData.getFullSpecialityName();
        Pattern specialityPattern = Pattern.compile(SPECIALITY_REGEXP_NEW);
        Matcher specialityMatcher = specialityPattern.matcher(specialityName);
        if (specialityMatcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Student getStudentFromData(ImportedData data) {
        Student student = new Student();
        Date birthDate = parseDate(data.getBirthday());
        student.setBirthDate(birthDate);
        student.setName(data.getFirstName());
        student.setSurname(data.getLastName());
        student.setPatronimic(data.getMiddleName());
        student.setNameEng(data.getFirstNameEn());
        student.setSurnameEng(data.getLastNameEn());
        student.setSex("Чоловіча".equals(data.getPersonsSexName()) ? Sex.MALE : Sex.FEMALE);
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
            specialityName = matcher.group(2).trim();
        }
        speciality.setCode(specialityCode);
        speciality.setName(specialityName);
        return speciality;
    }

    @Override
    public Specialization getSpecializationFromData(ImportedData data) {
        Specialization specialization = new Specialization();
        specialization.setName("");
        specialization.setCode("");
        Speciality speciality = getSpecialityFromData(data);
        specialization.setSpeciality(speciality);
        Faculty faculty = new Faculty();
        if ((speciality.getCode() + " " + speciality.getName()).matches(SPECIALITY_REGEXP_NEW)) {
            specialization.setName(data.getProgramName());
        }
        specialization.setDegree(DegreeEnum.getDegreeFromEnumByName(data.getQualificationGroupName()));
        faculty.setName(data.getFacultyName());
        specialization.setFaculty(faculty);
        return specialization;
    }

    @Override
    public StudentPreviousUniversity getStudentPreviousUniversityFromData(ImportedData data) {
        StudentPreviousUniversity studentPreviousUniversity = null;
        if (!Strings.isNullOrEmpty(data.getUniversityFrom()) && !Strings.isNullOrEmpty(data.getEduFromInfo())) {
            studentPreviousUniversity = new StudentPreviousUniversity();
            studentPreviousUniversity.setUniversityName(data.getUniversityFrom());
            studentPreviousUniversity.setStudyStartDate(parseDate(data.getEducationDateBegin()));
            studentPreviousUniversity.setStudyEndDate(getExpelDateFromPreviousUniversity(data.getEduFromInfo()));
        }
        return studentPreviousUniversity;
    }

    private Date getExpelDateFromPreviousUniversity(String eduFromInfo) {
        Date deductionDateFromPreviousUniversity = null;
        DateFormat deductionDateFormatter = new SimpleDateFormat("dd.MM.yyyy");
        Pattern pattern = Pattern.compile(EXPEL_DATE_REGEXP);
        Matcher matcher = pattern.matcher(eduFromInfo);
        try {
            if (matcher.find()) {
                deductionDateFromPreviousUniversity = matcher.groupCount() > 0 ? deductionDateFormatter.parse(matcher.group(1)) : null;
            }
        } catch (ParseException e) {
            log.debug(e.getMessage());
        }
        return deductionDateFromPreviousUniversity;
    }

    @Override
    public StudentDegree getStudentDegreeFromData(ImportedData data) {
        Student student = getStudentFromData(data);
        Specialization specialization = getSpecializationFromData(data); //getSpeciality inside
        StudentDegree studentDegree = new StudentDegree();
        studentDegree.setActive(true);
        studentDegree.setStudent(student);
        studentDegree.setSpecialization(specialization);

        if (getStudentPreviousUniversityFromData(data) != null) {
            Set<StudentPreviousUniversity> studentPreviousUniversities = new HashSet<>();
            studentPreviousUniversities.add(getStudentPreviousUniversityFromData(data));
            studentDegree.setStudentPreviousUniversities(studentPreviousUniversities);
        }

        studentDegree.setSupplementNumber(data.getEducationId());
        studentDegree.setPayment(Payment.getPaymentFromUkrName(data.getPersonEducationPaymentTypeName()));
        studentDegree.setTuitionForm(TuitionForm.getTuitionFormFromUkrName(data.getEducationFormName()));
        if (data.getIsShortened().toUpperCase().equals("ТАК"))
            studentDegree.setTuitionTerm(TuitionTerm.SHORTENED);//(evaluateTuitionTermGuess(data.getEducationDateBegin(), data.getEducationDateEnd()));
        else
            studentDegree.setTuitionTerm(TuitionTerm.REGULAR);

        String previousEducationInfo[] = data.getEduDocInfo().split(";");
        for (int i = 0; i < previousEducationInfo.length; i++) previousEducationInfo[i] = previousEducationInfo[i].trim();
        if (previousEducationInfo.length > 0) {
            if (previousEducationInfo[0].indexOf('(') > 0)
                previousEducationInfo[0] = previousEducationInfo[0].substring(0, previousEducationInfo[0].indexOf('(')).trim();
            studentDegree.setPreviousDiplomaType(EducationDocument.getEducationDocumentByName(previousEducationInfo[0]));
        }
        if (previousEducationInfo.length > 1 && !previousEducationInfo[1].equals("")) {
            String number[] = previousEducationInfo[1].split(" ");
            studentDegree.setPreviousDiplomaNumber(number[0] + " № " + (number.length > 1 ? number[1] : ""));
        }
        try {
            studentDegree.setPreviousDiplomaDate(new SimpleDateFormat("dd.MM.yyyy").parse(previousEducationInfo[2]));
        } catch(Exception e) {}
        if (previousEducationInfo.length > 3) {
            String parts[] = previousEducationInfo[3].split(":");
            if (parts[1] != null) studentDegree.setPreviousDiplomaIssuedBy(parts[1]);
        }

        studentDegree.setAdmissionDate(parseDate(data.getEducationDateBegin()));
        Map<String, Object> admissionOrderNumberAndDate = getAdmissionOrderNumberAndDate(data.getRefillInfo());
        studentDegree.setAdmissionOrderNumber((String) admissionOrderNumberAndDate.get("admissionOrderNumber"));
        studentDegree.setAdmissionOrderDate((Date) admissionOrderNumberAndDate.get("admissionOrderDate"));
        if (!data.getCountry().equals(""))
            studentDegree.setCitizenship(Citizenship.getCitizenshipByCountryUkrName(data.getCountry()));
        else
            studentDegree.setCitizenship(Citizenship.UKR);
        return studentDegree;
    }

    private int getYearFromDateStringInImportedFile(String date) {
        int i = date.lastIndexOf('/');
        return Integer.parseInt(date.substring(i+1, i+3));
    }

    private TuitionTerm evaluateTuitionTermGuess(String educationBeginDate, String educationEndDate) {
        int educationDuration = getYearFromDateStringInImportedFile(educationEndDate) - getYearFromDateStringInImportedFile(educationBeginDate);
        if (educationDuration <= 3)
            return TuitionTerm.SHORTENED;
        else
            return TuitionTerm.REGULAR;
    }

    public Map<String, Object> getAdmissionOrderNumberAndDate(String refillInfo) {
        Map<String, Object> admissionOrderNumberAndDate = new HashMap<>();
        DateFormat admissionOrderDateFormatter = new SimpleDateFormat("dd.MM.yyyy");
        Pattern admissionPattern = Pattern.compile(ADMISSION_REGEXP);
        try {
            Matcher matcher = admissionPattern.matcher(refillInfo);
            if (matcher.find()) {
                admissionOrderNumberAndDate.put("admissionOrderNumber", matcher.groupCount() > 0 ? matcher.group(1).trim() : "");
                Date admissionOrderDate = matcher.groupCount() > 1 ? admissionOrderDateFormatter.parse(matcher.group(2).trim()) : null;
                admissionOrderNumberAndDate.put("admissionOrderDate", admissionOrderDate);
                return admissionOrderNumberAndDate;
            }
        } catch (ParseException e) {
            log.debug(e.getMessage());
        } catch (IllegalStateException e) {
            log.debug(e.getMessage());
        }
        return admissionOrderNumberAndDate;
    }

    @Override
    public void addSynchronizationReportForImportedData(ImportedData importedData, EdeboStudentDataSynchronizationReport edeboDataSyncronizationReport, int facultyId, Map<String, String> selectionParams) {
        if ((!(selectionParams.get("faculty").toUpperCase().equals(importedData.getFacultyName().toUpperCase())) && FacultyUtil.getUserFacultyIdInt()!=12)
                || !(selectionParams.get("degree") == null || selectionParams.get("degree").toUpperCase().equals(importedData.getQualificationGroupName().toUpperCase()))
                || !(selectionParams.get("speciality") == null || selectionParams.get("speciality").toUpperCase().equals(importedData.getFullSpecialityName().toUpperCase()))
        )
            return;
        StudentDegree studentDegreeFromData;
        if (isSpecialityPatternMatch(importedData)) {
            studentDegreeFromData = getStudentDegreeFromData(importedData);
            if (!isCriticalDataAvailable(studentDegreeFromData)) {
                String message = "Недостатньо інформації для синхронізації";
                edeboDataSyncronizationReport.addMissingPrimaryDataRed(new MissingPrimaryDataRedMessageBean(message, new StudentDegreePrimaryDataBean(importedData)));
                return;
            }
        } else {
            String message = "Неправильна освітня програма";
            edeboDataSyncronizationReport.addMissingPrimaryDataRed(new MissingPrimaryDataRedMessageBean(message, new StudentDegreePrimaryDataBean(importedData)));
            return;
        }

        Specialization specializationFromData = studentDegreeFromData.getSpecialization();
        Faculty facultyFromDb = facultyService.getByName(specializationFromData.getFaculty().getName());
        if (facultyFromDb == null) {
            String message = "Даний факультет відсутній";
            edeboDataSyncronizationReport.addMissingPrimaryDataRed(new MissingPrimaryDataRedMessageBean(message, new StudentDegreePrimaryDataBean(importedData)));
            return;
        }

        Speciality specialityFromDb = specialityService.findSpecialityByCodeAndName(specializationFromData.getSpeciality().getCode(),
                specializationFromData.getSpeciality().getName());
        if (specialityFromDb == null) {
            String message = "Дана спеціальність відсутня";
            edeboDataSyncronizationReport.addMissingPrimaryDataRed(new MissingPrimaryDataRedMessageBean(message, new StudentDegreePrimaryDataBean(importedData)));
            return;
        }

        Specialization specializationFromDB = specializationService.getByNameAndDegreeAndSpecialityAndFaculty(
                specializationFromData.getName(),
                specializationFromData.getDegree().getId(),
                specialityFromDb.getId(),
                facultyFromDb.getId());
        if (specializationFromDB == null) {
            if (Strings.isNullOrEmpty(specializationFromData.getName()) && specialityFromDb.getCode().length() == 3) {
                List<Specialization> specialitySpecializations = specializationService.getAllActiveBySpecialityAndDegree(specialityFromDb.getId(), facultyId, specializationFromData.getDegree().getId());
                if (specialitySpecializations.size() >= 0) {
                    specializationFromDB = specialitySpecializations.get(0);
                }
            }
        }
        if (specializationFromDB == null) {
            String message = "Дана освітня програма відсутня";
            edeboDataSyncronizationReport.addMissingPrimaryDataRed(new MissingPrimaryDataRedMessageBean(message, new StudentDegreePrimaryDataBean(importedData)));
            return;
        }
        studentDegreeFromData.setSpecialization(specializationFromDB);

        Set<StudentPreviousUniversity> studentPreviousUniversityFromData = studentDegreeFromData.getStudentPreviousUniversities();
        if (studentPreviousUniversityFromData.size() != 0) {
            Iterator<StudentPreviousUniversity> iterator = studentPreviousUniversityFromData.iterator();
            StudentPreviousUniversity studentPreviousUniversity = iterator.next();

            if (studentPreviousUniversity.getStudyStartDate() == null) {
                String message = "Відсутня дата початку навчання в попередньому ВНЗ";
                edeboDataSyncronizationReport.addMissingPrimaryDataRed(new MissingPrimaryDataRedMessageBean(
                        message,
                        new StudentDegreePrimaryDataBean(importedData))
                );
                return;
            }
            if (studentPreviousUniversity.getStudyEndDate() == null) {
                String message = "Відсутня дата закінчення навчання попереднього ВНЗ";
                edeboDataSyncronizationReport.addMissingPrimaryDataRed(new MissingPrimaryDataRedMessageBean(
                        message,
                        new StudentDegreePrimaryDataBean(importedData))
                );
                return;
            }
            if (Strings.isNullOrEmpty(studentPreviousUniversity.getUniversityName())) {
                String message = "Відсутня назва попереднього ВНЗ";
                edeboDataSyncronizationReport.addMissingPrimaryDataRed(new MissingPrimaryDataRedMessageBean(
                        message,
                        new StudentDegreePrimaryDataBean(importedData))
                );
                return;
            }
        }

        Student studentFromData = studentDegreeFromData.getStudent();
        Student studentFromDB = studentService.searchByFullNameAndBirthDate(
                studentFromData.getName(),
                studentFromData.getSurname(),
                studentFromData.getPatronimic(),
                studentFromData.getBirthDate()
        );

        if (studentFromDB == null) {
            edeboDataSyncronizationReport.addNoSuchStudentOrStudentDegreeInDbOrange(studentDegreeFromData);
            return;
        }
        studentDegreeFromData.getStudent().setId(studentFromDB.getId());

        StudentDegree studentDegreeFromDb = studentDegreeService.getByStudentIdAndSpecializationId(true, studentFromDB.getId(), specializationFromDB.getId());
        if (studentDegreeFromDb == null) {
            edeboDataSyncronizationReport.addNoSuchStudentOrStudentDegreeInDbOrange(studentDegreeFromData);
            return;
        }

        if (isSecondaryFieldsMatch(studentDegreeFromData, studentDegreeFromDb) &&
                isPreviousUniversityFieldsMatch(studentDegreeFromData.getStudentPreviousUniversities(), studentDegreeFromDb.getStudentPreviousUniversities())) {
            edeboDataSyncronizationReport.addSyncohronizedDegreeGreen(new StudentDegreePrimaryDataWithGroupBean(studentDegreeFromDb));
        } else {
            edeboDataSyncronizationReport.addUnmatchedSecondaryDataStudentDegreeBlue(studentDegreeFromData, studentDegreeFromDb);
        }
    }

    public boolean isSecondaryFieldsMatch(StudentDegree studentDegreeFromFile, StudentDegree studentDegreeFromDb) {
        return (EntityUtil.isValuesOfFieldsReturnedByGettersMatch(studentDegreeFromFile, studentDegreeFromDb, SECONDARY_STUDENT_DEGREE_FIELDS_TO_COMPARE) &&
                EntityUtil.isValuesOfFieldsReturnedByGettersMatch(studentDegreeFromFile.getStudent(), studentDegreeFromDb.getStudent(), SECONDARY_STUDENT_FIELDS_TO_COMPARE));
    }

    private boolean isPreviousUniversityFieldsMatch(Set<StudentPreviousUniversity> studentPreviousUniversityFromFile,
                                                    Set<StudentPreviousUniversity> studentPreviousUniversityFromDb) {
        if (studentPreviousUniversityFromDb.size() == 0 && studentPreviousUniversityFromFile.size() == 0) {
            return true;
        }

        for (StudentPreviousUniversity universityFromFile : studentPreviousUniversityFromFile) {
            for (StudentPreviousUniversity universityFromDb : studentPreviousUniversityFromDb) {
                if (EntityUtil.isValuesOfFieldsReturnedByGettersMatch(universityFromFile, universityFromDb, STUDENT_PREVIOUS_UNIVERSITY_FIELDS_TO_COMPARE)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void getAllIdForAbsentInFileStudentDegrees(EdeboStudentDataSynchronizationReport edeboDataSyncronizationReport,
                                                       Map<String, String> selectionParams,
                                                       int facultyId) {
        List<Integer> idNotForAbsentInFileStudentDegrees = edeboDataSyncronizationReport.getSynchronizedStudentDegreesGreen().stream().
                map(studentDegree -> studentDegree.getId()).collect(Collectors.toList());
        idNotForAbsentInFileStudentDegrees.addAll(edeboDataSyncronizationReport.getUnmatchedSecondaryDataStudentDegreesBlue().stream().
                map(studentDegree -> studentDegree.getStudentDegreeFromDb().getId()).collect(Collectors.toList()));
        int degreeId = 0;
        if (selectionParams.get("degree") != null) {
            degreeId = degreeService.getByName(selectionParams.get("degree")).getId();
        }
        int specialityId = 0;
        if (selectionParams.get("speciality") != null) {
            String code = "", name = "";
            String specialityParts[] = selectionParams.get("speciality").split(" ", 2);
            if (specialityParts.length == 2) {
                code = specialityParts[0];
                name = specialityParts[1];
                specialityId = specialityService.findSpecialityByCodeAndName(code, name).getId();
            }
        }
        List<StudentDegree> studentDegrees = studentDegreeService.getAllNotInImportData(idNotForAbsentInFileStudentDegrees, facultyId, degreeId, specialityId);
        for (StudentDegree studentDegree : studentDegrees) {
            edeboDataSyncronizationReport.addAbsentInFileStudentDegreeYellow(new StudentDegreePrimaryDataWithGroupBean(studentDegree));
        }
    }

    private Date parseDate(String fileDate) {
        try {
            DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
            formatter.setTimeZone(TimeZone.getTimeZone("EET"));
            return formatter.parse(fileDate);
        } catch (ParseException e) {
            log.debug(e.getMessage());
        }
        return null;
    }

    private void sortingSyncohronizedDegreeGreen(EdeboStudentDataSynchronizationReport edeboDataSyncronizationReport) {
        edeboDataSyncronizationReport.setSynchronizedStudentDegreesGreen(
                edeboDataSyncronizationReport.getSynchronizedStudentDegreesGreen()
                        .stream().sorted((sd1, sd2) -> (
                        sd1.getDegreeName() + " "
                                + sd1.getFullSpecialityName() + " "
                                + sd1.getFullSpecializationName() + " "
                                + sd1.getGroupName() + " "
                                + sd1.getLastName() + " "
                                + sd1.getFirstName() + " "
                                + sd1.getMiddleName())
                        .compareTo(
                                sd2.getDegreeName() + " "
                                        + sd2.getFullSpecialityName() + " "
                                        + sd2.getFullSpecializationName() + " "
                                        + sd2.getGroupName() + " "
                                        + sd2.getLastName() + " "
                                        + sd2.getFirstName() + " "
                                        + sd2.getMiddleName()))
                        .collect(Collectors.toList())
        );
    }

    private void sortingMissingPrimaryDataRed(EdeboStudentDataSynchronizationReport edeboDataSyncronizationReport) {
        edeboDataSyncronizationReport.setMissingPrimaryDataRed(
                edeboDataSyncronizationReport.getMissingPrimaryDataRed()
                        .stream().sorted((sd1, sd2) -> (
                        sd1.getStudentDegreePrimaryData().getDegreeName() + " "
                                + sd1.getStudentDegreePrimaryData().getFullSpecialityName() + " "
                                + sd1.getStudentDegreePrimaryData().getFullSpecializationName() + " "
                                + sd1.getStudentDegreePrimaryData().getLastName() + " "
                                + sd1.getStudentDegreePrimaryData().getFirstName() + " "
                                + sd1.getStudentDegreePrimaryData().getMiddleName())
                        .compareTo(
                                sd2.getStudentDegreePrimaryData().getDegreeName() + " "
                                        + sd2.getStudentDegreePrimaryData().getFullSpecialityName() + " "
                                        + sd2.getStudentDegreePrimaryData().getFullSpecializationName() + " "
                                        + sd2.getStudentDegreePrimaryData().getLastName() + " "
                                        + sd2.getStudentDegreePrimaryData().getFirstName() + " "
                                        + sd2.getStudentDegreePrimaryData().getMiddleName()))
                        .collect(Collectors.toList())
        );
    }

    private void sortingAbsentInFileStudentDegreeYellow(EdeboStudentDataSynchronizationReport edeboDataSyncronizationReport) {
        edeboDataSyncronizationReport.setAbsentInFileStudentDegreesYellow(
                edeboDataSyncronizationReport.getAbsentInFileStudentDegreesYellow()
                        .stream().sorted((sd1, sd2) -> (
                        sd1.getDegreeName() + " "
                                + sd1.getFullSpecialityName() + " "
                                + sd1.getFullSpecializationName() + " "
                                + sd1.getGroupName() + " "
                                + sd1.getLastName() + " "
                                + sd1.getFirstName() + " "
                                + sd1.getMiddleName())
                        .compareTo(
                                sd2.getDegreeName() + " "
                                        + sd2.getFullSpecialityName() + " "
                                        + sd2.getFullSpecializationName() + " "
                                        + sd2.getGroupName() + " "
                                        + sd2.getLastName() + " "
                                        + sd2.getFirstName() + " "
                                        + sd2.getMiddleName()))
                        .collect(Collectors.toList())
        );
    }

    private void sortingNoSuchStudentOrStudentDegreeInDbOrange(EdeboStudentDataSynchronizationReport edeboDataSyncronizationReport) {
        edeboDataSyncronizationReport.setNoSuchStudentOrSuchStudentDegreeInDbOrange(
                edeboDataSyncronizationReport.getNoSuchStudentOrSuchStudentDegreeInDbOrange()
                        .stream().sorted((sd1, sd2) ->
                        (sd1.getSpecialization().getDegree().getName() + " "
                                + sd1.getSpecialization().getSpeciality().getCode() + " "
                                + sd1.getSpecialization().getSpeciality().getName() + " "
                                + sd1.getSpecialization().getName() + " "
                                + sd1.getPreviousDiplomaType() + " "
                                + sd1.getStudent().getSurname() + " "
                                + sd1.getStudent().getName() + " "
                                + sd1.getStudent().getPatronimic())
                                .compareTo(sd2.getSpecialization().getDegree().getName() + " "
                                        + sd2.getSpecialization().getSpeciality().getCode() + " "
                                        + sd2.getSpecialization().getSpeciality().getName() + " "
                                        + sd2.getSpecialization().getName() + " "
                                        + sd2.getPreviousDiplomaType() + " "
                                        + sd2.getStudent().getSurname() + " "
                                        + sd2.getStudent().getName() + " "
                                        + sd2.getStudent().getPatronimic()))
                        .collect(Collectors.toList())
        );
    }
}
