package ua.edu.chdtu.deanoffice.api.student;

import com.fasterxml.jackson.annotation.JsonView;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ua.edu.chdtu.deanoffice.api.student.dto.*;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.service.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {
    @Autowired StudentDegreeService studentDegreeService;
    @Autowired StudentService studentService;
    @Autowired DegreeService degreeService;
    @Autowired StudentGroupService studentGroupService;

    @JsonView(StudentDegreeViews.Simple.class)
    @GetMapping("/degrees")
    public List<StudentDegreeDTO> getActiveStudentsDegree(
            @RequestParam(value = "active", required = false, defaultValue = "true") boolean active
    ) {
        return getActiveStudentDegree(active);
    }

    @JsonView(StudentDegreeViews.Detail.class)
    @GetMapping("/degrees/more-detail")
    public List<StudentDegreeDTO> getActiveStudentsDegree_moreDetail(
            @RequestParam(value = "active", required = false, defaultValue = "true") boolean active
    ) {
        return getActiveStudentDegree(active);
    }

    private List<StudentDegreeDTO> getActiveStudentDegree(boolean active) {
        return parseToStudentDegreeDTO(studentDegreeService.getAllByActive(active));
    }

    @JsonView(StudentDegreeViews.Degree.class)
    @GetMapping("/degrees/{student_degree_ids}")
    public List<StudentDegreeDTO> getAllStudentsDegreeById(
            @PathVariable("student_degree_ids") Integer[] studentDegreeIds
    ) {
        return parseToStudentDegreeDTO(studentDegreeService.findAllByIds(studentDegreeIds));
    }

    private List<StudentDegreeDTO> parseToStudentDegreeDTO(List<StudentDegree> studentDegreeList) {
        return new ModelMapper().map(studentDegreeList,new TypeToken<List<StudentDegreeDTO>>() {}.getType());
    }

    @JsonView(StudentDegreeViews.Personal.class)
    @GetMapping("/{student_ids}")
    public List<StudentDTO> getAllStudentsById(
            @PathVariable("student_ids") Integer[] studentIds
    ) {
        return this.parseToStudentDTO(studentService.findAllByIds(studentIds));
    }

    private List<StudentDTO> parseToStudentDTO(List<Student> studentList) {
        return new ModelMapper().map(studentList,new TypeToken<List<StudentDTO>>() {}.getType());
    }

    @JsonView(StudentDegreeViews.Search.class)
    @GetMapping("/search")
    public List<StudentDTO> searchStudentByFullName(
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

    private String getGroupNamesForStudent(Student student) {
        List<String> groupNames = new ArrayList<>();
        student.getDegrees().forEach(
                studentDegree -> groupNames.add(studentDegree.getStudentGroup().getName())
        );
        return String.join(", ", groupNames);
    }

//TODO cr: потрібно робити обробку помилок при збереженні
    @JsonView(StudentDegreeViews.Degree.class)
    @PostMapping("/degrees")
    public ResponseEntity<StudentDegreeDTO> createNewStudentDegree(
            @RequestBody() StudentDegreeDTO newStudentDegree,
            @RequestParam(value = "new_student", defaultValue = "false", required = false) boolean newStudent
    ) {
        if (newStudentDegree.getStudentGroupId() == null) {
            return ResponseEntity.notFound().build();
        }

        Student student;
        if (newStudent) {
            student = createStudent(newStudentDegree.getStudent());
        } else {
            student = studentService.getById(newStudentDegree.getStudent().getId());
        }
        if (student == null) {
            return ResponseEntity.notFound().build();
        }

        StudentDegree studentDegree = createStudentDegree(newStudentDegree, student);
        URI location = getLocation(studentDegree.getId());
        return ResponseEntity.created(location).body(new ModelMapper().map(studentDegree, StudentDegreeDTO.class));
    }

    private Student createStudent(StudentDTO newStudentDTO) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        Student newStudent = modelMapper.map(newStudentDTO, Student.class);
        newStudent.setId(0);
        return studentService.save(newStudent);
    }

    private StudentDegree createStudentDegree(StudentDegreeDTO newStudentDegreeDTO, Student student) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        StudentDegree newStudentDegree = modelMapper.map(newStudentDegreeDTO, StudentDegree.class);
        newStudentDegree.setStudent(student);
        newStudentDegree.setStudentGroup(studentGroupService.getById(newStudentDegreeDTO.getStudentGroupId()));
        newStudentDegree.setDegree(newStudentDegree.getDegree());

        return studentDegreeService.save(newStudentDegree);
    }

    private URI getLocation(Integer id) {
        return ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
    }
}
