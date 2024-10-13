package ua.edu.chdtu.deanoffice.api.course;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.course.dto.CourseDTO;
import ua.edu.chdtu.deanoffice.api.course.dto.coursesforstudents.CourseForStudentDTO;
import ua.edu.chdtu.deanoffice.api.course.dto.coursesforstudents.CourseForStudentWriteDTO;
import ua.edu.chdtu.deanoffice.api.course.dto.coursesforstudents.DeleteStudentsRequestDTO;
import ua.edu.chdtu.deanoffice.api.general.dto.MessageDTO;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.CourseForStudent;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.exception.NotFoundException;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.exception.UnauthorizedFacultyDataException;
import ua.edu.chdtu.deanoffice.service.course.CoursesForStudentsService;

import java.util.List;

import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;
import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.strictMap;

@RestController
public class CoursesForStudentsController {
    private CoursesForStudentsService coursesForStudentsService;

    public CoursesForStudentsController(CoursesForStudentsService coursesForStudentsService) {
        this.coursesForStudentsService = coursesForStudentsService;
    }

    @Secured({"ROLE_DEANOFFICER"})
    @GetMapping("/courses-for-students")
    public ResponseEntity<List<CourseForStudentDTO>> getCoursesForStudentsBySemester(@RequestParam List<Integer> studentDegreeIds,
                                                                               @RequestParam(value = "semester") int semester) {
        List<CourseForStudent> coursesForStudents = coursesForStudentsService.getCoursesForStudentDegreeAndSemester(studentDegreeIds, semester);
        return ResponseEntity.ok(strictMap(coursesForStudents, CourseForStudentDTO.class));
    }

    @Secured({"ROLE_DEANOFFICER"})
    @PostMapping("/courses-for-students/{studentDegreeId}")
    public ResponseEntity addCoursesForStudents(@PathVariable int studentDegreeId,
                                                @RequestBody List<CourseForStudentWriteDTO> coursesForStudents)
            throws OperationCannotBePerformedException, UnauthorizedFacultyDataException {
        StudentDegree studentDegree = coursesForStudentsService.getStudentDegreeById(studentDegreeId);
        String result = coursesForStudentsService.insertCoursesForStudentDegree(studentDegree, coursesForStudents);
        return new ResponseEntity(new MessageDTO(result), HttpStatus.CREATED);
    }

    @Secured({"ROLE_DEANOFFICER"})
    @DeleteMapping("/courses-for-students/delete-student/{studentDegreeId}/FromCourse/{courseId}")
    public ResponseEntity<String> deleteStudentFromCourse(@PathVariable int studentDegreeId, @PathVariable int courseId)
            throws UnauthorizedFacultyDataException, NotFoundException, OperationCannotBePerformedException {
        coursesForStudentsService.deleteStudentFromCourseByStudentDegreeIdAndCourseId(studentDegreeId, courseId);
        String successMessage = "Студента з ID: " + studentDegreeId + " видалено з курсу під ID: " + courseId;
        return ResponseEntity.ok(successMessage); // Возвращаем сообщение об успешном удалении
    }

    @Secured({"ROLE_DEANOFFICER"})
    @DeleteMapping("/courses-for-students/delete-students/")
    public ResponseEntity<String> deleteStudentsFromCourse(
            @RequestBody DeleteStudentsRequestDTO request) {
        try {
            String result = coursesForStudentsService.deleteStudentsFromCourses(request.getStudentDegreeIds(), request.getCourseIds());
            return ResponseEntity.ok(result);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Помилка при видаленні студентів: " + e.getMessage());
        }
    }

}
