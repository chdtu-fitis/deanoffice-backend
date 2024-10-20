package ua.edu.chdtu.deanoffice.service.course;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.api.course.dto.coursesforstudents.CourseForStudentWriteDTO;
import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.exception.UnauthorizedFacultyDataException;
import ua.edu.chdtu.deanoffice.repository.CourseRepository;
import ua.edu.chdtu.deanoffice.repository.CoursesForStudentsRepository;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;
import ua.edu.chdtu.deanoffice.repository.TeacherRepository;
import ua.edu.chdtu.deanoffice.security.FacultyAuthorized;

import java.util.List;
import java.util.Optional;

@Service
public class CoursesForStudentsService {
    private CoursesForStudentsRepository coursesForStudentsRepository;
    private StudentDegreeRepository studentDegreeRepository;
    private CourseRepository courseRepository;
    private TeacherRepository teacherRepository;

    public CoursesForStudentsService(CoursesForStudentsRepository coursesForStudentsRepository,
                                     StudentDegreeRepository studentDegreeRepository,
                                     CourseRepository courseRepository,
                                     TeacherRepository teacherRepository) {
        this.coursesForStudentsRepository = coursesForStudentsRepository;
        this.studentDegreeRepository = studentDegreeRepository;
        this.courseRepository = courseRepository;
        this.teacherRepository = teacherRepository;
    }

    public List<CourseForStudent> getCoursesForStudentDegreeAndSemester(List<Integer> studentDegreeIds, int semester) {
        return coursesForStudentsRepository.getByStudentIdAndSemester(studentDegreeIds, semester);
    }

    public List<CourseForStudent> getByIds(List<Integer> courseIds) {
        return coursesForStudentsRepository.findByIds(courseIds);
    }

    @FacultyAuthorized
    public String insertCoursesForStudentDegree(StudentDegree studentDegree, List<CourseForStudentWriteDTO> coursesForStudents)
            throws UnauthorizedFacultyDataException {
        int added = 0, notAdded = 0;
        for (CourseForStudentWriteDTO courseForStudentDTO : coursesForStudents) {
            Course course = courseRepository.getById(courseForStudentDTO.getCourseId());
            Teacher teacher = null;
            if (courseForStudentDTO.getTeacherId() != null) {
                teacher = teacherRepository.getById(courseForStudentDTO.getTeacherId());
            }
            CourseForStudent courseForStudent = new CourseForStudent(course, studentDegree, teacher, courseForStudentDTO.getCourseType());
            try {
                coursesForStudentsRepository.save(courseForStudent);
                added++;
            } catch(Exception e) {
                notAdded++;
            }
        }
        String result = "Додано " + added + " предметів";
        if (notAdded > 0)
            result += " . Помилка при додаванні " + notAdded + " предметів.";
        return result;
    }

    public StudentDegree getStudentDegreeById(int studentDegreeId) throws OperationCannotBePerformedException {
        Optional<StudentDegree> studentDegreeOptional = studentDegreeRepository.findById(studentDegreeId);
        StudentDegree studentDegree = studentDegreeOptional.orElseThrow(() ->
                new OperationCannotBePerformedException("Студента з таким id не існує"));
        return studentDegree;
    }
}
