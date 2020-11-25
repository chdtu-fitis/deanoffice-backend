package ua.edu.chdtu.deanoffice.service.document.individualcurriculum;

import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;
import ua.edu.chdtu.deanoffice.service.course.selective.SelectiveCoursesStudentDegreesService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.TemplateUtil;
import ua.edu.chdtu.deanoffice.util.PersonUtil;

import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static ua.edu.chdtu.deanoffice.service.document.DocumentIOService.TEMPLATES_PATH;
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.replaceInRow;

@Service
public class IndividualCurriculumFillTemplateService {
    private static final Logger log = LoggerFactory.getLogger(IndividualCurriculumFillTemplateService.class);

    private final static String TEMPLATE_PATH = TEMPLATES_PATH + "IndividualCurriculum.docx";
    private static final int STARTING_ROW_INDEX_AUTUMN_TABLE = 5;
    private static final int STARTING_ROW_INDEX_SPRING_TABLE = 9;
    private static final int STARTING_ROW_INDEX_PRACTICAL_TABLE = 15;
    private static final int STARTING_ROW_INDEX_CONCLUSION_TABLE = 16;
    private static final int TABLE_INDEX = 4;
    private static final String SPRING_COURSES_KEY = "Spring";
    private static final String AUTUMN_COURSES_KEY = "Autumn";
    private static final String PRACTICAL_COURSES_KEY = "Practical";

    private final DocumentIOService documentIOService;
    private final CourseForGroupService courseForGroupService;
    private final SelectiveCoursesStudentDegreesService selectiveCoursesStudentDegreesService;

    public IndividualCurriculumFillTemplateService(DocumentIOService documentIOService,
                                                   CourseForGroupService courseForGroupService,
                                                   SelectiveCoursesStudentDegreesService selectiveCoursesStudentDegreesService) {
        this.documentIOService = documentIOService;
        this.courseForGroupService = courseForGroupService;
        this.selectiveCoursesStudentDegreesService = selectiveCoursesStudentDegreesService;
    }

    public WordprocessingMLPackage fillTemplate(Set<StudentDegree> degrees, int studyYears) {
        WordprocessingMLPackage formedDocument = null;

        Map<String, String> commonStudentDegreeInfo = getCommonStudentDegreeInfo(degrees.iterator().next(), studyYears);

        for (StudentDegree degree : degrees) {
            try {
                if (Objects.nonNull(formedDocument)) {
                    TemplateUtil.addPageBreak(formedDocument);

                    formedDocument.getMainDocumentPart().getContent().addAll(
                            fillTemplateForSingleDegree(commonStudentDegreeInfo, degree, studyYears).getMainDocumentPart().getContent()
                    );
                } else {
                    formedDocument = fillTemplateForSingleDegree(commonStudentDegreeInfo, degree, studyYears);
                }
            } catch (Docx4JException e) {
                log.error(e.getMessage());
            }
        }
        return formedDocument;
    }

    private WordprocessingMLPackage fillTemplateForSingleDegree(Map<String, String> commonStudyInfo,
                                                                StudentDegree degree,
                                                                int studyYear) throws Docx4JException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(TEMPLATE_PATH);
        Map<String, String> replacements = new HashMap<>();

        replacements.putAll(getInfoForSingleStudent(degree, studyYear));
        replacements.putAll(commonStudyInfo);

        fillTableWithCoursesInfo(template, degree, studyYear);
        removeUnfilledPlaceholders(template);
        TemplateUtil.replaceTextPlaceholdersInTemplate(template, replacements);

        return template;
    }

    private Map<String, String> getInfoForSingleStudent(StudentDegree degree, int studyYear) {
        StudentGroup studentGroup = degree.getStudentGroup();
        Map<String, String> replacements = new HashMap<>();

        Speciality speciality = degree.getSpecialization().getSpeciality();
        String specialityName = speciality.getCode() + " " + speciality.getName();
        replacements.put("Speciality", TemplateUtil.getValueSafely(specialityName));
        replacements.put("FieldOfStudy", TemplateUtil.getValueSafely(speciality.getFieldOfStudy()));
        String educationProgram = degree.getSpecialization().getName();
        replacements.put("EducationProgram", TemplateUtil.getValueSafely(educationProgram));
        replacements.put("AG", TemplateUtil.getValueSafely(degree.getStudentGroup().getName()));

        String studentName = degree.getStudent().getFullNameUkr();
        String studentInitials = degree.getStudent().getInitialsUkr();
        replacements.put("StudentName", studentName);
        replacements.put("StudentAbr", studentInitials);

        String dean = degree.getStudentGroup().getSpecialization().getFaculty().getDean();
        replacements.put("DeanAbr", PersonUtil.makeInitialsSurnameLast(dean));

        replacements.put("RecNum", degree.getSupplementNumber());

        String tuitionForm = Objects.requireNonNull(
                TuitionForm.getTuitionFormFromUkrName(degree.getTuitionForm().getNameUkr())
        ).getNameUkr();
        replacements.put("TF", tuitionForm);

        String admissionDate = getYearFromDate(degree.getAdmissionDate());
        replacements.put("Begin", TemplateUtil.getValueSafely(admissionDate));
        String endDate = "";
        try {
            int endYear = Integer.parseInt(admissionDate);
            endDate = String.valueOf(endYear + studentGroup.getStudyYears().setScale(0, RoundingMode.HALF_UP).intValue());
        } catch (NumberFormatException e) {
        }
        finally {
            replacements.put("End", TemplateUtil.getValueSafely(endDate));
        }
        String groupStudyYear = "" + (studyYear - studentGroup.getCreationYear() + studentGroup.getBeginYears());
        replacements.put("Course", TemplateUtil.getValueSafely(groupStudyYear));

        return replacements;
    }

    private String getYearFromDate(Date date) {
        return Objects.nonNull(date) ? date.toString().split("-", 2)[0] : "";
    }

    private Map<String, String> getCommonStudentDegreeInfo(StudentDegree degree, int studyYear) {
        Map<String, String> replacements = new HashMap<>();
        replacements.put("StudyYear", ""+studyYear+"-"+(studyYear+1));
        String faculty = degree.getSpecialization().getFaculty().getName();
        replacements.put("Faculty", TemplateUtil.getValueSafely(faculty.toUpperCase()));
        String degreeName = degree.getSpecialization().getDegree().getName();
        replacements.put("Degree", TemplateUtil.getValueSafely(degreeName));
        return replacements;
    }

    private void fillTableWithCoursesInfo(WordprocessingMLPackage template, StudentDegree degree, int studyYear) throws Docx4JException {
        StudentGroup studentGroup = degree.getStudentGroup();
        List<Integer> semesters = getSemestersByCourseForGroup(studentGroup, studyYear);

        Map<String, List<CourseForGroup>> autumnSemesterPart1 = getCoursesBySemester(studentGroup, semesters.get(0));
        List<SelectiveCourse> autumnSelectiveCourses = getSelectiveCourses(degree.getId(), semesters.get(0));

        Map<String, List<CourseForGroup>> springSemesterPart1 = getCoursesBySemester(studentGroup, semesters.get(1));
        List<SelectiveCourse> springSelectiveCourses = getSelectiveCourses(degree.getId(), semesters.get(1));

        Tbl tempTable = TemplateUtil.getAllTablesFromDocument(template).get(TABLE_INDEX);
        if (!Objects.nonNull(tempTable)) {
            throw new Docx4JException("Проблеми з шаблоном на сервері.");
        }

        int numberOfRow = STARTING_ROW_INDEX_AUTUMN_TABLE;

        List<CourseForGroup> autumnMainCourses = autumnSemesterPart1.get(AUTUMN_COURSES_KEY);
        addMainCoursesToTable(tempTable, autumnMainCourses, numberOfRow);

        numberOfRow = numberOfRow + autumnMainCourses.size() + 1;

        addSelectiveCoursesToTable(tempTable, autumnSelectiveCourses, numberOfRow);

        numberOfRow += autumnSelectiveCourses.size() + 2;

        List<CourseForGroup> springMainCourses = springSemesterPart1.get(SPRING_COURSES_KEY);
        addMainCoursesToTable(tempTable, springMainCourses, numberOfRow);

        numberOfRow += springMainCourses.size() + 1;

        addSelectiveCoursesToTable(tempTable, springSelectiveCourses, numberOfRow);

        numberOfRow += springSelectiveCourses.size() + 1;

        List<CourseForGroup> practical = autumnSemesterPart1.get(PRACTICAL_COURSES_KEY);
        practical.addAll(springSemesterPart1.get(PRACTICAL_COURSES_KEY));

        addMainCoursesToTable(tempTable, practical, numberOfRow);

        numberOfRow += practical.size() + 1;

        fillConclusionTable(autumnMainCourses, autumnSelectiveCourses, springMainCourses, springSelectiveCourses,
                practical, template, numberOfRow);

        removeUnfilledPlaceholders(template);
    }

    private void fillConclusionTable(List<CourseForGroup> autumnMainCourses,
                                     List<SelectiveCourse> autumnSelectiveCourses,
                                     List<CourseForGroup> springMainCourses,
                                     List<SelectiveCourse> springSelectiveCourses,
                                     List<CourseForGroup> practicals,
                                     WordprocessingMLPackage template,
                                     int numberOfRow) {
        Tbl tempTable = TemplateUtil.getAllTablesFromDocument(template).get(TABLE_INDEX);

        if (!Objects.nonNull(tempTable)) {
            return;
        }

        List<Object> tableRows = TemplateUtil.getAllElementsFromObject(tempTable, Tr.class);

        Tr blankRow = (Tr) tableRows.get(numberOfRow);
//        Tr currentRow = XmlUtils.deepCopy(blankRow);
        int hourSumAutumnMainCourses = autumnMainCourses.stream().mapToInt(courseForGroup -> courseForGroup.getCourse().getHours()).sum();
        int hourSumAutumnSelectiveCourses = autumnSelectiveCourses.stream().mapToInt(selectiveCourse -> selectiveCourse.getCourse().getHours()).sum();
        int hourSumSpringMainCourses = springMainCourses.stream().mapToInt(courseForGroup -> courseForGroup.getCourse().getHours()).sum();
        int hourSumSpringSelectiveCourses = springSelectiveCourses.stream().mapToInt(selectiveCourse -> selectiveCourse.getCourse().getHours()).sum();
        int hourSumPracticals = practicals.stream().mapToInt(courseForGroup -> courseForGroup.getCourse().getHours()).sum();

        int hourPerCreditSumAutumnMainCourses = autumnMainCourses
                .stream().mapToInt(courseForGroup -> courseForGroup.getCourse().getHoursPerCredit()).sum();
        int hourPerCreditSumAutumnSelectiveCourses = autumnSelectiveCourses
                .stream().mapToInt(selectiveCourse -> selectiveCourse.getCourse().getHoursPerCredit()).sum();
        int hourPerCreditSumSpringMainCourses = springMainCourses
                .stream().mapToInt(courseForGroup -> courseForGroup.getCourse().getHoursPerCredit()).sum();
        int hourPerCreditSumSpringSelectiveCourses = springSelectiveCourses
                .stream().mapToInt(selectiveCourse -> selectiveCourse.getCourse().getHoursPerCredit()).sum();
        int hourPerCreditSumPracticals = practicals.stream()
                .mapToInt(courseForGroup -> courseForGroup.getCourse().getHoursPerCredit()).sum();

        Map<String, String> replacements = new HashMap<>();
        replacements.put("TH", String.valueOf(hourSumAutumnMainCourses + hourSumAutumnSelectiveCourses +
                hourSumSpringMainCourses + hourSumSpringSelectiveCourses + hourSumPracticals));
        replacements.put("TCr", String.valueOf(hourPerCreditSumAutumnMainCourses + hourPerCreditSumAutumnSelectiveCourses +
                hourPerCreditSumSpringMainCourses + hourPerCreditSumSpringSelectiveCourses + hourPerCreditSumPracticals));

        replaceInRow(blankRow, replacements);
        tempTable.getContent().add(numberOfRow, blankRow);
        tempTable.getContent().remove(numberOfRow);
    }

    private Map<String, List<CourseForGroup>> getCoursesBySemester(StudentGroup studentGroup, int semester) {
        Map<String, List<CourseForGroup>> container = new HashMap<>();

        List<CourseForGroup> courseForGroups =
                courseForGroupService.getCoursesForGroupBySemester(studentGroup.getId(), semester);

        String key = getKeyBySemester(semester);

        List<CourseForGroup> lessPractical = courseForGroups.stream().filter(cfg ->
                !cfg.getCourse().getKnowledgeControl().getName().toLowerCase().contains("практика")
        ).collect(Collectors.toList());
        List<CourseForGroup> practical = courseForGroups.stream().filter(cfg ->
                cfg.getCourse().getKnowledgeControl().getName().toLowerCase().contains("практика")
        ).collect(Collectors.toList());

        container.put(key, lessPractical);
        container.put(PRACTICAL_COURSES_KEY, practical);

        return container;
    }

    private List<SelectiveCourse> getSelectiveCourses(int studentDegreeId, int semester) {
        List<SelectiveCoursesStudentDegrees> selectiveCoursesStudentDegrees =
                selectiveCoursesStudentDegreesService.getSelectiveCoursesByStudentDegreeIdAndSemester(studentDegreeId, semester);
        return selectiveCoursesStudentDegrees.stream().map(SelectiveCoursesStudentDegrees::getSelectiveCourse).collect(Collectors.toList());
    }

    private String getKeyBySemester(int semester) {
        return semester % 2 == 0 ? SPRING_COURSES_KEY : AUTUMN_COURSES_KEY;
    }

    private void fillCourseTable(WordprocessingMLPackage template, List<CourseForGroup> courseForGroups,
                                 int startingRowIndex) {
        Tbl tempTable = TemplateUtil.getAllTablesFromDocument(template).get(TABLE_INDEX);

        if (!Objects.nonNull(tempTable)) {
            return;
        }

        List<Object> tableRows = TemplateUtil.getAllElementsFromObject(tempTable, Tr.class);
        int currentRowIndex = startingRowIndex;
        int numberOfRow = 1;
        Tr blankRow = (Tr) tableRows.get(currentRowIndex);

        for (CourseForGroup courseForGroup : courseForGroups) {
            Tr currentRow = XmlUtils.deepCopy(blankRow);
            Course course = courseForGroup.getCourse();
            Map<String, String> replacements = new HashMap<>();
            replacements.put("N", String.valueOf(numberOfRow));
            replacements.put("CourseName", course.getCourseName().getName());
            replacements.put("H", String.valueOf(course.getHours()));
            replacements.put("Cred", String.valueOf(course.getHoursPerCredit()));
            replacements.put("KC", String.valueOf(getKnowledgeControl(course)));

            Department department =
                    Objects.nonNull(courseForGroup.getTeacher()) ? courseForGroup.getTeacher().getDepartment() : null;
            replacements.put("Dep", Objects.nonNull(department) ? TemplateUtil.getValueSafely(department.getAbbr()) : "");

            replaceInRow(currentRow, replacements);
            tempTable.getContent().add(currentRowIndex, currentRow);

            currentRowIndex++;
            numberOfRow++;
        }
        tempTable.getContent().remove(currentRowIndex);
    }

    private String getKnowledgeControl(Course course) {
        String knowledgeControl = course.getKnowledgeControl().getName();

        return knowledgeControl.length() > 5 ?
                knowledgeControl.replaceAll("\\B.|\\P{L}", "").toUpperCase() : knowledgeControl;
    }

    private void removeUnfilledPlaceholders(WordprocessingMLPackage template) {
        Set<String> placeholdersToRemove = new HashSet<>();
        placeholdersToRemove.add("#N");
        placeholdersToRemove.add("#H");
        placeholdersToRemove.add("#Cred");
        placeholdersToRemove.add("#KC");
        placeholdersToRemove.add("#Dep");
        placeholdersToRemove.add("#TH");
        placeholdersToRemove.add("#TCr");
        TemplateUtil.replacePlaceholdersWithBlank(template, placeholdersToRemove);
    }

    private List<Integer> getSemestersByCourseForGroup(StudentGroup studentGroup, int studyYear) {
        int course = studyYear - studentGroup.getCreationYear() + studentGroup.getBeginYears();
        return Arrays.asList(course * 2 - 1, course * 2);
    }
}
