package ua.edu.chdtu.deanoffice.api.group;

import com.fasterxml.jackson.annotation.JsonView;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.group.dto.CourseForGroupDTO;
import ua.edu.chdtu.deanoffice.api.group.dto.GroupViews;
import ua.edu.chdtu.deanoffice.api.group.dto.GroupDTO;
import ua.edu.chdtu.deanoffice.api.group.dto.TeacherDTO;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.entity.Teacher;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;
import ua.edu.chdtu.deanoffice.service.GroupService;
import ua.edu.chdtu.deanoffice.service.TeacherService;

import java.lang.reflect.Type;
import java.util.List;

@RestController
@RequestMapping("/groups")
public class GroupController {
    ModelMapper modelMapper;
    @Autowired
    private final GroupService groupService;
    private final CourseForGroupService courseForGroupService;
    private final TeacherService teacherService;


    @Autowired
    public GroupController(GroupService groupService, CourseForGroupService courseForGroupService, TeacherService teacherService) {
        this.groupService = groupService;
        this.courseForGroupService = courseForGroupService;
        this.teacherService = teacherService;
        modelMapper = new ModelMapper();
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @JsonView(GroupViews.Name.class)
    public List<GroupDTO> getGroups() {
        List<StudentGroup> studentGroups = groupService.getGroups();
        Type listType = new TypeToken<List<GroupDTO>>() {}.getType();
        List<GroupDTO> groupDTOs = modelMapper.map(studentGroups,  listType);
        return groupDTOs;
        //Спробуй рухатися зверху в низ
    }

    @RequestMapping("{id}/courses")
    @ResponseBody
    @JsonView(GroupViews.Name.class)
    public List<CourseForGroupDTO> getCourses(@PathVariable String id) {
        List<CourseForGroup> courseForGroups = courseForGroupService.getCourseForGroup(Integer.parseInt(id));
        Type listType = new TypeToken<List<CourseForGroupDTO>>() {}.getType();
        List<CourseForGroupDTO> courseForGroupDTOS = modelMapper.map(courseForGroups,listType);
        return  courseForGroupDTOS;
    }

}
