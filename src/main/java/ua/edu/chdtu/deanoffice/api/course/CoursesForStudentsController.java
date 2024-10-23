package ua.edu.chdtu.deanoffice.api.course;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.course.dto.CourseDTO;
import ua.edu.chdtu.deanoffice.api.course.dto.CourseForGroupView;
import ua.edu.chdtu.deanoffice.api.course.dto.coursesforstudents.CourseForStudentDTO;
import ua.edu.chdtu.deanoffice.api.course.dto.coursesforstudents.CourseForStudentWriteDTO;
import ua.edu.chdtu.deanoffice.api.course.util.CourseForGroupUpdateHolder;
import ua.edu.chdtu.deanoffice.api.course.util.CourseForStudentUpdateHolder;
import ua.edu.chdtu.deanoffice.api.general.dto.MessageDTO;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.CourseForStudent;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.exception.NotFoundException;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.exception.UnauthorizedFacultyDataException;
import ua.edu.chdtu.deanoffice.service.course.CourseService;
import ua.edu.chdtu.deanoffice.service.course.CoursesForStudentsService;

import java.util.List;

import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;
import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.strictMap;

@RestController
public class CoursesForStudentsController {
    private CoursesForStudentsService coursesForStudentsService;
    private CourseService courseService;

    public CoursesForStudentsController(CoursesForStudentsService coursesForStudentsService, CourseService courseService) {
        this.coursesForStudentsService = coursesForStudentsService;
        this.courseService = courseService;
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
    @DeleteMapping("/courses-for-students/{studentDegreeId}")
    public ResponseEntity deleteCoursesForStudent(@PathVariable int studentDegreeId, @RequestParam List<Integer> courseIds) throws UnauthorizedFacultyDataException {
        String result = coursesForStudentsService.deleteCoursesForStudent(studentDegreeId, courseIds);
        return new ResponseEntity(new MessageDTO(result), HttpStatus.OK);
    }

    @Secured({"ROLE_DEANOFFICER"})
    @PutMapping("/courses-for-students/{studentDegreeId}/courses")
    public ResponseEntity<?> updateCourseForStudent(@PathVariable int studentDegreeId, @RequestBody CourseForStudentUpdateHolder coursesForStudentUpdateHolder) throws OperationCannotBePerformedException, UnauthorizedFacultyDataException {
        try {
            CourseDTO updatedCourse = courseService.updateCourse(studentDegreeId, coursesForStudentUpdateHolder);
            return ResponseEntity.ok(updatedCourse);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (UnauthorizedFacultyDataException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
    
}
