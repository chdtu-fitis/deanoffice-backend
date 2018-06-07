package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.Constants;
import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;
import ua.edu.chdtu.deanoffice.repository.CourseRepository;
import ua.edu.chdtu.deanoffice.repository.GradeRepository;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;


@Service
public class GradeService {
    private static final Integer[] KNOWLEDGE_CONTROL_PART1 = {Constants.EXAM, Constants.CREDIT, Constants.DIFFERENTIATED_CREDIT};
    private static final Integer[] KNOWLEDGE_CONTROL_PART2 = {Constants.COURSEWORK, Constants.COURSE_PROJECT};
    private static final Integer[] KNOWLEDGE_CONTROL_PART3 = {Constants.INTERNSHIP, Constants.NON_GRADED_INTERNSHIP};
    private static final Integer[] KNOWLEDGE_CONTROL_PART4 = {Constants.ATTESTATION};

    private final GradeRepository gradeRepository;
    private final CourseRepository courseRepository;
    private final StudentDegreeRepository studentDegreeRepository;
    private CourseService courseService;

    public GradeService(GradeRepository gradeRepository, CourseRepository courseRepository,
                        StudentDegreeRepository studentDegreeRepository, CourseService courseService) {
        this.gradeRepository = gradeRepository;
        this.courseRepository = courseRepository;
        this.studentDegreeRepository = studentDegreeRepository;
        this.courseService = courseService;
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
        Student student = studentDegree.getStudent();
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

    public Map<StudentDegree, List<Grade>>getGradeMapForStudents(List<Integer> studentDegreeIds, List<Integer> courseIds){
        return gradeRepository.findGradesByCourseAndBySemesterForStudents(studentDegreeIds,courseIds).stream().collect(
                Collectors.groupingBy(Grade::getStudentDegree,toList()));
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

    public void saveGradesByCourse(Course course, List<Grade> grades) {
        for (Grade grade : grades) {
            grade.setCourse(course);
            gradeRepository.save(grade);
        }
    }

    public void deleteGradeById(Integer gradeId) {
        gradeRepository.delete(gradeId);
    }

}