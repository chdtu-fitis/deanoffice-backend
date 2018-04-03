package ua.edu.chdtu.deanoffice.api.group;

import com.fasterxml.jackson.annotation.JsonView;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.group.dto.StudentDegreeFullNameDTO;
import ua.edu.chdtu.deanoffice.api.group.dto.StudentGroupDTO;
import ua.edu.chdtu.deanoffice.api.group.dto.StudentGroupShortDTO;
import ua.edu.chdtu.deanoffice.api.group.dto.StudentGroupView;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
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
    public ResponseEntity getGraduateGroups(@RequestParam int degreeId) {
        List<StudentGroup> groups = studentGroupService.getGraduateGroups(degreeId);
        return ResponseEntity.ok(parseToStudentGroupShortDTO(groups));
    }

    private List<StudentGroupDTO> parseToStudentGroupDTO(List<StudentGroup> studentGroupList) {
        ModelMapper modelMapper = new ModelMapper();
        Type listType = new TypeToken<List<StudentGroupDTO>>() {
        }.getType();
        return modelMapper.map(studentGroupList, listType);
    }

    private List<StudentGroupShortDTO> parseToStudentGroupShortDTO(List<StudentGroup> studentGroupList) {
        ModelMapper modelMapper = new ModelMapper();
        Type listType = new TypeToken<List<StudentGroupShortDTO>>() {
        }.getType();
        return modelMapper.map(studentGroupList, listType);
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
    public ResponseEntity getGroupsByCourse(@PathVariable int courseId) {
        List<StudentGroup> studentGroups = studentGroupService.getGroupsByCourse(courseId);
        return ResponseEntity.ok(parseToStudentGroupDTO(studentGroups));
    }

    @GetMapping("/groups")
    @JsonView(StudentGroupView.AllGroupData.class)
    public ResponseEntity getActiveGroups(
            @RequestParam(value = "only-active", required = false, defaultValue = "true") boolean onlyActive
    ) {
        List<StudentGroup> studentGroups = studentGroupService.getAllByActive(onlyActive);
        return ResponseEntity.ok(parseToStudentGroupDTO(studentGroups));
    }

    @GetMapping("/groups/{groupId}/students")
    public ResponseEntity getStudentsByGroupId(@PathVariable Integer groupId) {
        List<StudentDegree> students = this.studentGroupService.getStudentsByGroupId(groupId);
        return ResponseEntity.ok(parseToStudentDegreeFullNameDTO(students));
    }

    private List<StudentDegreeFullNameDTO> parseToStudentDegreeFullNameDTO(List<StudentDegree> students) {
        ModelMapper modelMapper = new ModelMapper();
        Type listType = new TypeToken<List<StudentDegreeFullNameDTO>>() {}.getType();
        return modelMapper.map(students, listType);
    }
}
