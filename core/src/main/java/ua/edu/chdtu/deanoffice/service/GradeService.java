package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.chdtu.deanoffice.Constants;
import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;
import ua.edu.chdtu.deanoffice.repository.CourseRepository;
import ua.edu.chdtu.deanoffice.repository.GradeRepository;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;
import ua.edu.chdtu.deanoffice.service.course.CourseService;
import ua.edu.chdtu.deanoffice.util.GradeUtil;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Service
public class GradeService {
    private static final Integer[] KNOWLEDGE_CONTROL_PART1 = {Constants.EXAM, Constants.CREDIT, Constants.DIFFERENTIATED_CREDIT};
    private static final Integer[] KNOWLEDGE_CONTROL_PART2 = {Constants.COURSEWORK, Constants.COURSE_PROJECT};
    private static final Integer[] KNOWLEDGE_CONTROL_PART3 = {Constants.INTERNSHIP, Constants.NON_GRADED_INTERNSHIP};
    private static final Integer[] KNOWLEDGE_CONTROL_PART4 = {Constants.ATTESTATION, Constants.STATE_EXAM};
    public static final String NEW_GRADED_VALUE = "new graded";

    private final GradeRepository gradeRepository;
    private final CourseRepository courseRepository;
    private final StudentDegreeRepository studentDegreeRepository;
    private CourseService courseService;
    private KnowledgeControlService knowledgeControlService;

    @Autowired
    public GradeService(GradeRepository gradeRepository, CourseRepository courseRepository,
                        StudentDegreeRepository studentDegreeRepository, CourseService courseService, KnowledgeControlService knowledgeControlService) {
        this.gradeRepository = gradeRepository;
        this.courseRepository = courseRepository;
        this.studentDegreeRepository = studentDegreeRepository;
        this.courseService = courseService;
        this.knowledgeControlService = knowledgeControlService;
    }

    public List<List<Grade>> getGradesByStudentDegreeId(Integer studentDegreeId) {
        StudentDegree studentDegree = studentDegreeRepository.getById(studentDegreeId);
        List<Course> courses = courseRepository.getByGroupId(studentDegree.getStudentGroup().getId());
        List<List<Grade>> grades = new ArrayList<>();

        if (courses.isEmpty()) {
            grades.add(new ArrayList<>());
            grades.add(new ArrayList<>());
            grades.add(new ArrayList<>());
            grades.add(new ArrayList<>());
            return grades;
        }

        List<Integer> courseIds = courses.stream().map(BaseEntity::getId).collect(Collectors.toList());

        grades.add(getGrades(studentDegree, courseIds, Arrays.asList(KNOWLEDGE_CONTROL_PART1)));
        grades.add(getGrades(studentDegree, courseIds, Arrays.asList(KNOWLEDGE_CONTROL_PART2)));
        grades.add(getGrades(studentDegree, courseIds, Arrays.asList(KNOWLEDGE_CONTROL_PART3)));
        grades.add(getGrades(studentDegree, courseIds, Arrays.asList(KNOWLEDGE_CONTROL_PART4)));

        return grades;
    }

    private List<Grade> getGrades(StudentDegree studentDegree,
                                  List<Integer> courseIds,
                                  List<Integer> knowledgeControlTypes) {
        return gradeRepository.getByStudentDegreeIdAndCoursesAndKCTypes(studentDegree.getId(),
                courseIds,
                knowledgeControlTypes);
    }

    @Transactional
    public void setAcademicDifferenceByGradeIds(Map<Boolean, List<Integer>> academicDifferenceAndGradeIds) {
        List<Integer> gradesIdsWithAcademicDifference = academicDifferenceAndGradeIds.get(true);
        if (gradesIdsWithAcademicDifference != null && !gradesIdsWithAcademicDifference.isEmpty()) {
            gradeRepository.updateAcademicDifference(true, gradesIdsWithAcademicDifference);
        }
        List<Integer> gradesIdsWithoutAcademicDifference = academicDifferenceAndGradeIds.get(false);
        if (gradesIdsWithoutAcademicDifference != null && !gradesIdsWithoutAcademicDifference.isEmpty()) {
            gradeRepository.updateAcademicDifference(false, gradesIdsWithoutAcademicDifference);
        }
    }

    public List<Grade> setGradeAndEcts(List<Grade> grades) {
        grades.forEach(grade -> {
            grade.setEcts(EctsGrade.getEctsGrade(grade.getPoints()));
            Course course = courseService.getById(grade.getCourse().getId());
            grade.setGrade(EctsGrade.getGrade(grade.getPoints(), course.getKnowledgeControl().isGraded()));
        });
        return grades;
    }

    public List<Grade> getAllDifferentiatedGrades(Integer studentDegreeId) {
        StudentDegree studentDegree = studentDegreeRepository.getById(studentDegreeId);
        List<Integer> knowledgeControlTypes = Arrays.asList(Constants.EXAM, Constants.DIFFERENTIATED_CREDIT, Constants.COURSEWORK, Constants.COURSE_PROJECT,
                Constants.ATTESTATION, Constants.INTERNSHIP, Constants.STATE_EXAM);
        List<Integer> courseIds = courseRepository.getByGroupId(studentDegree.getStudentGroup().getId())
                .stream().map(BaseEntity::getId).collect(Collectors.toList());
        return new ArrayList<>(getGrades(studentDegree, courseIds, knowledgeControlTypes));
    }


    public List<Grade> getGradesForStudents(List<Integer> studentsIds, List<Integer> courseIds) {
        if (studentsIds.isEmpty() || courseIds.isEmpty()) return new ArrayList<>();
        return gradeRepository.findGradesByCourseAndBySemesterForStudents(studentsIds, courseIds);
    }

    public Map<StudentDegree, List<Grade>> getGradeMapForStudents(Map<StudentGroup, List<Integer>> groupsWithStudents, Map<StudentGroup, List<Integer>> courseIdsForGroup) {
        Map<StudentDegree, List<Grade>> result = new HashMap<StudentDegree, List<Grade>>();
        for (StudentGroup group : groupsWithStudents.keySet()) {
            List<Integer> studentDegreeIds = groupsWithStudents.get(group);
            List<Integer> courseIds = courseIdsForGroup.get(group);
            Map<StudentDegree, List<Grade>> oneGroupGrades = null;
            if (!studentDegreeIds.isEmpty() && !courseIds.isEmpty())
                oneGroupGrades = gradeRepository.findGradesByCourseAndBySemesterForStudents(studentDegreeIds, courseIds).stream()
                        .sorted((g1, g2) -> new Integer(g1.getCourse().getKnowledgeControl().getId()).compareTo(g2.getCourse().getKnowledgeControl().getId()))
                        .collect(Collectors.groupingBy(Grade::getStudentDegree, toList()));
            else
                oneGroupGrades = new HashMap<>();
            result = Stream.concat(result.entrySet().stream(), oneGroupGrades.entrySet().stream()).collect(Collectors.toMap(
                    entry -> entry.getKey(),
                    entry -> entry.getValue()));
        }
        return result;
    }

    public List<Grade> insertGrades(List<Grade> grades) {
        return gradeRepository.save(grades);
    }

    public Grade getGradeForStudentAndCourse(Integer studentDegreeId, Integer courseId) {
        return gradeRepository.getByStudentDegreeIdAndCourseId(studentDegreeId, courseId);
    }

    public List<Grade> getGradesByCourseAndGroup(int courseId, int groupId) {
        return gradeRepository.findByCourseAndGroup(courseId, groupId);
    }

    public void saveGradesByCourse(Course course, List<Grade> grades, Map<String, Boolean> gradedDefinition) {
        for (Grade grade : grades) {
            grade.setCourse(course);
            Boolean newGradedValue = gradedDefinition.get(NEW_GRADED_VALUE);
            if (newGradedValue != null) {
                if (newGradedValue) {
                    grade.setGrade(GradeUtil.getGradeFromPoints(grade.getPoints()));
                } else {
                    grade.setGrade(GradeUtil.getCreditFromPoints(grade.getPoints()));
                }
            }
            gradeRepository.save(grade);
        }
    }

    public void deleteGradeById(Integer gradeId) {
        gradeRepository.delete(gradeId);
    }

    @Transactional
    public void updateNationalGradeByCourseIdAndGradedFalse(int courseId) {
        List<Integer> studentDegreeIds = gradeRepository.getStudentDegreeIdByCourseId(courseId);
        for (Integer studentDegreeId : studentDegreeIds) {
            gradeRepository.updateGradeByCourseIdAndGradedFalse(courseId, studentDegreeId);
        }
    }

    @Transactional
    public void updateNationalGradeByCourseIdAndGradedTrue(int courseId) {
        List<Integer> studentDegreeIds = gradeRepository.getStudentDegreeIdByCourseId(courseId);
        for (Integer studentDegreeId : studentDegreeIds) {
            gradeRepository.updateGradeByCourseIdAndGradedTrue(courseId, studentDegreeId);
        }
    }

    public Map<String, Boolean> evaluateGradedChange(int oldKnowledgeControlId, int newKnowledgeControlId) {
        Map<String, Boolean> gradeDefinition = new HashMap<>();
        if (oldKnowledgeControlId != newKnowledgeControlId) {
            boolean oldGraded = knowledgeControlService.getGradedByKnowledgeControlId(oldKnowledgeControlId);
            boolean newGraded = knowledgeControlService.getGradedByKnowledgeControlId(newKnowledgeControlId);
            if (oldGraded != newGraded) {
                gradeDefinition.put(NEW_GRADED_VALUE, newGraded);
            }
        }
        return gradeDefinition;
    }

    public List<StudentDegree> filterStudentByGrade(List<StudentDegree> studentDegrees, CourseForGroup courseForGroup, boolean isGoodMark) {
        List<StudentDegree> studentDegreeResult = new ArrayList<>();
        studentDegrees.forEach(studentDegree -> {
            boolean isStudentHasGoodMark = gradeRepository.isStudentHaveGoodMarkFromCourse(
                    studentDegree.getId(),
                    studentDegree.getStudentGroup().getId(),
                    courseForGroup.getCourse().getId()
            );
            if (isStudentHasGoodMark == isGoodMark)
                studentDegreeResult.add(studentDegree);
        });
        return studentDegreeResult;
    }

    public List<Grade> getGradesByStudetDegreeIdAndKCTypes(Integer studentDegreeId, List<Integer> knowledgeControlsIds) {
        return gradeRepository.getByStudentDegreeIdAndKCTypes(studentDegreeId, knowledgeControlsIds);
    }
}
