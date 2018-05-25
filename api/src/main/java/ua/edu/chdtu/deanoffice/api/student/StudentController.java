package ua.edu.chdtu.deanoffice.api.student;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentDTO;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentView;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.superclasses.NameEntity;
import ua.edu.chdtu.deanoffice.service.StudentService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;

@RestController
@RequestMapping("/students")
public class StudentController {
    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @JsonView(StudentView.Search.class)
    @GetMapping("/search")
    public List searchStudentByFullName(
            @RequestParam(value = "surname", defaultValue = "", required = false) String surname,
            @RequestParam(value = "name", defaultValue = "", required = false) String name,
            @RequestParam(value = "patronimic", defaultValue = "", required = false) String patronimic,
            @CurrentUser ApplicationUser user
            ) {
        List<Student> foundStudents = studentService.searchByFullName(name, surname, patronimic, user.getFaculty().getId());
        List<StudentDTO> foundStudentsDTO = map(foundStudents, StudentDTO.class);
        foundStudentsDTO.forEach(studentDTO -> {
            Student student = foundStudents.get(foundStudentsDTO.indexOf(studentDTO));
            studentDTO.setGroups(getGroupNamesForStudent(student));
        });
        return foundStudentsDTO;
    }

    private String getGroupNamesForStudent(Student student) {
        return student.getDegrees().stream()
                .map(StudentDegree::getStudentGroup)
                .filter(Objects::nonNull)
                .map(NameEntity::getName)
                .collect(Collectors.joining(", "));
    }

    @JsonView(StudentView.Personal.class)
    @GetMapping("/{student_id}")
    public ResponseEntity getStudentsById(@PathVariable("student_id") Integer studentId) {
        Student student = studentService.findById(studentId);
        return ResponseEntity.ok(map(student, StudentDTO.class));
    }

    @PutMapping
    public ResponseEntity updateStudent(@RequestBody Student student) {
        try {
            studentService.save(student);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @PutMapping("/{student_id}/photo")
    public ResponseEntity uploadPhotoForStudent(@RequestBody String photoUrl, @PathVariable(value = "student_id") int studentId) {
        try {
            studentService.addPhoto(photoUrl, studentId);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @GetMapping("/{student_id}/photo")
    public ResponseEntity getStudentPhoto(@PathVariable(value = "student_id") Integer id) {
        Student student = studentService.findById(id);
        return ResponseEntity.ok().body(student.getPhotoUrl());
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, StudentController.class);
    }
}