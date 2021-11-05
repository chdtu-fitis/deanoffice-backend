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
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.entity.EctsGrade;
import ua.edu.chdtu.deanoffice.entity.Grade;
import ua.edu.chdtu.deanoffice.entity.SelectiveCourse;
import ua.edu.chdtu.deanoffice.entity.SelectiveCoursesStudentDegrees;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentAcademicVacation;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;
import ua.edu.chdtu.deanoffice.repository.GradeRepository;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;
import ua.edu.chdtu.deanoffice.service.GradeService;
import ua.edu.chdtu.deanoffice.service.StudentAcademicVacationService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.course.selective.SelectiveCoursesStudentDegreesService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;
import ua.edu.chdtu.deanoffice.service.document.TemplateUtil;
import ua.edu.chdtu.deanoffice.service.document.report.personalstatement.reports.AcademicVacationReport;
import ua.edu.chdtu.deanoffice.service.document.report.personalstatement.reports.PracticeReport;
import ua.edu.chdtu.deanoffice.service.document.report.personalstatement.reports.QualificationReport;
import ua.edu.chdtu.deanoffice.util.DateUtil;
import ua.edu.chdtu.deanoffice.util.GradeUtil;
import ua.edu.chdtu.deanoffice.util.PersonUtil;
import ua.edu.chdtu.deanoffice.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static ua.edu.chdtu.deanoffice.service.document.DocumentIOService.TEMPLATES_PATH;
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.*;
import static ua.edu.chdtu.deanoffice.util.LanguageUtil.transliterate;

@Service
public class StudentOneYearGradesAbstractService {

    private static final String TEMPLATE_PATH_FRONT = TEMPLATES_PATH + "PersonalWrapperFront.docx";
    private static final String TEMPLATE_PATH = TEMPLATES_PATH + "PersonalStatement.docx";
    private static final String TEMPLATE_PATH_BACK = TEMPLATES_PATH + "PersonalWrapperBack.docx";
    private static final int NUMBER_OF_MANDATORY_ROWS_IN_FIRST_SEMESTER_TABLE = 10;
    private static final int NUMBER_OF_MANDATORY_ROWS_IN_TABLE = 20;
    @Autowired
    private StudentDegreeRepository studentDegreeRepository;
    @Autowired
    private GradeService gradeService;
    @Autowired
    private GradeRepository gradeRepository;
    @Autowired
    private SelectiveCoursesStudentDegreesService selectiveCoursesStudentDegreesService;
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

    private Map<StudentDegree, List<StudentGradeAbstractBean>> getGradeMap(Integer year, List<StudentDegree> studentDegrees, SemesterType semesterType) {
        Map<StudentGroup, List<StudentDegree>> groupsWithStudents = studentDegrees.stream().collect(Collectors.groupingBy(sd -> sd.getStudentGroup()));
        Set<StudentGroup> groups = groupsWithStudents.keySet();
        Map<StudentGroup, List<CourseForGroup>> coursesForGroups = groups.stream().collect(Collectors.toMap(
                group -> group,
                group -> courseForGroupService.getCoursesForGroupBySemester(group.getId(), getSemesterByYearForGroup(year, group) + semesterType.getNumber() - 1)
        ));

        Map<StudentDegree, List<Integer>> selectiveCourseIdsForStudent = studentDegrees.stream().collect(Collectors.toMap(
                studentDegree -> studentDegree,
                studentDegree -> selectiveCoursesStudentDegreesService.getSelectiveCoursesByStudentDegreeIdAndSemester(studentDegree.getId(), (studentDegreeService.getRealStudentDegreeYear(studentDegree,year)-1) * 2 + semesterType.getNumber())
                                                                            .stream()
                                                                            .map(SelectiveCoursesStudentDegrees::getSelectiveCourse)
                                                                            .map(SelectiveCourse::getCourse)
                                                                            .map(BaseEntity::getId)
                                                                            .collect(Collectors.toList())
        ));

        Map<StudentDegree, List<StudentGradeAbstractBean>> gradeMapForStudentsCourses = getGradeMapForStudents(groupsWithStudents, coursesForGroups);
        Map<StudentDegree, List<StudentGradeAbstractBean>> gradeMapForStudentsSelectiveCourses = getGradeMapForStudents(selectiveCourseIdsForStudent);
        Map<StudentDegree, List<StudentGradeAbstractBean>> fullGradeMapForStudent = concatStudentOneYearGrades(gradeMapForStudentsCourses,gradeMapForStudentsSelectiveCourses);

        return sortByKnowledgeControls(fullGradeMapForStudent);
    }

    public Map<StudentDegree, List<StudentGradeAbstractBean>> sortByKnowledgeControls(Map<StudentDegree, List<StudentGradeAbstractBean>> fullGradeMapForStudent) {
        for (StudentDegree studentDegree :fullGradeMapForStudent.keySet()){
            Collections.sort(fullGradeMapForStudent.get(studentDegree), new Comparator<StudentGradeAbstractBean>() {
                @Override
                public int compare(StudentGradeAbstractBean o1, StudentGradeAbstractBean o2) {
                    return new Integer(o1.getGrade().getCourse().getKnowledgeControl().getId()).compareTo(
                            o2.getGrade().getCourse().getKnowledgeControl().getId());

                }
            });
        }
        return fullGradeMapForStudent;
    }

    //For regular courses
    public Map<StudentDegree, List<StudentGradeAbstractBean>> getGradeMapForStudents(Map<StudentGroup, List<StudentDegree>> groupsWithStudents,
                                                                                     Map<StudentGroup, List<CourseForGroup>> coursesForGroup) {
        Map<StudentDegree, List<StudentGradeAbstractBean>> gradesForStudents = new HashMap<StudentDegree, List<StudentGradeAbstractBean>>();

        for (StudentGroup group : groupsWithStudents.keySet()) {
            List<StudentDegree> studentDegrees = groupsWithStudents.get(group);
            Map<Integer, Date> courseIdsWithDate = coursesForGroup.get(group)
                    .stream()
                    .collect(HashMap::new, (m,v)->m.put(v.getCourse().getId(), v.getExamDate()), HashMap::putAll);

            Map<StudentDegree, List<StudentGradeAbstractBean>> gradesForGroup = studentDegrees.stream().collect(
                    toMap(studentDegree -> studentDegree,
                            studentDegree -> getStudentOneYearGradesBean(studentDegree.getId(),courseIdsWithDate)));
            gradesForStudents = concatStudentOneYearGrades(gradesForStudents,gradesForGroup);
        }

        return gradesForStudents;
    }

    //For selective courses
    public Map<StudentDegree, List<StudentGradeAbstractBean>> getGradeMapForStudents(Map<StudentDegree, List<Integer>> selectiveCourseIdsForStudent) {
        Map<StudentDegree, List<StudentGradeAbstractBean>> gradesForSelective = new HashMap<StudentDegree, List<StudentGradeAbstractBean>>();
        Set<StudentDegree> studentDegrees = selectiveCourseIdsForStudent.keySet();
        gradesForSelective = studentDegrees.stream().collect(
                toMap(studentDegree -> studentDegree,
                        studentDegree -> {
                            if (!selectiveCourseIdsForStudent.get(studentDegree).isEmpty())
                                return getStudentOneYearSelectiveGradesBean(studentDegree.getId(), selectiveCourseIdsForStudent.get(studentDegree));
                            else
                                return new ArrayList<>();
                        }));
        return gradesForSelective;
    }

    public Map<StudentDegree, List<StudentGradeAbstractBean>> concatStudentOneYearGrades(Map<StudentDegree, List<StudentGradeAbstractBean>> grades1,
                                                                                         Map<StudentDegree, List<StudentGradeAbstractBean>> grades2){
        return Stream.of(grades1, grades2)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> new ArrayList<>(e.getValue()),
                        (left, right) -> {left.addAll(right); return left;}
                ));
    }

    public List<StudentGradeAbstractBean> getStudentOneYearGradesBean(Integer studentDegreeId, Map<Integer, Date> courseIdsWithDate) {
        return gradeRepository.getByStudentDegreeIdAndCourses(studentDegreeId, new ArrayList<>(courseIdsWithDate.keySet()))
                .stream()
                .map(grade -> new StudentGradeAbstractBean(grade,courseIdsWithDate.get(grade.getCourse().getId()),false))
                .collect(toList());
    }

    public List<StudentGradeAbstractBean> getStudentOneYearSelectiveGradesBean(Integer studentDegreeId, List<Integer> courseIds) {
        return gradeRepository.getByStudentDegreeIdAndCourses(studentDegreeId, courseIds)
                .stream()
                .map(grade -> new StudentGradeAbstractBean(grade,null,true))
                .collect(toList());
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

    private void formSemesterInTable(Tbl table, List<StudentGradeAbstractBean> gradeBeans, Integer year, SemesterType semesterType, StudentDegree studentDegree) {
        List<Tr> tableRows = (List<Tr>) (Object) getAllElementsFromObject(table, Tr.class);
        int currentIndex = 2, rowNumber = NUMBER_OF_MANDATORY_ROWS_IN_FIRST_SEMESTER_TABLE;
        if (semesterType == SemesterType.SECOND) {
            currentIndex = tableRows.size() - 2;
            rowNumber = NUMBER_OF_MANDATORY_ROWS_IN_TABLE;
        }
        Tr rowToCopy = tableRows.get(currentIndex);
        if (gradeBeans != null) {
            fillRowByGrade(tableRows.get(currentIndex - 1), gradeBeans.get(0), year);
            for (StudentGradeAbstractBean gradeBean : gradeBeans.subList(1, gradeBeans.size())) {
                Tr newRow = XmlUtils.deepCopy(rowToCopy);
                fillRowByGrade(newRow, gradeBean, year);
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

    private Map<String, String> getGradeDictionary(StudentGradeAbstractBean gradeBean, Integer year) {
        Map<String, String> result = new HashMap<>();
        result.put("sy", "НАВЧАЛЬНИЙ РІК");
        String gradeNumberYear = "";
        gradeNumberYear = getYearName(getStudentStudyYear(gradeBean.getGrade().getStudentDegree(), year)).toUpperCase() + " " + year + "-" + (year + 1);
        result.put("f", getSemesterName(getStudentStudyYear(gradeBean.getGrade().getStudentDegree(), year) * 2).toUpperCase());
        result.put("s", getSemesterName((getStudentStudyYear(gradeBean.getGrade().getStudentDegree(), year) * 2) + 1).toUpperCase());
        result.put("n", gradeNumberYear);
        result.put("subj", gradeBean.getGrade().getCourse().getCourseName().getName());
        result.put("t", resolveTypeField(gradeBean));
        result.put("h", gradeBean.getGrade().getCourse().getHours().toString());
        result.put("c", gradeBean.getGrade().getCourse().getCredits().toString());
        String gradeFieldValue = "";
        if (gradeBean.getGrade() != null && GradeUtil.isEnoughToPass(gradeBean.getGrade().getPoints())) {
            gradeFieldValue = gradeBean.getGrade().getCourse().getKnowledgeControl().isGraded() ? gradeBean.getGrade().getGrade().toString() : "зарах";//psgb.getGrade().toString()
        }
        result.put("g", gradeFieldValue);
        if (GradeUtil.isEnoughToPass(gradeBean.getGrade().getPoints())) {
            result.put("p", gradeBean.getGrade().getPoints().toString());
        }
        if (gradeBean.getGrade().getEcts() != null && GradeUtil.isEnoughToPass(gradeBean.getGrade().getPoints())) {
            result.put("e", gradeBean.getGrade().getEcts().toString());
        }

        if (gradeBean.getDate() != null && GradeUtil.isEnoughToPass(gradeBean.getGrade().getPoints())) {
            String date = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(gradeBean.getDate());
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

    private void fillRowByGrade(Tr row, StudentGradeAbstractBean gradeBean, Integer year) {
        replaceInRow(row, getGradeDictionary(gradeBean, year));
    }

    private void fillRowByLost(Tr row) {
        replaceInRow(row, getLostDictionary());
    }

    private String resolveTypeField(StudentGradeAbstractBean gradeBean) {
        switch (gradeBean.getGrade().getCourse().getKnowledgeControl().getId()) {
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
                return gradeBean.isSelective() ? "(з/в)" : "(з)";
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

    public synchronized File preparePersonalWrapperFront(List<Integer> studentDegreeIds) throws Docx4JException, IOException {
        if (studentDegreeIds.size() > 0) {
            List<StudentDegree> studentDegrees = new ArrayList<>();
            StringBuilder fileName = new StringBuilder();
            studentDegreeIds.forEach(studentDegreeId -> {
                studentDegrees.add(studentDegreeService.getById(studentDegreeId));
                fileName.append(studentDegreeId).append("_");
            });

            WordprocessingMLPackage filledTemplate = fillFrontPage(TEMPLATE_PATH_FRONT, studentDegrees);
            return documentIOService.saveDocumentToTemp(filledTemplate,
                    fileName + "Front", FileFormatEnum.DOCX);
        } else throw new IOException();
    }

    public synchronized File preparePersonalWrapperBack(List<Integer> studentDegreeIds) throws Docx4JException, IOException {
        if (studentDegreeIds.size() > 0) {
            List<StudentDegree> studentDegrees = new ArrayList<>();
            StringBuilder fileName = new StringBuilder();
            studentDegreeIds.forEach(studentDegreeId -> {
                studentDegrees.add(studentDegreeService.getById(studentDegreeId));
                fileName.append(studentDegreeId).append("_");
            });

            WordprocessingMLPackage filledTemplate = fillBackPage(TEMPLATE_PATH_BACK, studentDegrees);
            return documentIOService.saveDocumentToTemp(filledTemplate,
                    fileName + "Back", FileFormatEnum.DOCX);
        } else throw new IOException();
    }

    private WordprocessingMLPackage fillFrontPage(String templateName,
                                                  List<StudentDegree> studentDegrees) throws Docx4JException {
        WordprocessingMLPackage reportsDocument = fillFrontPage(templateName, studentDegrees.get(0));
        studentDegrees.remove(0);
        if (studentDegrees.size() > 0) {
            studentDegrees.forEach(studentDegree -> {
                TemplateUtil.addPageBreak(reportsDocument);
                try {
                    reportsDocument.getMainDocumentPart().getContent()
                            .addAll(fillFrontPage(templateName, studentDegree).getMainDocumentPart().getContent());
                } catch (Docx4JException e) {
                    e.printStackTrace();
                }
            });
        }
        return reportsDocument;
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
        List<String> graduates = StringUtil.makeHyphenationForRow(studentDegree.getPreviousDiplomaIssuedBy() != null ?
                studentDegree.getPreviousDiplomaIssuedBy() : "", 55);
        commonDict.put("Graduated", graduates.get(0));
        commonDict.put("Graduated2", graduates.get(1));
        commonDict.put("GradSer", studentDegree.getPreviousDiplomaNumber() != null ?
                studentDegree.getPreviousDiplomaNumber() : "");
        List<String> addresses = StringUtil.makeHyphenationForRow(
                studentDegree.getStudent().getRegistrationAddress() != null ?
                        studentDegree.getStudent().getRegistrationAddress() :
                        studentDegree.getStudent().getActualAddress() != null ?
                                studentDegree.getStudent().getActualAddress() : "", 55);
        commonDict.put("POfRes", addresses.get(0));
        commonDict.put("POfRes2", addresses.get(1));
        commonDict.put("PhoneNum", studentDegree.getStudent().getTelephone() != null ?
                studentDegree.getStudent().getTelephone() : "");
        commonDict.put("AdmPriv", studentDegree.getStudent().getPrivilege() != null ?
                studentDegree.getStudent().getPrivilege().getName() : "");
        commonDict.put("AdmDate", studentDegree.getAdmissionDate() != null ?
                DateUtil.getDate(studentDegree.getAdmissionDate()) : "");
        commonDict.put("AdmSer", studentDegree.getAdmissionOrderNumber() != null ?
                studentDegree.getAdmissionOrderNumber() : "");
        fillAcademicVacationTable(template, prepareAcademicVacationReports(studentDegree.getId()));
        replaceTextPlaceholdersInTemplate(template, commonDict);
        return template;
    }

    private WordprocessingMLPackage fillBackPage(String templateName,
                                                 StudentDegree studentDegree) throws Docx4JException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(templateName);
        Map<String, String> commonDict = new HashMap<>(prepareStudentsGrade(studentDegree.getId()));
        fillPracticeTable(template, preparePracticeReports(studentDegree.getId()));
        fillQualificationTable(template, prepareQualificationReport(studentDegree.getId()));
        List<String> thesis = StringUtil.makeHyphenationForRow(studentDegree.getThesisName() != null ?
                studentDegree.getThesisName() : "", 60);
        commonDict.put("ThesisName", thesis.get(0));
        commonDict.put("ThesisName2", thesis.get(1));
        commonDict.put("DeanName", studentDegree.getSpecialization().getFaculty().getDean() != null ?
                PersonUtil.makeNameThenSurnameInCapital(
                        studentDegree.getSpecialization().getFaculty().getDean()) : "");
        replaceTextPlaceholdersInTemplate(template, commonDict);
        return template;
    }

    private WordprocessingMLPackage fillBackPage(String templateName,
                                                 List<StudentDegree> studentDegrees) throws Docx4JException {
        WordprocessingMLPackage reportsDocument = fillBackPage(templateName, studentDegrees.get(0));
        studentDegrees.remove(0);
        if (studentDegrees.size() > 0) {
            studentDegrees.forEach(studentDegree -> {
                TemplateUtil.addPageBreak(reportsDocument);
                try {
                    reportsDocument.getMainDocumentPart().getContent()
                            .addAll(fillBackPage(templateName, studentDegree).getMainDocumentPart().getContent());
                } catch (Docx4JException e) {
                    e.printStackTrace();
                }
            });
        }
        return reportsDocument;
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
        Long perfect = 0L, good = 0L, satisfactory = 0L;
        for (Grade grade : grades) {
            if (grade.getPoints() == null)
                continue;
            if (grade.getPoints() >= EctsGrade.A.getLowerBound())
                perfect++;
            else if (grade.getPoints() <= EctsGrade.B.getUpperBound()
                    && grade.getPoints() >= EctsGrade.C.getLowerBound())
                good++;
            else if (grade.getPoints() <= EctsGrade.D.getUpperBound()
                    && grade.getPoints() >= EctsGrade.E.getLowerBound())
                satisfactory++;
        }
        Integer amount = grades.size();

        DecimalFormat df = new DecimalFormat("0.00");
        Map<String, String> result = new HashMap<>();
        result.put("Amount", String.valueOf(amount));
        result.put("P", String.valueOf(perfect));
        result.put("Pp", perfect != 0 ?
                String.valueOf(df.format(perfect.doubleValue() / amount.doubleValue() * 100)) : "0");
        result.put("G", String.valueOf(good));
        result.put("Gp", good != 0 ?
                String.valueOf(df.format(good.doubleValue() / amount.doubleValue() * 100)) : "0");
        result.put("S", String.valueOf(satisfactory));
        result.put("Sp", satisfactory != 0 ?
                String.valueOf(df.format(satisfactory.doubleValue() / amount.doubleValue() * 100)) : "0");
        return result;
    }

    private List<AcademicVacationReport> prepareAcademicVacationReports(Integer studentDegreeId) {
        List<AcademicVacationReport> academicVacationReports = new ArrayList<>();
        List<StudentAcademicVacation> studentAcademicVacations = studentAcademicVacationService.getByDegreeId(studentDegreeId);
        for (StudentAcademicVacation studentAcademicVacation : studentAcademicVacations)
            academicVacationReports.add(new AcademicVacationReport(
                    String.valueOf(studentAcademicVacation.getStudyYear()),
                    studentAcademicVacation.getOrderNumber(),
                    DateUtil.getDate(studentAcademicVacation.getOrderDate()),
                    studentAcademicVacation.getOrderReason().getName()));
        return academicVacationReports;
    }

    private void fillAcademicVacationTable(WordprocessingMLPackage template,
                                           List<AcademicVacationReport> academicVacationReports) {
        Tbl tempTable = findTable(template, "Курс");
        if (tempTable == null) return;
        Tr templateRow;
        int rowToAddIndex = 1;
        for (AcademicVacationReport academicVacationReport : academicVacationReports) {
            Map<String, String> replacements = academicVacationReport.getDictionary();
            templateRow = getTableRow(tempTable, rowToAddIndex);
            replaceInRow(templateRow, replacements);
            rowToAddIndex++;
        }
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
                    grade.getPoints() != null ?
                            String.valueOf(grade.getPoints()) : "",
                    grade.getGrade() != null ?
                            String.valueOf(grade.getGrade()) : "",
                    grade.getEcts() != null ?
                            grade.getEcts().name() : ""));
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

    private List<QualificationReport> prepareQualificationReport(Integer studentDegreeId) {
        List<QualificationReport> qualificationReports = new ArrayList<>();
        List<Integer> kCTypes = new ArrayList<>();
        kCTypes.add(Constants.ATTESTATION);
        kCTypes.add(Constants.STATE_EXAM);
        List<Grade> grades = gradeService.getGradesByStudetDegreeIdAndKCTypes(studentDegreeId, kCTypes);
        int number = 1;
        for (Grade grade : grades) {
            qualificationReports.add(new QualificationReport(
                    grade.getCourse().getCourseName().getName(),
                    number++,
                    grade.getPoints() != null ?
                            String.valueOf(grade.getPoints()) : "",
                    grade.getGrade() != null ?
                            String.valueOf(grade.getGrade()) : "",
                    grade.getEcts() != null ?
                            grade.getEcts().name() : ""));
        }
        return qualificationReports;
    }

    private void fillQualificationTable(WordprocessingMLPackage template, List<QualificationReport> qualificationReports) {
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
        private Map<StudentDegree, List<StudentGradeAbstractBean>> gradeMapForFirstSemester;
        private Map<StudentDegree, List<StudentGradeAbstractBean>> gradeMapForSecondSemester;
    }
}
