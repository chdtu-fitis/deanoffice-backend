package ua.edu.chdtu.deanoffice.api.courseForGroup;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.group.dto.CourseForGroupDTO;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping("/coursesForGroup")
public class CourseForGroupController {
    @Autowired
    private CourseForGroupService courseForGroupService;

    @PostMapping(value = "/{studentGroupId}/coursesForGroup")
    public ResponseEntity.BodyBuilder addCoursesForGroup(@RequestBody List<CourseForGroup> newCourses, @RequestBody List<CourseForGroup> mutableCourses, @RequestBody List<String> deleteCoursesIdList, @PathVariable String studentGroupId) {
        if (newCourses != null || mutableCourses !=null || deleteCoursesIdList != null){
            courseForGroupService.addCourseForGroupAndNewChanges(newCourses, mutableCourses, deleteCoursesIdList, Integer.parseInt(studentGroupId));
            return ResponseEntity.ok();
        }
        else {
            return ResponseEntity.badRequest();
        }

    }
}
