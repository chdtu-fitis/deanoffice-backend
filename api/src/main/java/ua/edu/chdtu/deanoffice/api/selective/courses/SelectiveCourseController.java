package ua.edu.chdtu.deanoffice.api.selective.courses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.selective.courses.dto.SelectiveCourseDTO;
import ua.edu.chdtu.deanoffice.entity.SelectiveCourse;
import ua.edu.chdtu.deanoffice.service.selective.courses.SelectiveCourseService;
import java.util.List;
import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;

@RestController
@RequestMapping("/selective-courses")
public class SelectiveCourseController {

    private SelectiveCourseService selectiveCourseService;

    @Autowired
    public SelectiveCourseController(SelectiveCourseService selectiveCourseService) {
        this.selectiveCourseService = selectiveCourseService;
    }

    @GetMapping
    public ResponseEntity getAllAvailableSelectiveCourses(@RequestParam(required = false) Integer studyYear) {
        List<SelectiveCourse> selectiveCourses = selectiveCourseService.getSelectiveCoursesInCurrentYear(studyYear);
        return ResponseEntity.ok(map(selectiveCourses, SelectiveCourseDTO.class));
    }
}
