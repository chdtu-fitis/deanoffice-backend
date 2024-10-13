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

import java.util.*;
import java.util.stream.Collectors;

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
        // Перевірка існування студенту та курсу
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

        // Видалення
        try {
            coursesForStudentsRepository.deleteStudentFromCourseByStudentDegreeIdAndCourseId(studentDegreeId, courseId);
        } catch (Exception e) {
            throw new OperationCannotBePerformedException("Помилка при видаленні предмету: " + e.getMessage());
        }
    }


    @FacultyAuthorized
    public String deleteStudentsFromCourses(List<Integer> studentDegreeIds, List<Integer> courseIds) throws NotFoundException {
        StringBuilder result = new StringBuilder();
        boolean anySuccess = false;  // Помечаем, что хотя бы одно действие прошло успешно
        List<Integer> failedCourses = new ArrayList<>();  // Курсы, которые не найдены
        List<Integer> failedStudents = new ArrayList<>(); // Студенты, которые не найдены
        Set<Integer> notValidCourse = new HashSet<>();
        Set<Integer> successfullyDeletedStudents = new HashSet<>();

        List<String> failureReasons = new ArrayList<>();

        // Перевірка існування студентів
        List<Integer> existingStudents = studentDegreeIds.stream()
                .filter(coursesForStudentsRepository::existsByStudentDegreeId)
                .toList();

        // Перевірка існування курсів
        for (Integer courseId : courseIds) {
            if (!coursesForStudentsRepository.existsByCourseId(courseId)) {
                failedCourses.add(courseId);
                failureReasons.add("Курс з ID " + courseId + " не знайдено.");
                continue; // Якщо курсу не існує - пропускаємо
            }

            List<Integer> studentsIdOnCourse = coursesForStudentsRepository.getStudentsOnCourseByCourseId(courseId);
            List<Integer> successfullyDeletedIds = new ArrayList<>();

            for (Integer studentDegreeId : existingStudents) {
                if (studentsIdOnCourse.contains(studentDegreeId)) {
                    coursesForStudentsRepository.deleteStudentFromCourseByStudentDegreeIdAndCourseId(studentDegreeId, courseId);
                    successfullyDeletedIds.add(studentDegreeId);
                    successfullyDeletedStudents.add(studentDegreeId);
                    anySuccess = true;
                } else {
                    notValidCourse.add(studentDegreeId);
                }
            }

            if (!successfullyDeletedIds.isEmpty()) {
                result.append("Успішно видалені студенти з ID: ")
                        .append(successfullyDeletedIds)
                        .append(" з курса під ID: ").append(courseId).append(".\n");
            }
        }

        // Додаємо неіснуючих студентів у список помилок
        failedStudents.addAll(studentDegreeIds.stream()
                .filter(studentId -> !existingStudents.contains(studentId))
                .toList());

        if (!failedStudents.isEmpty()) {
            result.append("Студенти з ID: ").append(failedStudents)
                    .append(" не існують або не зареєстровані на жодному курсі.\n");
            failureReasons.add("Студенти з ID " + failedStudents + " не існують або не зареєстровані на жодному курсі."); // Добавляем причину
        }
        if (!failedCourses.isEmpty()) {
            result.append("Курси з ID ").append(failedCourses)
                    .append(" не знайдено.\n");
        }

        Set<Integer> difference = new HashSet<>(notValidCourse);
        difference.removeAll(successfullyDeletedStudents);

        if (!difference.isEmpty()) {
            result.append("Студенти з ID: ").append(notValidCourse)
                    .append(" не зареєстровані на обраних курсах.\n");
            failureReasons.add("Студенти з ID " + difference + " не зареєстровані на обраних курсах.");
        }

        if (!anySuccess) {
            String reasons = String.join("\n", failureReasons);
            throw new NotFoundException("Операція завершилася невдачею: жодного студента не вдалося видалити.\n" + reasons);
        }

        return result.toString();
    }



}
