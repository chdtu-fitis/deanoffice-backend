package ua.edu.chdtu.deanoffice.api.course;

import com.fasterxml.jackson.annotation.JsonView;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.course.dto.CourseForGroupDTO;
import ua.edu.chdtu.deanoffice.api.course.dto.CourseForGroupView;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;

import java.lang.reflect.Type;
import java.util.List;

@RequestMapping("/")
@RestController
public class CourseController {
    private CourseForGroupService courseForGroupService;

    @Autowired
    public CourseController(CourseForGroupService courseForGroupService) {
        this.courseForGroupService = courseForGroupService;
    }

    @GetMapping("/courses")
    @JsonView(CourseForGroupView.Basic.class)
    public ResponseEntity getCoursesBySemester(@RequestParam String semester) {
        List<CourseForGroup> courseForGroups = courseForGroupService.getCoursesForGroupBySemester(Integer.parseInt(semester));
        return ResponseEntity.ok(parseToCourseForGroupDTO(courseForGroups));
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
