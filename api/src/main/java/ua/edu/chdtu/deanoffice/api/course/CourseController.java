package ua.edu.chdtu.deanoffice.api.course;

import com.fasterxml.jackson.annotation.JsonView;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.course.dto.CourseDTO;
import ua.edu.chdtu.deanoffice.api.course.dto.CourseForGroupDTO;
import ua.edu.chdtu.deanoffice.api.course.dto.CourseForGroupView;
import ua.edu.chdtu.deanoffice.api.group.dto.GroupViews;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;
import ua.edu.chdtu.deanoffice.service.CourseService;

import javax.websocket.server.PathParam;
import java.lang.reflect.Type;
import java.util.List;

@RequestMapping("/")
@RestController
public class CourseController {
    private CourseForGroupService courseForGroupService;
    private CourseService courseService;

    @Autowired
    public CourseController(CourseForGroupService courseForGroupService, CourseService courseService) {
        this.courseForGroupService = courseForGroupService;
        this.courseService = courseService;
    }

    @GetMapping("/courses")
    @ResponseBody
    @JsonView(GroupViews.Name.class)
    public List<CourseDTO> getCoursesBySemester(@PathParam("semester") String semester) {
        List<Course> courses = courseService.getCoursesBySemester(Integer.parseInt(semester));
        Type listType = new TypeToken<List<CourseDTO>>() {}.getType();
        ModelMapper modelMapper = new ModelMapper();
        List<CourseDTO> coursesDTOS = modelMapper.map(courses, listType);
        return coursesDTOS;
    }
  
    private List<CourseForGroupDTO> parseToCourseForGroupDTO(List<CourseForGroup> courseForGroupList) {
        Type listType = new TypeToken<List<CourseForGroupDTO>>() {}.getType();
        return new ModelMapper().map(courseForGroupList, listType);
    }

    @GetMapping("/groups/{groupId}/courses")
    @JsonView(CourseForGroupView.Basic.class)
    public ResponseEntity getCoursesByGroupAndSemester(@PathVariable String groupId, @RequestParam Integer semester) {
        List<CourseForGroup> coursesForGroup = courseForGroupService.getCoursesForGroupBySemester(Integer.parseInt(groupId), semester);
        return ResponseEntity.ok(parseToCourseForGroupDTO(coursesForGroup));
    }


    @GetMapping("/groups/{groupId}/courses/all")
    @JsonView(CourseForGroupView.Course.class)
    public ResponseEntity getCourses(@PathVariable String groupId) {
        List<CourseForGroup> courseForGroups = courseForGroupService.getCourseForGroup(Integer.parseInt(groupId));
        return ResponseEntity.ok(parseToCourseForGroupDTO(courseForGroups));
    }

    @GetMapping("/specialization/{id}/courses")
    @JsonView(CourseForGroupView.Basic.class)
    public ResponseEntity getCoursesBySpecialization(@PathVariable String id, @RequestParam("semester") String semester) {
        List<CourseForGroup> courseForGroups = courseForGroupService.getCourseForGroupBySpecialization(Integer.parseInt(id), Integer.parseInt(semester));
        return ResponseEntity.ok(parseToCourseForGroupDTO(courseForGroups));
    }
}
