package ua.edu.chdtu.deanoffice.api.student;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.service.StudentService;

import java.util.List;
import java.util.stream.Collectors;

import static ua.edu.chdtu.deanoffice.api.general.parser.Parser.parse;

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
            @RequestParam(value = "name", defaultValue = "", required = false) String name,
            @RequestParam(value = "surname", defaultValue = "", required = false) String surname,
            @RequestParam(value = "patronimic", defaultValue = "", required = false) String patronimic
    ) {
        List<Student> foundStudent = studentService.searchByFullName(name, surname, patronimic);
        List<StudentDTO> foundStudentDTO = parse(foundStudent, StudentDTO.class);
        foundStudentDTO.forEach(studentDTO -> {
            Student student = foundStudent.get(foundStudentDTO.indexOf(studentDTO));
            studentDTO.setGroups(getGroupNamesForStudent(student));
        });
        return foundStudentDTO;
    }

    private String getGroupNamesForStudent(Student student) {
        return student.getDegrees().stream()
                .map(studentDegree -> studentDegree.getStudentGroup().getName())
                .collect(Collectors.joining(", "));
    }

    @JsonView(StudentView.Personal.class)
    @GetMapping("/{student_id}")
    public ResponseEntity getStudentsById(@PathVariable("student_id") Integer studentId) {
        Student student = studentService.findById(studentId);
        return ResponseEntity.ok(parse(student, StudentDTO.class));
    }

    @PutMapping("/")
    public ResponseEntity updateStudent(@RequestBody Student student) {
        try {
            Student upStudent = studentService.update(student);
            return ResponseEntity.ok(parse(upStudent, StudentDTO.class));
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @PutMapping("/{id}/photo")
    public ResponseEntity uploadPhotoForStudent(@RequestBody byte[] photo, @PathVariable(value = "id") Integer id) {
        try {
            Student student = studentService.findById(id);
            student.setPhoto(photo);
            studentService.update(student);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @GetMapping("/{id}/photo")
    public ResponseEntity getStudentPhoto(@PathVariable(value = "id") Integer id) {
        Student student = studentService.findById(id);
        if (student == null) {
            return ResponseEntity.notFound().eTag("Not found student with id " + id).build();
        }
        byte[] photo = student.getPhoto();
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(photo);
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, StudentController.class);
    }
}