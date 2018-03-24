package ua.edu.chdtu.deanoffice.api.course;

import com.fasterxml.jackson.annotation.JsonView;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.group.dto.CourseDTO;
import ua.edu.chdtu.deanoffice.api.group.dto.CourseForGroupDTO;
import ua.edu.chdtu.deanoffice.api.group.dto.GroupDTO;
import ua.edu.chdtu.deanoffice.api.group.dto.GroupViews;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;
import ua.edu.chdtu.deanoffice.service.CourseService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;

import java.lang.reflect.Type;
import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {
    private StudentGroupService groupService;
    private CourseForGroupService courseForGroupService;
    private CourseService courseService;

    @Autowired
    public CourseController(StudentGroupService groupService, CourseForGroupService courseForGroupService, CourseService courseService) {
        this.groupService = groupService;
        this.courseForGroupService = courseForGroupService;
        this.courseService = courseService;
    }

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
    public List<CourseDTO> getCoursesBySemester(@PathVariable String semester) {
        List<Course> courses = courseService.getCoursesBySemester(Integer.parseInt(semester));
        Type listType = new TypeToken<List<CourseDTO>>() {}.getType();
        ModelMapper modelMapper = new ModelMapper();
        List<CourseDTO> coursesDTOS = modelMapper.map(courses, listType);
        return coursesDTOS;
    }

    @PostMapping
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

    @RequestMapping("/groups/{groupId}")
    @ResponseBody
    @JsonView(GroupViews.Name.class)
    public List<CourseForGroupDTO> getCoursesByGroupAndSemester(@PathVariable String groupId, @RequestParam Integer semester) {
        List<CourseForGroup> coursesForGroup = courseForGroupService.getCoursesForGroupBySemester(Integer.parseInt(groupId), semester);
        Type listType = new TypeToken<List<CourseForGroupDTO>>() {
        }.getType();
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(coursesForGroup, listType);
    }
}
