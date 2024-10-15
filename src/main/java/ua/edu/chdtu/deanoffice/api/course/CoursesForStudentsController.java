package ua.edu.chdtu.deanoffice.api.course;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import ua.edu.chdtu.deanoffice.api.course.dto.CourseDTO;
import ua.edu.chdtu.deanoffice.api.course.dto.coursesforstudents.CourseForStudentDTO;
import ua.edu.chdtu.deanoffice.api.course.dto.coursesforstudents.CourseForStudentWriteDTO;
import ua.edu.chdtu.deanoffice.api.general.dto.MessageDTO;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.CourseForStudent;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
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
    @DeleteMapping("/courses-for-students/{studentDegreeId}")
    public ResponseEntity deleteCoursesForStudent(@PathVariable int studentDegreeId, @RequestParam List<Integer> courseIds) throws UnauthorizedFacultyDataException {
        String result = coursesForStudentsService.deleteCoursesForStudent(studentDegreeId, courseIds);
        return new ResponseEntity(new MessageDTO(result), HttpStatus.OK);
    }
}
