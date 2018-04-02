package ua.edu.chdtu.deanoffice.api.course;

import com.fasterxml.jackson.annotation.JsonView;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.course.dto.CourseDTO;
import ua.edu.chdtu.deanoffice.api.course.dto.CourseForGroupDTO;
import ua.edu.chdtu.deanoffice.api.course.dto.CourseForGroupView;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;
import ua.edu.chdtu.deanoffice.service.CourseService;

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
    public ResponseEntity getCoursesBySemester(@RequestParam(value = "semester") int semester) {
        List<Course> courses = courseService.getCoursesBySemester(semester);
        return ResponseEntity.ok(parseToCourseDTO(courses));
    }

    private List<CourseDTO> parseToCourseDTO(List<Course> courses) {
        Type listType = new TypeToken<List<CourseDTO>>() {}.getType();
        return new ModelMapper().map(courses, listType);
    }

    @GetMapping("/groups/{groupId}/courses")
    @JsonView(CourseForGroupView.Basic.class)
    public ResponseEntity getCoursesByGroupAndSemester(@PathVariable int groupId, @RequestParam int semester) {
        List<CourseForGroup> coursesForGroup = courseForGroupService.getCoursesForGroupBySemester(groupId, semester);
        return ResponseEntity.ok(parseToCourseForGroupDTO(coursesForGroup));
    }

    private List<CourseForGroupDTO> parseToCourseForGroupDTO(List<CourseForGroup> courseForGroupList) {
        Type listType = new TypeToken<List<CourseForGroupDTO>>() {}.getType();
        return new ModelMapper().map(courseForGroupList, listType);
    }

    @GetMapping("/groups/{groupId}/courses/all")
    @JsonView(CourseForGroupView.Course.class)
    public ResponseEntity getCourses(@PathVariable int groupId) {
        List<CourseForGroup> courseForGroups = courseForGroupService.getCourseForGroup(groupId);
        return ResponseEntity.ok(parseToCourseForGroupDTO(courseForGroups));
    }

    @GetMapping("/specialization/{id}/courses")
    @JsonView(CourseForGroupView.Basic.class)
    public ResponseEntity getCoursesBySpecialization(@PathVariable int id, @RequestParam("semester") int semester) {
        List<CourseForGroup> courseForGroups = courseForGroupService.getCourseForGroupBySpecialization(id, semester);
        return ResponseEntity.ok(parseToCourseForGroupDTO(courseForGroups));
    }

    @PostMapping("/courses")
    @ResponseBody
    public ResponseEntity createCourse(@RequestBody Course course){
        try {
            this.courseService.createCourse(course);
            return new ResponseEntity(HttpStatus.CREATED);
        }
        catch (DataIntegrityViolationException e){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }
}
