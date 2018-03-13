package ua.edu.chdtu.deanoffice.api.specialization;

import com.fasterxml.jackson.annotation.JsonView;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.group.dto.CourseForGroupDTO;
import ua.edu.chdtu.deanoffice.api.group.dto.GroupViews;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;

import java.lang.reflect.Type;
import java.util.List;

@RestController
@RequestMapping("/specialization")
public class SpecializationController {
    @Autowired
    private CourseForGroupService courseForGroupService;

    @GetMapping("/{id}/courses")
    @JsonView(GroupViews.Name.class)
    public List<CourseForGroupDTO> getCoursesBySpecialization(@PathVariable String id, @RequestParam("semester") String semester){
        List<CourseForGroup> courseForGroups = courseForGroupService.getCourseForGroupBySpecialization(Integer.parseInt(id), Integer.parseInt(semester));
        Type listType = new TypeToken<List<CourseForGroupDTO>>() {}.getType();
        ModelMapper modelMapper = new ModelMapper();
        List<CourseForGroupDTO> courseForGroupDTOS = modelMapper.map(courseForGroups, listType);
        return courseForGroupDTOS;
    }
}
