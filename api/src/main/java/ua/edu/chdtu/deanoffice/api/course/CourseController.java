package ua.edu.chdtu.deanoffice.api.course;

import com.fasterxml.jackson.annotation.JsonView;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.group.dto.CourseDTO;
import ua.edu.chdtu.deanoffice.api.group.dto.CourseForGroupDTO;
import ua.edu.chdtu.deanoffice.api.group.dto.GroupDTO;
import ua.edu.chdtu.deanoffice.api.group.dto.GroupViews;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;
import ua.edu.chdtu.deanoffice.service.CourseService;

import java.lang.reflect.Type;
import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {
    @Autowired
    private CourseService courseService;
    @Autowired
    private CourseForGroupService courseForGroupService;

    @RequestMapping("/{courseId}/groups")
    @ResponseBody
    @JsonView(GroupViews.Name.class)
    public List<GroupDTO> getGroupsByCourse(@PathVariable String courseId) {
        List<StudentGroup> studentGroups = courseService.getGroupsByCourse(Integer.parseInt(courseId));
        Type listType = new TypeToken<List<GroupDTO>>() {}.getType();
        ModelMapper modelMapper = new ModelMapper();
        List<GroupDTO> groupDTOs = modelMapper.map(studentGroups, listType);
        return groupDTOs;
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

}
