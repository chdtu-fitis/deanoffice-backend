package ua.edu.chdtu.deanoffice.api.student;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentDTO;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentStatusDTO;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentView;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.superclasses.NameEntity;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.service.StudentService;
import ua.edu.chdtu.deanoffice.api.student.dto.ShortStudentInfoDTO;

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

    @Secured("ROLE_DEANOFFICER")
    @JsonView(StudentView.Search.class)
    @GetMapping("/search")
    public List searchStudentByFullName(
            @RequestParam(value = "surname", defaultValue = "", required = false) String surname,
            @RequestParam(value = "name", defaultValue = "", required = false) String name,
            @RequestParam(value = "patronimic", defaultValue = "", required = false) String patronimic
            ) {
        List<Student> foundStudents = studentService.searchByFullName(name, surname, patronimic);
        List<StudentDTO> foundStudentsDTO = map(foundStudents, StudentDTO.class);
        foundStudentsDTO.forEach(studentDTO -> {
            Student student = foundStudents.get(foundStudentsDTO.indexOf(studentDTO));
            studentDTO.setGroups(getGroupNamesForStudent(student));
        });
        return foundStudentsDTO;
    }
    @GetMapping("/search-by-name-and-code")
    public Integer[] searchStudentByFullNameAndSpecialityCode(
            @RequestParam(value = "surname") String surname,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "patronimic", defaultValue = "", required = false) String patronimic,
            @RequestParam(value = "code") String code
    ) {
        return studentService.searchStudentByFullNameAndSpecialityCode(name, surname, patronimic,code);
    }

    private String getGroupNamesForStudent(Student student) {
        return student.getDegrees().stream()
                .map(StudentDegree::getStudentGroup)
                .filter(Objects::nonNull)
                .map(NameEntity::getName)
                .collect(Collectors.joining(", "));
    }

    @Secured("ROLE_DEANOFFICER")
    @JsonView(StudentView.Personal.class)
    @GetMapping("/{student_id}")
    public ResponseEntity getStudentsById(@PathVariable("student_id") Integer studentId) {
        try {
            Student student = studentService.findById(studentId);
            return ResponseEntity.ok(map(student, StudentDTO.class));
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @Secured("ROLE_DEANOFFICER")
    @PutMapping
    public ResponseEntity updateStudent(@RequestBody Student student) {
        try {
            if (student.getId() == 0) {
                handleException(new OperationCannotBePerformedException("Не можна змінити дані неіснуючого студента"));
            }
            studentService.save(student);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @Secured("ROLE_DEANOFFICER")
    @PutMapping("/{student_id}/photo")
    public ResponseEntity uploadPhotoForStudent(@RequestBody String photoUrl, @PathVariable(value = "student_id") int studentId) {
        try {
            studentService.addPhoto(photoUrl, studentId);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @Secured("ROLE_DEANOFFICER")
    @GetMapping("/{student_id}/photo")
    public ResponseEntity getStudentPhoto(@PathVariable(value = "student_id") Integer id) {
        try {
            Student student = studentService.findById(id);
            return ResponseEntity.ok().body(student.getPhotoUrl());
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, StudentController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }

    @GetMapping("/status")
    public ResponseEntity<StudentStatusDTO> getStudentStatus(@RequestParam int studentId) {
        StudentStatusDTO studentStatusDTO = new StudentStatusDTO(studentService.getStudentStatus(studentId));
        return ResponseEntity.ok(studentStatusDTO);
    }

    @GetMapping("all-active-students")
    public ResponseEntity<List<ShortStudentInfoDTO>> getAllActiveStudents(@RequestParam(required = false) String facultyAbbr) {
        List<ShortStudentInfoDTO> shortStudentsInfo = studentService.getAllActiveStudents(facultyAbbr).stream()
                .map(studentInfoBean -> new ShortStudentInfoDTO(
                        studentInfoBean.getId(),
                        studentInfoBean.getSurname() + " " + studentInfoBean.getName() + " " + studentInfoBean.getPatronimic(),
                        studentInfoBean.getStudentGroupName(),
                        studentInfoBean.getSpecialityCode()
                ))
                .collect(Collectors.toList());;
        return ResponseEntity.ok(shortStudentsInfo);
    }
}
