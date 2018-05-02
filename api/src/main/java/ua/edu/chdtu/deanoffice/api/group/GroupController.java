package ua.edu.chdtu.deanoffice.api.group;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.general.dto.NamedDTO;
import ua.edu.chdtu.deanoffice.api.group.dto.StudentGroupDTO;
import ua.edu.chdtu.deanoffice.api.group.dto.StudentGroupShortDTO;
import ua.edu.chdtu.deanoffice.api.group.dto.StudentGroupView;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;

import java.util.List;

import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;

@RestController
public class GroupController {
    private StudentGroupService studentGroupService;

    @Autowired
    public GroupController(StudentGroupService studentGroupService) {
        this.studentGroupService = studentGroupService;
    }

    @JsonView(StudentGroupView.WithStudents.class)
    @GetMapping("/groups/graduates")
    public ResponseEntity getGraduateGroups(@RequestParam int degreeId) {
        List<StudentGroup> groups = studentGroupService.getGraduateGroups(degreeId);
        return ResponseEntity.ok(map(groups, StudentGroupShortDTO.class));
    }

    @GetMapping("/groups/filter")
    @JsonView(StudentGroupView.WithStudents.class)
    public ResponseEntity getGroupsByDegreeAndYear(
            @RequestParam Integer degreeId,
            @RequestParam Integer year
    ) {
        List<StudentGroup> groups = studentGroupService.getGroupsByDegreeAndYear(degreeId, year);
        return ResponseEntity.ok(map(groups, StudentGroupDTO.class));
    }

    @GetMapping("courses/{courseId}/groups")
    public ResponseEntity getGroupsByCourse(@PathVariable int courseId) {
        List<StudentGroup> studentGroups = studentGroupService.getGroupsByCourse(courseId);
        return ResponseEntity.ok(map(studentGroups, NamedDTO.class));
    }

    @GetMapping("/groups")
    @JsonView(StudentGroupView.AllGroupData.class)
    public ResponseEntity getActiveGroups(
            @RequestParam(value = "only-active", required = false, defaultValue = "true") boolean onlyActive
    ) {
        List<StudentGroup> studentGroups = studentGroupService.getAllByActive(onlyActive);
        return ResponseEntity.ok(map(studentGroups, StudentGroupDTO.class));
    }
}
