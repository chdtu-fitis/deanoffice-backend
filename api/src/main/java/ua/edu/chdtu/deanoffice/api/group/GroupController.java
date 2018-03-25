package ua.edu.chdtu.deanoffice.api.group;

import com.fasterxml.jackson.annotation.JsonView;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.group.dto.*;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;
import ua.edu.chdtu.deanoffice.service.GroupService;
import ua.edu.chdtu.deanoffice.service.document.diploma.supplement.GraduateService;

import java.lang.reflect.Type;
import java.util.List;

@RestController
@RequestMapping("/groups")
public class GroupController {
    private GraduateService graduateService;
    private GroupService groupService;
    private CourseForGroupService courseForGroupService;

    @Autowired
    public GroupController(
            GraduateService graduateService,
            GroupService groupService,
            CourseForGroupService courseForGroupService
    ) {
        this.graduateService = graduateService;
        this.groupService = groupService;
        this.courseForGroupService = courseForGroupService;
    }

    @JsonView(StudentGroupView.WithStudents.class)
    @GetMapping("/graduates")
    public ResponseEntity getGraduateGroups(@RequestParam Integer degreeId) {
        List<StudentGroup> groups = graduateService.getGraduateGroups(degreeId);
        return ResponseEntity.ok(parseToStudentGroupDTO(groups));
    }

    private List<GroupWithStudentsDTO> parseToStudentGroupDTO(List<StudentGroup> studentGroupList) {
        ModelMapper modelMapper = new ModelMapper();
        Type listType = new TypeToken<List<StudentGroupDTO>>() {}.getType();
        return modelMapper.map(studentGroupList, listType);
    }

    private List<GroupWithStudentsDTO> parseToGroupWithStudentsDTO(List<StudentGroup> studentGroupList) {
        ModelMapper modelMapper = new ModelMapper();
        Type listType = new TypeToken<List<GroupWithStudentsDTO>>() {}.getType();
        return modelMapper.map(studentGroupList, listType);
    }

    @GetMapping()
    @JsonView(StudentGroupView.BasicGroupData.class)
    public ResponseEntity getGroups() {
        List<StudentGroup> studentGroups = groupService.getGroups();
        return ResponseEntity.ok(parseToStudentGroupDTO(studentGroups));
    }

    @GetMapping("/year")
    public ResponseEntity getGroupsByDegreeAndYear(
            @RequestParam Integer degreeId,
            @RequestParam Integer year
    ) {
        List<StudentGroup> groups = groupService.getGroupsByDegreeAndYear(degreeId, year);
        return ResponseEntity.ok(parseToGroupWithStudentsDTO(groups));
    }

    @GetMapping("{id}/courses")
    @JsonView(GroupViews.Course.class)
    public ResponseEntity getCourses(@PathVariable String id) {
        List<CourseForGroup> courseForGroups = courseForGroupService.getCourseForGroup(Integer.parseInt(id));
        return ResponseEntity.ok(parseToCourseForGroupDTO(courseForGroups));
    }

    private List<CourseForGroupDTO> parseToCourseForGroupDTO(List<CourseForGroup> courseForGroupList) {
        Type listType = new TypeToken<List<CourseForGroupDTO>>() {}.getType();
        return new ModelMapper().map(courseForGroupList, listType);
    }

    @GetMapping("{id}/{semester}/courses")
    @JsonView(GroupViews.Name.class)
    public ResponseEntity getCoursesBySemester(@PathVariable String id, @PathVariable String semester) {
        List<CourseForGroup> courseForGroups = courseForGroupService.getCoursesForGroupBySemester(Integer.parseInt(id), Integer.parseInt(semester));
        return ResponseEntity.ok(parseToCourseForGroupDTO(courseForGroups));
    }
}
