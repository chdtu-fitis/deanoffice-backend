package ua.edu.chdtu.deanoffice.api.group;

import com.fasterxml.jackson.annotation.JsonView;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.group.dto.StudentGroupDTO;
import ua.edu.chdtu.deanoffice.api.group.dto.StudentGroupView;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.service.GroupService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;
import ua.edu.chdtu.deanoffice.service.document.diploma.supplement.GraduateService;

import java.lang.reflect.Type;
import java.util.List;

@RestController
@RequestMapping("/groups")
public class GroupController {
    private GraduateService graduateService;
    private GroupService groupService;
    private StudentGroupService studentGroupService;

    @Autowired
    public GroupController(
            GraduateService graduateService,
            GroupService groupService,
            StudentGroupService studentGroupService
    ) {
        this.studentGroupService = studentGroupService;
        this.graduateService = graduateService;
        this.groupService = groupService;
    }

    @JsonView(StudentGroupView.WithStudents.class)
    @GetMapping("/graduates")
    public ResponseEntity getGraduateGroups(@RequestParam Integer degreeId) {
        List<StudentGroup> groups = graduateService.getGraduateGroups(degreeId);
        return ResponseEntity.ok(parseToStudentGroupDTO(groups));
    }

    private List<StudentGroupDTO> parseToStudentGroupDTO(List<StudentGroup> studentGroupList) {
        ModelMapper modelMapper = new ModelMapper();
        Type listType = new TypeToken<List<StudentGroupDTO>>() {}.getType();
        return modelMapper.map(studentGroupList, listType);
    }

    @GetMapping()
    @JsonView(StudentGroupView.GroupData.class)
    public ResponseEntity getGroups() {
        List<StudentGroup> studentGroups = groupService.getGroups();
        return ResponseEntity.ok(parseToStudentGroupDTO(studentGroups));
    }

    @GetMapping("/year")
    @JsonView(StudentGroupView.WithStudents.class)
    public ResponseEntity getGroupsByDegreeAndYear(
            @RequestParam Integer degreeId,
            @RequestParam Integer year
    ) {
        List<StudentGroup> groups = groupService.getGroupsByDegreeAndYear(degreeId, year);
        return ResponseEntity.ok(parseToStudentGroupDTO(groups));
    }


    @GetMapping("courses/{courseId}/groups")
    @JsonView(StudentGroupView.Basic.class)
    public List<StudentGroupDTO> getGroupsByCourse(@PathVariable String courseId) {
        List<StudentGroup> studentGroups = studentGroupService.getGroupsByCourse(Integer.parseInt(courseId));
        return parseToStudentGroupDTO(studentGroups);
    }
}
