package ua.edu.chdtu.deanoffice.service.document.report.personalstatement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.Constants;
import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;
import ua.edu.chdtu.deanoffice.repository.CourseRepository;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;
import ua.edu.chdtu.deanoffice.service.*;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;
import ua.edu.chdtu.deanoffice.service.document.report.personalstatement.reports.PracticeReport;
import ua.edu.chdtu.deanoffice.service.document.report.personalstatement.reports.AcademicVacationReport;
import ua.edu.chdtu.deanoffice.service.document.report.personalstatement.reports.QualificationReport;
import ua.edu.chdtu.deanoffice.util.DateUtil;
import ua.edu.chdtu.deanoffice.util.GradeUtil;
import ua.edu.chdtu.deanoffice.util.LanguageUtil;
import ua.edu.chdtu.deanoffice.util.PersonUtil;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static ua.edu.chdtu.deanoffice.service.document.DocumentIOService.TEMPLATES_PATH;
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.*;
import static ua.edu.chdtu.deanoffice.util.LanguageUtil.transliterate;

@Service
public class PersonalStatementService {

    private static final String TEMPLATE_PATH_FRONT = TEMPLATES_PATH + "PersonalWrapperFront.docx";
    private static final String TEMPLATE_PATH = TEMPLATES_PATH + "PersonalStatement.docx";
    private static final String TEMPLATE_PATH_BACK = TEMPLATES_PATH + "PersonalWrapperBack.docx";
    private static final int NUMBER_OF_MANDATORY_ROWS_IN_FIRST_SEMESTER_TABLE = 10;
    private static final int NUMBER_OF_MANDATORY_ROWS_IN_TABLE = 20;
    @Autowired
    private StudentDegreeRepository studentDegreeRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private GradeService gradeService;
    @Autowired
    private DocumentIOService documentIOService;
    @Autowired
    private CourseForGroupService courseForGroupService;
    @Autowired
    StudentDegreeService studentDegreeService;
    @Autowired
    StudentAcademicVacationService studentAcademicVacationService;

    public File formDocument(Integer year, List<Integer> studentDegreeIds)
            throws Docx4JException, IOException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(TEMPLATE_PATH);
        List<StudentDegree> studentDegrees = studentDegreeRepository.getAllByIds(studentDegreeIds);
        studentDegrees = studentDegrees.stream().filter(sd -> getStudentStudyYear(sd, year) >= 0).collect(Collectors.toList());
        YearGrades yearGrades = new YearGrades(getGradeMap(year, studentDegrees, SemesterType.FIRST), getGradeMap(year, studentDegrees, SemesterType.SECOND));
        generateTables(template, yearGrades, year);
        Set<StudentGroup> groups = yearGrades.getGradeMapForFirstSemester().keySet().stream().map(sd -> sd.getStudentGroup()).collect(Collectors.toSet());
        return documentIOService.saveDocumentToTemp(template, transliterate(generateFileName(groups)), FileFormatEnum.DOCX);
    }

    private String generateFileName(Set<StudentGroup> groups) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("PersonalFile_");
        groups.forEach(groupId -> {
            stringBuilder.append(groupId.getName()).append("_");
        });
        return stringBuilder.toString();
    }

    private Map<StudentDegree, List<Grade>> getGradeMap(Integer year, List<StudentDegree> studentDegrees, SemesterType semesterType) {
        Map<StudentGroup, List<StudentDegree>> groupsWithStudents = studentDegrees.stream().collect(Collectors.groupingBy(sd -> sd.getStudentGroup()));
        Map<StudentGroup, List<Integer>> groupsWithStudentIds = groupsWithStudents.entrySet().stream().collect(Collectors.toMap(
                entry -> entry.getKey(),
                entry -> entry.getValue().stream().map(BaseEntity::getId).collect(Collectors.toList())
        ));
        Set<StudentGroup> groups = groupsWithStudents.keySet();
        Map<StudentGroup, List<Integer>> courseIdsForGroups = groups.stream().collect(Collectors.toMap(
                group -> group,
                group -> courseRepository.getByGroupIdAndSemester(group.getId(), getSemesterByYearForGroup(year, group) + semesterType.getNumber() - 1)
                        .stream()
                        .map(BaseEntity::getId)
                        .collect(Collectors.toList())
        ));
        return gradeService.getGradeMapForStudents(groupsWithStudentIds, courseIdsForGroups);
    }

    private void generateTables(WordprocessingMLPackage template, YearGrades yearGrades, Integer year) {
        Tbl templateTable = (Tbl) getAllElementsFromObject(template.getMainDocumentPart(), Tbl.class).get(0);
        List<StudentDegree> studentDegrees = new ArrayList<>(yearGrades.getGradeMapForFirstSemester().keySet());
        studentDegrees.sort(new GroupStudentDegreeComparator());
        for (StudentDegree studentDegree : studentDegrees) {
            Tbl table = XmlUtils.deepCopy(templateTable);
            fillFirstRow(table, studentDegree.getStudent());
            formSemesterInTable(table, yearGrades.getGradeMapForFirstSemester().get(studentDegree), year, SemesterType.FIRST, studentDegree);
            formSemesterInTable(table, yearGrades.getGradeMapForSecondSemester().get(studentDegree), year, SemesterType.SECOND, studentDegree);
            fillLastRow(table, studentDegree, year);
            template.getMainDocumentPart().addObject(table);
        }
        template.getMainDocumentPart().getContent().remove(0);
    }

    private void fillFirstRow(Tbl table, Student student) {
        List<Tr> tableRows = (List<Tr>) (Object) getAllElementsFromObject(table, Tr.class);
        replaceInRow(tableRows.get(0), getStudentDictionary(student));
    }

    private void formSemesterInTable(Tbl table, List<Grade> grades, Integer year, SemesterType semesterType, StudentDegree studentDegree) {
        List<Tr> tableRows = (List<Tr>) (Object) getAllElementsFromObject(table, Tr.class);
        int currentIndex = 2, rowNumber = NUMBER_OF_MANDATORY_ROWS_IN_FIRST_SEMESTER_TABLE;
        if (semesterType == SemesterType.SECOND) {
            currentIndex = tableRows.size() - 2;
            rowNumber = NUMBER_OF_MANDATORY_ROWS_IN_TABLE;
        }
        Tr rowToCopy = tableRows.get(currentIndex);
        if (grades != null) {
            fillRowByGrade(tableRows.get(currentIndex - 1), grades.get(0), year);
            for (Grade grade : grades.subList(1, grades.size())) {
                Tr newRow = XmlUtils.deepCopy(rowToCopy);
                fillRowByGrade(newRow, grade, year);
                table.getContent().add(currentIndex, newRow);
                currentIndex++;
            }
        } else fillRowByEmpty(tableRows.get(currentIndex - 1), studentDegree, year);
        for (int i = currentIndex; i < rowNumber; i++) {
            Tr newRow = XmlUtils.deepCopy(rowToCopy);
            fillRowByLost(newRow);
            table.getContent().add(currentIndex, newRow);
            currentIndex++;
        }
        table.getContent().remove(currentIndex);
    }

    private Map<String, String> getGradeDictionaryForEmpty(StudentDegree studentDegree, Integer year) {
        Map<String, String> result = new HashMap<>();
        result.put("sy", "НАВЧАЛЬНИЙ РІК");
        String gradeNumberYear = "";
        gradeNumberYear = getYearName(getStudentStudyYear(studentDegree, year)).toUpperCase() + " " + year + "-" + (year + 1);
        result.put("f", getSemesterName(getStudentStudyYear(studentDegree, year) * 2).toUpperCase());
        result.put("s", getSemesterName((getStudentStudyYear(studentDegree, year) * 2) + 1).toUpperCase());
        result.put("n", gradeNumberYear);
        return result;
    }

    private Map<String, String> getGradeDictionary(Grade grade, Integer year) {
        Map<String, String> result = new HashMap<>();
        result.put("sy", "НАВЧАЛЬНИЙ РІК");
        String gradeNumberYear = "";
        gradeNumberYear = getYearName(getStudentStudyYear(grade.getStudentDegree(), year)).toUpperCase() + " " + year + "-" + (year + 1);
        result.put("f", getSemesterName(getStudentStudyYear(grade.getStudentDegree(), year) * 2).toUpperCase());
        result.put("s", getSemesterName((getStudentStudyYear(grade.getStudentDegree(), year) * 2) + 1).toUpperCase());
        result.put("n", gradeNumberYear);
        result.put("subj", grade.getCourse().getCourseName().getName());
        result.put("t", resolveTypeField(grade));
        result.put("h", grade.getCourse().getHours().toString());
        result.put("c", grade.getCourse().getCredits().toString());
        String gradeFieldValue = "";
        if (grade.getGrade() != null && GradeUtil.isEnoughToPass(grade.getPoints())) {
            gradeFieldValue = grade.getCourse().getKnowledgeControl().isGraded() ? grade.getGrade().toString() : "зарах";
        }
        result.put("g", gradeFieldValue);
        if (GradeUtil.isEnoughToPass(grade.getPoints())) {
            result.put("p", grade.getPoints().toString());
        }
        if (grade.getEcts() != null && GradeUtil.isEnoughToPass(grade.getPoints())) {
            result.put("e", grade.getEcts().toString());
        }
        CourseForGroup courseForGroup = courseForGroupService.getCourseForGroup(grade.getStudentDegree().getStudentGroup().getId(), grade.getCourse().getId());
        if (courseForGroup.getExamDate() != null && GradeUtil.isEnoughToPass(grade.getPoints())) {
            String date = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(courseForGroup.getExamDate());
            result.put("d", date);
        }
        return result;
    }

    private Map<String, String> getLostDictionary() {
        Map<String, String> result = new HashMap<>();
        result.put("subj", "");
        result.put("t", "");
        result.put("h", "");
        result.put("c", "");
        result.put("g", "");
        result.put("p", "");
        result.put("e", "");
        result.put("d", "");
        return result;
    }

    private void fillRowByEmpty(Tr row, StudentDegree studentDegree, Integer year) {
        replaceInRow(row, getGradeDictionaryForEmpty(studentDegree, year));
    }

    private void fillRowByGrade(Tr row, Grade grade, Integer year) {
        replaceInRow(row, getGradeDictionary(grade, year));
    }

    private void fillRowByLost(Tr row) {
        replaceInRow(row, getLostDictionary());
    }

    private String resolveTypeField(Grade grade) {
        switch (grade.getCourse().getKnowledgeControl().getId()) {
            case Constants.COURSEWORK:
                return "(КР)";
            case Constants.COURSE_PROJECT:
                return "(КП)";
            case Constants.INTERNSHIP:
            case Constants.NON_GRADED_INTERNSHIP:
                return "(пр)";
            case Constants.EXAM:
                return "(і)";
            case Constants.CREDIT:
            case Constants.DIFFERENTIATED_CREDIT:
            case Constants.STATE_EXAM:
            case Constants.ATTESTATION:
            default:
                return "(з)";
        }
    }

    private Map<String, String> getStudentDictionary(Student student) {
        Map<String, String> result = new HashMap<>();
        result.put("stud", student.getFullNameUkr());
        return result;
    }

    private void fillLastRow(Tbl table, StudentDegree studentDegree, Integer year) {
        List<Tr> tableRows = (List<Tr>) (Object) getAllElementsFromObject(table, Tr.class);
        replaceInRow(tableRows.get(tableRows.size() - 1), getLastRowDictionary(year, studentDegree));
    }

    private Map<String, String> getLastRowDictionary(Integer year, StudentDegree studentDegree) {
        Map<String, String> result = new HashMap<>();
        result.put("nc", "Переведений на " + getYearName((getStudentStudyYear(studentDegree, year + 1))) + " курс. Наказ від «_____»________20___року №____");
        return result;
    }

    private Integer getSemesterByYearForGroup(Integer year, StudentGroup studentGroup) {
        return (year - studentGroup.getCreationYear() + studentGroup.getBeginYears() - 1) * 2 + 1;
    }

    public int getStudentStudyYear(StudentDegree studentDegree, int year) {
        return year - studentDegree.getStudentGroup().getCreationYear() + studentDegree.getStudentGroup().getBeginYears() - 1;
    }

    private String getYearName(Integer year) {
        final String[] YEAR_NAMES = {"перший", "другий", "третій", "четвертий", "п'ятий", "шостий"};
        return YEAR_NAMES[year];
    }

    private String getSemesterName(Integer semester) {
        final String[] SEMESTER_NAMES = {"перший", "другий", "третій", "четвертий", "п'ятий", "шостий", "сьомий", "восьмий",
                "дев'ятий", "десятий", "одинадцятий", "дванадцятий"};
        return SEMESTER_NAMES[semester];
    }

    public synchronized File preparePersonalWrapperFront(Integer studentDegreeId) throws Docx4JException, IOException {
        StudentDegree studentDegree = studentDegreeService.getById(studentDegreeId);
        return documentIOService.saveDocumentToTemp(fillFrontPage(TEMPLATE_PATH_FRONT, studentDegree),
                LanguageUtil.transliterate(studentDegree.getStudent().getName()) + "Front" + ".docx", FileFormatEnum.DOCX);
    }

    public synchronized File preparePersonalWrapperBack(Integer studentDegreeId) throws Docx4JException, IOException {
        StudentDegree studentDegree = studentDegreeService.getById(studentDegreeId);
        return documentIOService.saveDocumentToTemp(fillBackPage(TEMPLATE_PATH_BACK, studentDegree),
                LanguageUtil.transliterate(studentDegree.getStudent().getName()) + "Back" + ".docx", FileFormatEnum.DOCX);
    }

    private WordprocessingMLPackage fillFrontPage(String templateName,
                                                  StudentDegree studentDegree) throws Docx4JException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(templateName);
        Map<String, String> commonDict = new HashMap<>();
        commonDict.put("Faculty", studentDegree.getSpecialization().getFaculty().getName());
        commonDict.put("Degree", studentDegree.getSpecialization().getDegree().getName());
        commonDict.put("Speciality", studentDegree.getSpecialization().getSpeciality().getName());
        commonDict.put((studentDegree.getSpecialization().getCode() == null) ? "EducationalProgram" : "Specialization",
                studentDegree.getSpecialization().getName());
        commonDict.put("Name", studentDegree.getStudent().getFullNameUkr());
        commonDict.put("BDate", (studentDegree.getStudent().getBirthDate() != null) ?
                DateUtil.getDate(studentDegree.getStudent().getBirthDate()) : "");
        commonDict.put("GrY", (studentDegree.getPreviousDiplomaDate() != null) ?
                DateUtil.getYear(studentDegree.getPreviousDiplomaDate()) : "");
        commonDict.put("Graduated", studentDegree.getPreviousDiplomaIssuedBy());
        commonDict.put("GradSer", studentDegree.getPreviousDiplomaNumber());
        commonDict.put("POfRes", studentDegree.getStudent().getRegistrationAddress());
        commonDict.put("PhoneNum", studentDegree.getStudent().getTelephone());
        commonDict.put("AdmPriv", studentDegree.getStudent().getPrivilege().getName());
        commonDict.put("AdmDate", studentDegree.getAdmissionDate() != null ?
                DateUtil.getDate(studentDegree.getAdmissionDate()) : "");
        commonDict.put("AdmSer", studentDegree.getAdmissionOrderNumber());
        fillAcademicVacationTable(template, prepareAcademicVacationReports(studentDegree.getId()));
        replaceTextPlaceholdersInTemplate(template, commonDict);
        return template;
    }

    private WordprocessingMLPackage fillBackPage(String templateName,
                                                 StudentDegree studentDegree) throws Docx4JException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(templateName);
        Map<String, String> commonDict = new HashMap<>(prepareStudentsGrade(studentDegree.getId()));
        fillPracticeTable(template, preparePracticeReports(studentDegree.getId()));
        fillQualificationTable(template,prepareQualificationReport(studentDegree.getId()));
        if (studentDegree.getThesisName().length() > 55) {
            commonDict.put("ThesisName", studentDegree.getThesisName().substring(0, 55));
            commonDict.put("ThesisName2", studentDegree.getThesisName().substring(55));
        } else {
            commonDict.put("ThesisName", studentDegree.getThesisName());
        }
        commonDict.put("DeanName", PersonUtil.makeInitialsSurnameLast(studentDegree.getSpecialization().getFaculty().getDean()));
        replaceTextPlaceholdersInTemplate(template, commonDict);
        return template;
    }

    private Map<String, String> prepareStudentsGrade(Integer studentDegreeId) {
        List<Integer> kCTypes = new ArrayList<>();
        kCTypes.add(Constants.INTERNSHIP);
        kCTypes.add(Constants.ATTESTATION);
        kCTypes.add(Constants.COURSE_PROJECT);
        kCTypes.add(Constants.COURSEWORK);
        kCTypes.add(Constants.CREDIT);
        kCTypes.add(Constants.DIFFERENTIATED_CREDIT);
        kCTypes.add(Constants.EXAM);
        kCTypes.add(Constants.STATE_EXAM);
        kCTypes.add(Constants.NON_GRADED_INTERNSHIP);
        List<Grade> grades = gradeService.getGradesByStudetDegreeIdAndKCTypes(studentDegreeId, kCTypes);
        Long perfect = grades.stream().filter(grade -> (grade.getPoints() >= EctsGrade.A.getLowerBound())).count(),
                good = grades.stream().filter(grade -> (grade.getPoints() <= EctsGrade.B.getUpperBound()
                        && grade.getPoints() >= EctsGrade.C.getLowerBound())).count(),
                satisfactory = grades.stream().filter(grade -> (grade.getPoints() <= EctsGrade.D.getUpperBound()
                        && grade.getPoints() >= EctsGrade.E.getLowerBound())).count();
        Integer amount = grades.size();

        DecimalFormat df = new DecimalFormat("0.00");
        Map<String, String> result = new HashMap<>();
        result.put("Amount", String.valueOf(amount));
        result.put("P", String.valueOf(perfect));
        result.put("Pp", String.valueOf(df.format(perfect.doubleValue() / amount.doubleValue() * 100)));
        result.put("G", String.valueOf(good));
        result.put("Gp", String.valueOf(df.format(good.doubleValue() / amount.doubleValue() * 100)));
        result.put("S", String.valueOf(satisfactory));
        result.put("Sp", String.valueOf(df.format(satisfactory.doubleValue() / amount.doubleValue() * 100)));
        return result;
    }

    private List<AcademicVacationReport> prepareAcademicVacationReports(Integer studentDegreeId) {
        List<AcademicVacationReport> academicVacationReports = new ArrayList<>();
        List<StudentAcademicVacation> studentAcademicVacations = studentAcademicVacationService.getByDegreeId(studentDegreeId);
        for (StudentAcademicVacation studentAcademicVacation : studentAcademicVacations)
            academicVacationReports.add(new AcademicVacationReport(
                    String.valueOf(studentAcademicVacation.getStudyYear()),
                    studentAcademicVacation.getOrderNumber(),
                    studentAcademicVacation.getOrderDate() != null ?
                            DateUtil.getDate(studentAcademicVacation.getOrderDate()) : "",
                    studentAcademicVacation.getOrderReason().getName()));
        return academicVacationReports;
    }

    private List<QualificationReport> prepareQualificationReport(Integer studentDegreeId) {
        List<QualificationReport> qualificationReports = new ArrayList<>();
        List<Integer> kCTypes = new ArrayList<>();
        kCTypes.add(Constants.ATTESTATION);
        List<Grade> grades = gradeService.getGradesByStudetDegreeIdAndKCTypes(studentDegreeId, kCTypes);
        int number = 1;
        for (Grade grade : grades) {
            qualificationReports.add(new QualificationReport(
                    grade.getCourse().getCourseName().getName(),
                    number++,
                    grade.getGrade(),
                    grade.getPoints(),
                    EctsGrade.getEctsGrade(grade.getPoints()).getNationalGradeUkr(grade)));
        }
        return qualificationReports;
    }

    private void fillQualificationTable(WordprocessingMLPackage template, List<QualificationReport> qualificationReports){
        Tbl tempTable = findTable(template, "Кваліфікаційний іспит та/або кваліфікаційна робота");
        if (tempTable == null) return;
        Tr templateRow = getTableRow(tempTable, 2);
        int rowToAddIndex = 2;
        for (QualificationReport qualificationReport : qualificationReports) {
            Map<String, String> replacements = qualificationReport.getDictionary();
            addRowToTable(tempTable, templateRow, rowToAddIndex, replacements);
            rowToAddIndex++;
        }
        tempTable.getContent().remove(templateRow);
    }

    private void fillAcademicVacationTable(WordprocessingMLPackage template, List<AcademicVacationReport> academicVacationReports) {
        Tbl tempTable = findTable(template, "Курс");
        if (tempTable == null) return;
        Tr templateRow = getTableRow(tempTable, 1);
        int rowToAddIndex = 1;
        for (AcademicVacationReport academicVacationReport : academicVacationReports) {
            Map<String, String> replacements = academicVacationReport.getDictionary();
            addRowToTable(tempTable, templateRow, rowToAddIndex, replacements);
            rowToAddIndex++;
        }
        tempTable.getContent().remove(templateRow);
    }

    private Tr getTableRow(Tbl table, int row) {
        return (Tr) getAllElementsFromObject(table, Tr.class).get(row);
    }

    private List<PracticeReport> preparePracticeReports(Integer studentDegreeId) {
        List<PracticeReport> practiceReports = new ArrayList<>();
        List<Integer> practiceKCTypes = new ArrayList<>();
        practiceKCTypes.add(Constants.INTERNSHIP);
        List<Grade> grades = gradeService.getGradesByStudetDegreeIdAndKCTypes(studentDegreeId, practiceKCTypes);
        int number = 1;
        for (Grade grade : grades)
            practiceReports.add(new PracticeReport(grade.getCourse().getCourseName().getName(),
                    number++,
                    grade.getGrade(),
                    grade.getPoints(),
                    EctsGrade.getEctsGrade(grade.getPoints()).getNationalGradeUkr(grade)));
        return practiceReports;
    }

    private void fillPracticeTable(WordprocessingMLPackage template, List<PracticeReport> practiceReports) {
        Tbl tempTable = findTable(template, "Назва практики");
        if (tempTable == null) return;
        Tr templateRow;
        int rowToAddIndex = 2;
        for (PracticeReport PracticeReport : practiceReports) {
            Map<String, String> replacements = PracticeReport.getDictionary();
            templateRow = getTableRow(tempTable, rowToAddIndex);
            replaceInRow(templateRow, replacements);
            rowToAddIndex++;
        }
    }

    @Getter
    private enum SemesterType {
        FIRST(1),
        SECOND(2);
        private int number;

        SemesterType(int number) {
            this.number = number;
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private class YearGrades {
        private Map<StudentDegree, List<Grade>> gradeMapForFirstSemester;
        private Map<StudentDegree, List<Grade>> gradeMapForSecondSemester;
    }
}
