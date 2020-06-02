package ua.edu.chdtu.deanoffice.service.document.individualcurriculum;

import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.entity.Department;
import ua.edu.chdtu.deanoffice.entity.KnowledgeControl;
import ua.edu.chdtu.deanoffice.entity.Speciality;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.entity.Teacher;
import ua.edu.chdtu.deanoffice.entity.TuitionForm;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;
import ua.edu.chdtu.deanoffice.service.KnowledgeControlService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.TemplateUtil;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static ua.edu.chdtu.deanoffice.service.document.DocumentIOService.TEMPLATES_PATH;
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.replaceInRow;

@Service
public class IndividualCurriculumFillTemplateService {
    private static final Logger log = LoggerFactory.getLogger(IndividualCurriculumFillTemplateService.class);

    private final static String TEMPLATE_PATH = TEMPLATES_PATH + "IndividualCurriculum.docx";
    private static final int STARTING_ROW_INDEX_AUTUMN_TABLE = 5;
    private static final int STARTING_ROW_INDEX_SPRING_TABLE = 10;
    private static final int TABLE_INDEX = 4;

    private final DocumentIOService documentIOService;
    private final CourseForGroupService courseForGroupService;
    private final CurrentYearService currentYearService;

    public IndividualCurriculumFillTemplateService(DocumentIOService documentIOService,
                                                   CourseForGroupService courseForGroupService, CurrentYearService currentYearService) {
        this.documentIOService = documentIOService;
        this.courseForGroupService = courseForGroupService;
        this.currentYearService = currentYearService;
    }

    public WordprocessingMLPackage fillTemplate(Set<StudentDegree> degrees) {
        WordprocessingMLPackage formedDocument = null;

        Map<String, String> commonStudentDegreeInfo = getCommonStudentDegreeInfo(degrees.iterator().next());

        for (StudentDegree degree : degrees) {
            try {
                if (Objects.nonNull(formedDocument)) {
                    TemplateUtil.addPageBreak(formedDocument);

                    formedDocument.getMainDocumentPart().getContent().addAll(
                            fillTemplateForSingleDegree(commonStudentDegreeInfo, degree).getMainDocumentPart().getContent()
                    );
                } else {
                    formedDocument = fillTemplateForSingleDegree(commonStudentDegreeInfo, degree);
                }
            } catch (Docx4JException e) {
                log.error(e.getMessage());
            }
        }

        return formedDocument;
    }

    private WordprocessingMLPackage fillTemplateForSingleDegree(Map<String, String> commonStudyInfo,
                                                                StudentDegree degree) throws Docx4JException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(TEMPLATE_PATH);
        Map<String, String> replacements = new HashMap<>();

        replacements.putAll(getInfoForSingleStudent(degree));
        replacements.putAll(commonStudyInfo);

        fillTableWithCoursesInfo(template, degree);
        removeUnfilledPlaceholders(template);
        TemplateUtil.replaceTextPlaceholdersInTemplate(template, replacements);

        return template;
    }

    private Map<String, String> getInfoForSingleStudent(StudentDegree degree) {
        StudentGroup studentGroup = degree.getStudentGroup();

        Map<String, String> replacements = new HashMap<>();

        String studentName = degree.getStudent().getFullNameUkr();
        replacements.put("StudentName", studentName);

        replacements.put("RecNum", degree.getSupplementNumber());

        String tuitionForm = Objects.requireNonNull(
                TuitionForm.getTuitionFormFromUkrName(degree.getTuitionForm().getNameUkr())
        ).getNameUkr();
        replacements.put("TF", tuitionForm);

        String admissionDate = getYearFromDate(degree.getAdmissionDate());
        replacements.put("Begin", TemplateUtil.getValueSafely(admissionDate));
        String endDate = String.valueOf(Integer.parseInt(admissionDate) + studentGroup.getStudyYears().intValue() + 1);
        replacements.put("End", TemplateUtil.getValueSafely(endDate));

        int dbCurrentYear = currentYearService.get().getCurrYear();
        String course = String.valueOf(dbCurrentYear - studentGroup.getCreationYear() + studentGroup.getBeginYears());
        replacements.put("Course", TemplateUtil.getValueSafely(course));

        return replacements;
    }

    private String getYearFromDate(Date date) {
        return Objects.nonNull(date) ? date.toString().split("-", 2)[0] : "";
    }

    private Map<String, String> getCommonStudentDegreeInfo(StudentDegree degree) {
        Map<String, String> replacements = new HashMap<>();

        replacements.put("StudyYear", TemplateUtil.getValueSafely(getStudyYear()));

        String faculty = degree.getSpecialization().getFaculty().getName();
        replacements.put("Faculty", TemplateUtil.getValueSafely(faculty.toUpperCase()));

        Speciality speciality = degree.getSpecialization().getSpeciality();
        String specialityName = speciality.getCode() + " " + speciality.getName();
        replacements.put("Speciality", TemplateUtil.getValueSafely(specialityName));
        replacements.put("FieldOfStudy", TemplateUtil.getValueSafely(speciality.getFieldOfStudy()));

        String educationProgram = degree.getSpecialization().getName();
        replacements.put("EducationProgram", TemplateUtil.getValueSafely(educationProgram));

        String degreeName = degree.getSpecialization().getDegree().getName();
        replacements.put("Degree", TemplateUtil.getValueSafely(degreeName));

        replacements.put("AG", TemplateUtil.getValueSafely(degree.getStudentGroup().getName()));

        return replacements;
    }

    private String getStudyYear() {
        int currentYear = currentYearService.get().getCurrYear();
        return String.format("%4d-%4d", currentYear, currentYear + 1);
    }

    private void fillTableWithCoursesInfo(WordprocessingMLPackage template, StudentDegree degree) {
        StudentGroup studentGroup = degree.getStudentGroup();
        List<Integer> semesters = getSemestersByCourseForGroup(studentGroup);

        List<CourseForGroup> autumnSemester =
                courseForGroupService.getCoursesForGroupBySemester(studentGroup.getId(), semesters.get(0));
        List<CourseForGroup> springSemester =
                courseForGroupService.getCoursesForGroupBySemester(studentGroup.getId(), semesters.get(1));


        List<CourseForGroup> collect = autumnSemester.stream()
                .filter(courses ->
                        courses.getCourse().getKnowledgeControl().getName().toLowerCase().contains("практика")
                ).collect(Collectors.toList());

        fillCourseTable(template, autumnSemester, STARTING_ROW_INDEX_AUTUMN_TABLE);
        fillCourseTable(template, springSemester, STARTING_ROW_INDEX_SPRING_TABLE + autumnSemester.size());

        removeUnfilledPlaceholders(template);
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
            replacements.put("KC", String.valueOf(course.getKnowledgeControl())
                    .replaceAll("\\B.|\\P{L}", "").toUpperCase()
            );

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

    private void removeUnfilledPlaceholders(WordprocessingMLPackage template) {
        Set<String> placeholdersToRemove = new HashSet<>();
        placeholdersToRemove.add("#N");
        placeholdersToRemove.add("#H");
        placeholdersToRemove.add("#Cred");
        placeholdersToRemove.add("#KC");
        placeholdersToRemove.add("#Dep");

        TemplateUtil.replacePlaceholdersWithBlank(template, placeholdersToRemove);
    }

    private List<Integer> getSemestersByCourseForGroup(StudentGroup studentGroup) {
        int dbCurrentYear = currentYearService.get().getCurrYear();
        int course = dbCurrentYear - studentGroup.getCreationYear() + studentGroup.getBeginYears();

        return Arrays.asList(course * 2 - 1, course * 2);
    }
}
