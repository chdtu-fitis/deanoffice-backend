package ua.edu.chdtu.deanoffice.api.student;

import com.fasterxml.jackson.annotation.JsonView;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.student.dto.*;
import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.service.StudentService;

import java.util.List;
import java.util.stream.Collectors;

import static ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice.handleException;

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
        List<StudentDTO> foundStudentDTO = parseToStudentDTO(foundStudent);
        foundStudentDTO.forEach(studentDTO -> {
            Student student = foundStudent.get(foundStudentDTO.indexOf(studentDTO));
            studentDTO.setGroups(getGroupNamesForStudent(student));
        });
        return foundStudentDTO;
    }

    private List<StudentDTO> parseToStudentDTO(List<Student> studentList) {
        return new ModelMapper().map(studentList, new TypeToken<List<StudentDTO>>() {}.getType());
    }

    private String getGroupNamesForStudent(Student student) {
        return student.getDegrees().stream()
                .map(studentDegree -> studentDegree.getStudentGroup().getName())
                .collect(Collectors.joining(", "));
    }

    @JsonView(StudentView.Personal.class)
    @GetMapping("/{id}")
    public ResponseEntity getAllStudentsId(
            @PathVariable("id") Integer studentId
    ) {
        return ResponseEntity.ok(parseToStudentDTO(studentService.findById(studentId)));
    }

    static StudentDTO parseToStudentDTO(Student student) {
        return new ModelMapper().map(student, StudentDTO.class);
    }

    @PutMapping("/")
    public ResponseEntity updateStudent(@RequestBody Student student) {
        Student upStudent;
        try {
            upStudent = studentService.update(student);
        } catch (Exception exception) {
            return handleException(exception);
        }
        return ResponseEntity.ok(parseToStudentDTO(upStudent));
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
        if (student.getPhoto() == null) {
            return ResponseEntity.unprocessableEntity().body("Student with id " + id + " don`t have a photo");
        }
        byte[] photo = student.getPhoto();
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(photo);
    }
}