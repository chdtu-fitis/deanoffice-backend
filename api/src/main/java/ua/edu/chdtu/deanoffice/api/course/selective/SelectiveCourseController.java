package ua.edu.chdtu.deanoffice.api.course.selective;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.course.selective.dto.SelectiveCourseWriteDTO;
import ua.edu.chdtu.deanoffice.api.general.mapper.Mapper;
import ua.edu.chdtu.deanoffice.api.course.selective.dto.SelectiveCourseDTO;
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

    @Secured({"ROLE_NAVCH_METHOD"})
    @PostMapping("selective-courses/create")
    public ResponseEntity createSelectiveCourse(@Validated @RequestBody SelectiveCourseWriteDTO selectiveCourseWriteDTO) {
        SelectiveCourse selectiveCourse = Mapper.strictMap(selectiveCourseWriteDTO, SelectiveCourse.class);
        SelectiveCourse selectiveCourseAfterSave = selectiveCourseService.create(selectiveCourse);
        SelectiveCourseDTO selectiveCourseAfterSaveDTO = map(selectiveCourseAfterSave, SelectiveCourseDTO.class);
        return new ResponseEntity(selectiveCourseAfterSaveDTO, HttpStatus.CREATED);
    }

    @Secured({"ROLE_NAVCH_METHOD"})
    @DeleteMapping("/selective-courses/{id}")
    public ResponseEntity deleteSelectiveCourse(@PathVariable("id") int id) {
        SelectiveCourse selectiveCourse = selectiveCourseService.getSelectiveCourseById(id);
        selectiveCourseService.delete(selectiveCourse);
        return ResponseEntity.ok().build();
    }

    @Secured({"ROLE_NAVCH_METHOD"})
    @PutMapping("/selective-courses/{id}/restore")
    public ResponseEntity restoreSelectiveCourse(@PathVariable("id") int id) {
        SelectiveCourse selectiveCourse = selectiveCourseService.getSelectiveCourseById(id);
        selectiveCourseService.restore(selectiveCourse);
        return ResponseEntity.ok().build();
    }
}
