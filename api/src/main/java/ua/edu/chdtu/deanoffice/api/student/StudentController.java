package ua.edu.chdtu.deanoffice.api.student;

import com.fasterxml.jackson.annotation.JsonView;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.student.dto.*;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.service.DegreeService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.StudentService;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {
    @Autowired StudentDegreeService studentDegreeService;
    @Autowired StudentService studentService;
    @Autowired DegreeService degreeService;

    private List<StudentDegreeDTO> getStudentDegreeDTOListByActive(boolean active) {
        return new ModelMapper().map(
                studentDegreeService.findAllByActiveId(active),
                new TypeToken<List<StudentDegreeDTO>>() {}.getType()
        );
    }

    @JsonView(StudentDegreeViews.Simple.class)
    @GetMapping("/degrees")
    public List<StudentDegreeDTO> getActiveStudentsDegree_simple(
            @RequestParam(value = "active", required = false, defaultValue = "true") boolean active
    ) {
        return getStudentDegreeDTOListByActive(active);
    }

    @JsonView(StudentDegreeViews.Detail.class)
    @GetMapping("/degrees/more-detail")
    public List<StudentDegreeDTO> getActiveStudentsDegree_detail(
            @RequestParam(value = "active", required = false, defaultValue = "true") boolean active
    ) {
        return getStudentDegreeDTOListByActive(active);
    }

    @GetMapping("/{student_ids}/personal-data")
    public List<StudentDTO> getAllStudentsById(
            @PathVariable("student_ids") Integer[] studentIds
    ) {
        return new ModelMapper().map(
                studentService.findAllByStudentIds(studentIds),
                new TypeToken<List<StudentDTO>>() {}.getType()
        );
    }

    @JsonView(StudentDegreeViews.Degree.class)
    @GetMapping("/{student_degree_ids}/degree-data")
    public List<StudentDegreeDTO> getAllStudentsDegreeById(
            @PathVariable("student_degree_ids") Integer[] studentDegreeIds
    ) {
        return new ModelMapper().map(
                studentDegreeService.findAllByStudentDegreeIds(studentDegreeIds),
                new TypeToken<List<StudentDegreeDTO>>() {}.getType()
        );
    }

    @JsonView(StudentDegreeViews.Search.class)
    @GetMapping("/search")
    public List<StudentDTO> searchStudentByNameSurnamePanronimic(
            @RequestParam(value = "name", defaultValue = "", required = false) String name,
            @RequestParam(value = "surname", defaultValue = "", required = false) String surname,
            @RequestParam(value = "patronimic", defaultValue = "", required = false) String patronimic
    ) {
        name = java.net.URLDecoder.decode(name);
        surname = java.net.URLDecoder.decode(surname);
        patronimic = java.net.URLDecoder.decode(patronimic);
        return new ModelMapper().map(
                studentService.searchStudentByFullName(name, surname, patronimic),
                new TypeToken<List<StudentDTO>>() {}.getType()
        );
    }

 //   @JsonView(StudentDegreeViews.Degree.class)
    @PostMapping("/degrees")
    public ResponseEntity<StudentDegreeDTO> createNewStudentDegree(
            @RequestBody() NewStudentDegreeDTO newStudentDegreeDTO
    ) {
        return ResponseEntity.ok(
                new ModelMapper().map(
                        createStudentDegree(newStudentDegreeDTO),
                        new TypeToken<StudentDegreeDTO>() {}.getType()
                )
        );
    }

    private StudentDegree createStudentDegree(NewStudentDegreeDTO newStudentDegreeDTO) {
        StudentDegree newStudentDegree = new ModelMapper().map(
                newStudentDegreeDTO,
                new TypeToken<StudentDegree>() {}.getType()
        );

        newStudentDegree.setDegree(degreeService.getDegree(newStudentDegreeDTO.getDegreeId()));
        newStudentDegree.setStudent(studentService.getStudentById(newStudentDegreeDTO.getStudentId()));
        newStudentDegree.setStudentGroup(
                studentService.getStudentGroupById(newStudentDegreeDTO.getStudentGroupId())
        );
        return studentDegreeService.save(newStudentDegree);
    }
}
