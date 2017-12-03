package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.Grade;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.repository.CourseRepository;
import ua.edu.chdtu.deanoffice.repository.GradeRepository;
import ua.edu.chdtu.deanoffice.repository.StudentRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class GradeService {

    private static final int EXAM = 1;
    private static final int CREDIT = 2;
    private static final int COURSEWORK = 3;
    private static final int COURSE_PROJECT = 4;
    private static final int DIFFERENTIATED_CREDIT = 5;
    private static final int STATE_EXAM = 6;
    private static final int ATTESTATION = 7;
    private static final int INTERNSHIP = 8;

    private Integer KNOWLEDGE_CONTROL_PART1[] = {EXAM, CREDIT, DIFFERENTIATED_CREDIT};
    private Integer KNOWLEDGE_CONTROL_PART2[] = {COURSEWORK, COURSE_PROJECT};
    private Integer KNOWLEDGE_CONTROL_PART3[] = {INTERNSHIP};
    private Integer KNOWLEDGE_CONTROL_PART4[] = {ATTESTATION};

    private GradeRepository gradeRepository;
    private CourseRepository courseRepository;
    private StudentRepository studentRepository;

    public GradeService(GradeRepository gradeRepository, CourseRepository courseRepository, StudentRepository studentRepository) {
        this.gradeRepository = gradeRepository;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
    }

    public List<List<Grade>> getGradesByStudentId(Integer studentId) {
        Student student = studentRepository.getOne(studentId);
        List<Course> courses = courseRepository.getByGroupId(student.getStudentGroup().getId());
        List<List<Grade>> grades = new ArrayList<>();

        if (courses.isEmpty()) {
            grades.add(new ArrayList<>());
            grades.add(new ArrayList<>());
            grades.add(new ArrayList<>());
            grades.add(new ArrayList<>());
            return grades;
        }

        List<Integer> courseIds = new ArrayList<>();
        courses.forEach(course -> courseIds.add(course.getId()));

        grades.add(getGrades(student, courseIds, Arrays.asList(KNOWLEDGE_CONTROL_PART1)));
        grades.add(getGrades(student, courseIds, Arrays.asList(KNOWLEDGE_CONTROL_PART2)));
        grades.add(getGrades(student, courseIds, Arrays.asList(KNOWLEDGE_CONTROL_PART3)));
        grades.add(getGrades(student, courseIds, Arrays.asList(KNOWLEDGE_CONTROL_PART4)));

        return grades;
    }

    private List<Grade> getGrades(Student student,
                                  List<Integer> courseIds,
                                  List<Integer> knowledgeControlTypes) {
        List<Grade> result = gradeRepository.getByStudentIdAndCoursesAndKCTypes(student.getId(),
                courseIds,
                knowledgeControlTypes);
        return result;
    }
}
