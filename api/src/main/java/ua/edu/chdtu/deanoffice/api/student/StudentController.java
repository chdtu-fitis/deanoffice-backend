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
import ua.edu.chdtu.deanoffice.service.DegreeService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;
import ua.edu.chdtu.deanoffice.service.StudentService;

import java.net.URI;
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
    public List<StudentDegreeDTO> getActiveStudentsDegree_simple(
            @RequestParam(value = "active", required = false, defaultValue = "true") boolean active
    ) {
        return getActiveStudentDegree(active);
    }

    @JsonView(StudentDegreeViews.Detail.class)
    @GetMapping("/degrees/more-detail")
    public List<StudentDegreeDTO> getActiveStudentsDegree_detail(
            @RequestParam(value = "active", required = false, defaultValue = "true") boolean active
    ) {
        return getActiveStudentDegree(active);
    }

    @GetMapping("/{student_ids}/personal-data")
    public List<StudentDTO> getAllStudentsById(
            @PathVariable("student_ids") Integer[] studentIds
    ) {
        return this.parseToStudentDTO(studentService.findAllByStudentIds(studentIds));
    }

    @JsonView(StudentDegreeViews.Degree.class)
    @GetMapping("/{student_degree_ids}/degree-data")
    public List<StudentDegreeDTO> getAllStudentsDegreeById(
            @PathVariable("student_degree_ids") Integer[] studentDegreeIds
    ) {
        return parseToStudentDegreeDTO(studentDegreeService.findAllByStudentDegreeIds(studentDegreeIds));
    }

    @JsonView(StudentDegreeViews.Search.class)
    @GetMapping("/search")
    public List<StudentDTO> searchStudentByNameSurnamePanronimic(
            @RequestParam(value = "name", defaultValue = "", required = false) String name,
            @RequestParam(value = "surname", defaultValue = "", required = false) String surname,
            @RequestParam(value = "patronimic", defaultValue = "", required = false) String patronimic
    ) {
        return parseToStudentDTO(studentService.searchStudentByFullName(name, surname, patronimic));
    }

    @JsonView(StudentDegreeViews.Degree.class)
    @PostMapping("/degrees")
    public ResponseEntity<StudentDegreeDTO> createNewStudentDegree(
            @RequestBody() NewStudentDegreeDTO newStudentDegreeDTO,
            @RequestParam(value = "new_student", defaultValue = "false", required = false) boolean newStudent
    ) {
        if (newStudentDegreeDTO.getDegreeId() == null || newStudentDegreeDTO.getStudentGroupId() == null) {
            return ResponseEntity.notFound().build();
        }

        Student student;
        if (newStudent) {
            student = createStudent(newStudentDegreeDTO.getStudent());
        } else {
            student = studentService.getStudentById(newStudentDegreeDTO.getStudent().getId());
        }
        if (student == null) {
            return ResponseEntity.notFound().build();
        }

        StudentDegree studentDegree = createStudentDegree(newStudentDegreeDTO, student);
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

    private StudentDegree createStudentDegree(NewStudentDegreeDTO newStudentDegreeDTO, Student student) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        StudentDegree newStudentDegree = modelMapper.map(newStudentDegreeDTO, StudentDegree.class);
        newStudentDegree.setDegree(degreeService.getDegree(newStudentDegreeDTO.getDegreeId()));
        newStudentDegree.setStudent(student);
        newStudentDegree.setStudentGroup(studentGroupService.getStudentGroupById(newStudentDegreeDTO.getStudentGroupId()));

        return studentDegreeService.save(newStudentDegree);
    }

    private URI getLocation(Integer id) {
        return ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
    }

    private List<StudentDTO> parseToStudentDTO(List<Student> studentList) {
        return new ModelMapper().map(studentList,new TypeToken<List<StudentDTO>>() {}.getType());
    }

    private List<StudentDegreeDTO> parseToStudentDegreeDTO(List<StudentDegree> studentDegreeList) {
        return new ModelMapper().map(studentDegreeList,new TypeToken<List<StudentDegreeDTO>>() {}.getType());
    }

    private List<StudentDegreeDTO> getActiveStudentDegree(boolean active) {
        return parseToStudentDegreeDTO(studentDegreeService.findAllByActiveId(active));
    }
}
