package ua.edu.chdtu.deanoffice.service.course;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.api.course.dto.coursesforstudents.CourseForStudentWriteDTO;
import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.exception.NotFoundException;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.exception.UnauthorizedFacultyDataException;
import ua.edu.chdtu.deanoffice.repository.CourseRepository;
import ua.edu.chdtu.deanoffice.repository.CoursesForStudentsRepository;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;
import ua.edu.chdtu.deanoffice.repository.TeacherRepository;
import ua.edu.chdtu.deanoffice.security.FacultyAuthorized;
import ua.edu.chdtu.deanoffice.service.course.selective.SelectiveCourseService;
import ua.edu.chdtu.deanoffice.service.course.selective.SelectiveCoursesStudentDegreesService;

import java.util.ArrayList;
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

    @FacultyAuthorized
    public void deleteStudentFromCourseByStudentDegreeIdAndCourseId(int studentDegreeId, int courseId) throws NotFoundException, OperationCannotBePerformedException {
        // Проверяем, существует ли студент и курс
        boolean studentExists = coursesForStudentsRepository.existsByStudentDegreeId(studentDegreeId);
        boolean courseExists = coursesForStudentsRepository.existsByCourseId(courseId);

        List<Integer> studentsIdOnCourse =
                coursesForStudentsRepository.getStudentsOnCourseByCourseId(courseId);

        if (!courseExists) {
            throw new NotFoundException("Курс з ID: " + courseId + " не знайдено.");
        }
        if (!studentExists) {
            throw new NotFoundException("Студент з ID: " + studentDegreeId + " не знайдено.");
        }
        if (!studentsIdOnCourse.contains(studentDegreeId)) {
            throw new NotFoundException("Студента з ID: " + studentDegreeId + " не зареєстровано на курсі з ID: " + courseId);
        }

        // Выполняем удаление
        try {
            coursesForStudentsRepository.deleteStudentFromCourseByStudentDegreeIdAndCourseId(studentDegreeId, courseId);
        } catch (Exception e) {
            throw new OperationCannotBePerformedException("Помилка при видаленні предмету: " + e.getMessage());
        }
    }


    @FacultyAuthorized
    public String deleteStudentsFromCourses(List<Integer> studentDegreeIds, List<Integer> courseIds) {
        StringBuilder result = new StringBuilder();
        try {
            for (Integer courseId : courseIds) {
                // Усі студенти для i-го курсу
                List<Integer> studentsIdOnCourse =
                        coursesForStudentsRepository.getStudentsOnCourseByCourseId(courseId);

                // Якщо студент є на курсі - видаляємо
                List<Integer> successfullyDeletedIds = new ArrayList<>();
                for (Integer studentDegreeId : studentDegreeIds) {
                    if (studentsIdOnCourse.contains(studentDegreeId)) {
                        coursesForStudentsRepository.deleteStudentFromCourseByStudentDegreeIdAndCourseId(studentDegreeId, courseId);
                        successfullyDeletedIds.add(studentDegreeId);
                    }
                }

                if (!successfullyDeletedIds.isEmpty()) {
                    result.append("Успішно видалені студенти з ID: ").append(successfullyDeletedIds).append(" з курса під ID: ").append(courseId).append(".\n");
                } else {
                    result.append("Немає студентів для видалення з курсу з ID: ").append(courseId).append(".\n");
                }
            }
        } catch (Exception e) {
            return "Помилка при видаленні студентів: " + e.getMessage();
        }

        return result.toString();
    }
}
