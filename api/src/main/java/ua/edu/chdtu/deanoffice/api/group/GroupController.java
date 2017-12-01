package ua.edu.chdtu.deanoffice.api.group;

import com.fasterxml.jackson.annotation.JsonView;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.group.dto.GroupViews;
import ua.edu.chdtu.deanoffice.api.group.dto.GroupDTO;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.entity.TestEntity;
import ua.edu.chdtu.deanoffice.test.GroupService;
import ua.edu.chdtu.deanoffice.test.TestService;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/groups")
public class GroupController {

    @Autowired
    private final GroupService groupService;
    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @JsonView(GroupViews.Name.class)
    public List<GroupDTO> getGroups() {
        ModelMapper modelMapper = new ModelMapper();
        List<StudentGroup> studentGroups = groupService.getGroups();
        Type listType = new TypeToken<List<GroupDTO>>() {}.getType();
        List<GroupDTO> groupDTOs = modelMapper.map(studentGroups,  listType);
        return groupDTOs;
    }

    /*@RequestMapping("{id}/courses")
    @ResponseBody
    public TestEntity getCourses(@PathVariable String id) {
        TestEntity test = testService.getTest();
        return test;
    }*/

}
