package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.Grade;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.repository.CourseRepository;
import ua.edu.chdtu.deanoffice.repository.GradeRepository;
import ua.edu.chdtu.deanoffice.repository.StudentRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class GradeService {

    public static final int EXAM = 1;
    public static final int CREDIT = 2;
    public static final int COURSEWORK = 3;
    public static final int COURSE_PROJECT = 4;
    public static final int DIFFERENTIATED_CREDIT = 5;
    public static final int STATE_EXAM = 6;
    public static final int ATTESTATION = 7;
    public static final int INTERNSHIP = 8;

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
        //List<Course> courses = courseRepository.findAll();
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
        List<Integer> knowledgeControlIds = new ArrayList<>();

        knowledgeControlIds.add(EXAM);
        knowledgeControlIds.add(CREDIT);
        knowledgeControlIds.add(DIFFERENTIATED_CREDIT);
        grades.add(getGrades(student, courseIds, knowledgeControlIds));

        knowledgeControlIds.add(COURSEWORK);
        knowledgeControlIds.add(COURSE_PROJECT);
        grades.add(getGrades(student, courseIds, knowledgeControlIds));

        knowledgeControlIds.add(INTERNSHIP);
        grades.add(getGrades(student, courseIds, knowledgeControlIds));

        knowledgeControlIds.add(ATTESTATION);
        grades.add(getGrades(student, courseIds, knowledgeControlIds));

        return grades;
    }

    private List<Grade> getGrades(Student student,
                                  List<Integer> courseIds,
                                  List<Integer> knowledgeControlTypes) {
        List<Grade> result = gradeRepository.getByStudentIdAndCoursesAndKCTypes(student.getId(),
                courseIds,
                knowledgeControlTypes);
        knowledgeControlTypes.clear();
        return result;
    }
}
