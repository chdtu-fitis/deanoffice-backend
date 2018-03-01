package ua.edu.chdtu.deanoffice.api.student;

import com.fasterxml.jackson.annotation.JsonView;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.student.dto.*;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.StudentService;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {
    @Autowired StudentDegreeService studentDegreeService;
    @Autowired StudentService studentService;

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
    @PutMapping("/search")
    public List<StudentDTO> searchStudentByNameSurnamePanronimic(
            @RequestBody() StudentDTO student
    ) {
        return new ModelMapper().map(
                studentService.searchStudentByFullName(
                        student.getName(), student.getSurname(), student.getPatronimic()
                ), new TypeToken<List<StudentDTO>>() {}.getType()
        );
    }
}
