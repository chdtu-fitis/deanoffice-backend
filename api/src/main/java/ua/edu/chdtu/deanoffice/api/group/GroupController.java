package ua.edu.chdtu.deanoffice.api.group;

import com.fasterxml.jackson.annotation.JsonView;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.general.PersonFullNameDTO;
import ua.edu.chdtu.deanoffice.api.group.dto.StudentGroupDTO;
import ua.edu.chdtu.deanoffice.api.group.dto.StudentGroupView;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentDTO;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;

import java.lang.reflect.Type;
import java.util.List;

@RestController
@RequestMapping("/")
public class GroupController {
    private StudentGroupService studentGroupService;

    @Autowired
    public GroupController(
            StudentGroupService studentGroupService
    ) {
        this.studentGroupService = studentGroupService;
    }

    @JsonView(StudentGroupView.WithStudents.class)
    @GetMapping("/groups/graduates")
    public ResponseEntity getGraduateGroups(@RequestParam Integer degreeId) {
        List<StudentGroup> groups = studentGroupService.getGraduateGroups(degreeId);
        return ResponseEntity.ok(parseToStudentGroupDTO(groups));
    }

    private List<StudentGroupDTO> parseToStudentGroupDTO(List<StudentGroup> studentGroupList) {
        ModelMapper modelMapper = new ModelMapper();
        Type listType = new TypeToken<List<StudentGroupDTO>>() {}.getType();
        return modelMapper.map(studentGroupList, listType);
    }

    @GetMapping("/groups")
    @JsonView(StudentGroupView.GroupData.class)
    public ResponseEntity getGroups() {
        List<StudentGroup> studentGroups = studentGroupService.getGroups();
        return ResponseEntity.ok(parseToStudentGroupDTO(studentGroups));
    }

    @GetMapping("/groups/filter")
    @JsonView(StudentGroupView.WithStudents.class)
    public ResponseEntity getGroupsByDegreeAndYear(
            @RequestParam Integer degreeId,
            @RequestParam Integer year
    ) {
        List<StudentGroup> groups = studentGroupService.getGroupsByDegreeAndYear(degreeId, year);
        return ResponseEntity.ok(parseToStudentGroupDTO(groups));
    }

    @GetMapping("courses/{courseId}/groups")
    @JsonView(StudentGroupView.Basic.class)
    public ResponseEntity getGroupsByCourse(@PathVariable String courseId) {
        List<StudentGroup> studentGroups = studentGroupService.getGroupsByCourse(Integer.parseInt(courseId));
        return ResponseEntity.ok(parseToStudentGroupDTO(studentGroups));
    }

    @RequestMapping(method = RequestMethod.GET, path = "/groups/{id}/students")
    public ResponseEntity<List<PersonFullNameDTO>> getGroupStudents(@PathVariable Integer id) {
        List<Student> students = studentGroupService.getGroupStudents(id);
        return ResponseEntity.ok(parseToPersonFullNameDTO(students));
    }

    private List<PersonFullNameDTO> parseToPersonFullNameDTO(List<Student> students) {
        ModelMapper modelMapper = new ModelMapper();
        Type listType = new TypeToken<List<PersonFullNameDTO>>() {}.getType();
        return modelMapper.map(students, listType);
    }
}
