package ua.edu.chdtu.deanoffice.api.courseForGroup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;
import java.util.List;

@RestController
@RequestMapping("/coursesForGroup")
public class CourseForGroupController {
    @Autowired
    private CourseForGroupService courseForGroupService;


}
