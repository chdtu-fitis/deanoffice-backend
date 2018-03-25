package ua.edu.chdtu.deanoffice.api.course;

import com.fasterxml.jackson.annotation.JsonView;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.course.dto.CourseForGroupDTO;
import ua.edu.chdtu.deanoffice.api.course.dto.CourseForGroupView;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;

import java.lang.reflect.Type;
import java.util.List;

@RestController
public class CourseForGroupController {
    private StudentGroupService groupService;
    private CourseForGroupService courseForGroupService;

    @Autowired
    public CourseForGroupController(
            StudentGroupService groupService,
            CourseForGroupService courseForGroupService
    ) {
        this.groupService = groupService;
        this.courseForGroupService = courseForGroupService;
    }
/*
    @RequestMapping("courses"/{courseId}/groups")
    @ResponseBody
    @JsonView(GroupViews.Name.class)
    public List<GroupDTO> getGroupsByCourse(@PathVariable String courseId) {
        List<StudentGroup> studentGroups = groupService.getGroupsByCourse(Integer.parseInt(courseId));
        Type listType = new TypeToken<List<GroupDTO>>() {
        }.getType();
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(studentGroups, listType);
    }

    @RequestMapping("courses"/{semester}")
    @ResponseBody
    @JsonView(GroupViews.Name.class)
    public List<CourseForGroupDTO> getCoursesBySemester(@PathVariable String semester) {
        List<CourseForGroup> courseForGroups = courseForGroupService.getCoursesForGroupBySemester(Integer.parseInt(semester));
        Type listType = new TypeToken<List<CourseForGroupDTO>>() {
        }.getType();
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(courseForGroups, listType);
    }

    @RequestMapping("courses"/groups/{groupId}")
    @ResponseBody
    @JsonView(GroupViews.Name.class)
    public List<CourseForGroupDTO> getCoursesByGroupAndSemester(@PathVariable String groupId, @RequestParam Integer semester) {
        List<CourseForGroup> coursesForGroup = courseForGroupService.getCoursesForGroupBySemester(Integer.parseInt(groupId), semester);
        Type listType = new TypeToken<List<CourseForGroupDTO>>() {
        }.getType();
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(coursesForGroup, listType);
    }*/


    @GetMapping("/groups/{id}")
    @JsonView(CourseForGroupView.Course.class)
    public ResponseEntity getCourses(@PathVariable String id) {
        List<CourseForGroup> courseForGroups = courseForGroupService.getCourseForGroup(Integer.parseInt(id));
        return ResponseEntity.ok(parseToCourseForGroupDTO(courseForGroups));
    }

    private List<CourseForGroupDTO> parseToCourseForGroupDTO(List<CourseForGroup> courseForGroupList) {
        Type listType = new TypeToken<List<CourseForGroupDTO>>() {}.getType();
        return new ModelMapper().map(courseForGroupList, listType);
    }

    @GetMapping("/groups/{id}/{semester}/courses")
    @JsonView(CourseForGroupView.Basic.class)
    public ResponseEntity getCoursesBySemester(@PathVariable String id, @PathVariable String semester) {
        List<CourseForGroup> courseForGroups = courseForGroupService.getCoursesForGroupBySemester(Integer.parseInt(id), Integer.parseInt(semester));
        return ResponseEntity.ok(parseToCourseForGroupDTO(courseForGroups));
    }

    @GetMapping("specialization/{id}/courses")
    @JsonView(CourseForGroupView.Basic.class)
    public List<CourseForGroupDTO> getCoursesBySpecialization(@PathVariable String id, @RequestParam("semester") String semester) {
        List<CourseForGroup> courseForGroups = courseForGroupService.getCourseForGroupBySpecialization(Integer.parseInt(id), Integer.parseInt(semester));
        Type listType = new TypeToken<List<CourseForGroupDTO>>() {
        }.getType();
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(courseForGroups, listType);
    }
}
