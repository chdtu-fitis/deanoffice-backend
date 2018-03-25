package ua.edu.chdtu.deanoffice.api.course;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;

@RestController
@RequestMapping("/courses")
public class CourseController {
    private StudentGroupService groupService;
    private CourseForGroupService courseForGroupService;

    public CourseController(StudentGroupService groupService, CourseForGroupService courseForGroupService) {
        this.groupService = groupService;
        this.courseForGroupService = courseForGroupService;
    }
/*
    @RequestMapping("/{courseId}/groups")
    @ResponseBody
    @JsonView(GroupViews.Name.class)
    public List<GroupDTO> getGroupsByCourse(@PathVariable String courseId) {
        List<StudentGroup> studentGroups = groupService.getGroupsByCourse(Integer.parseInt(courseId));
        Type listType = new TypeToken<List<GroupDTO>>() {
        }.getType();
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(studentGroups, listType);
    }

    @RequestMapping("/{semester}")
    @ResponseBody
    @JsonView(GroupViews.Name.class)
    public List<CourseForGroupDTO> getCoursesBySemester(@PathVariable String semester) {
        List<CourseForGroup> courseForGroups = courseForGroupService.getCoursesForGroupBySemester(Integer.parseInt(semester));
        Type listType = new TypeToken<List<CourseForGroupDTO>>() {
        }.getType();
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(courseForGroups, listType);
    }

    @RequestMapping("/groups/{groupId}")
    @ResponseBody
    @JsonView(GroupViews.Name.class)
    public List<CourseForGroupDTO> getCoursesByGroupAndSemester(@PathVariable String groupId, @RequestParam Integer semester) {
        List<CourseForGroup> coursesForGroup = courseForGroupService.getCoursesForGroupBySemester(Integer.parseInt(groupId), semester);
        Type listType = new TypeToken<List<CourseForGroupDTO>>() {
        }.getType();
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(coursesForGroup, listType);
    }*/
}
