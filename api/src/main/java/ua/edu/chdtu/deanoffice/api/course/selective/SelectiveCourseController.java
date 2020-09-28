package ua.edu.chdtu.deanoffice.api.course.selective;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.course.selective.dto.SelectiveCourseWriteDTO;
import ua.edu.chdtu.deanoffice.api.general.mapper.Mapper;
import ua.edu.chdtu.deanoffice.api.course.selective.dto.SelectiveCourseDTO;
import ua.edu.chdtu.deanoffice.entity.SelectiveCourse;
import ua.edu.chdtu.deanoffice.service.selective.courses.SelectiveCourseService;
import javax.validation.constraints.Min;
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
    @PostMapping("/create")
    public ResponseEntity createSelectiveCourse(@Validated @RequestBody SelectiveCourseWriteDTO selectiveCourseWriteDTO) {
        SelectiveCourse selectiveCourse = Mapper.strictMap(selectiveCourseWriteDTO, SelectiveCourse.class);
        SelectiveCourse selectiveCourseAfterSave = selectiveCourseService.create(selectiveCourse);
        SelectiveCourseDTO selectiveCourseAfterSaveDTO = map(selectiveCourseAfterSave, SelectiveCourseDTO.class);
        return new ResponseEntity(selectiveCourseAfterSaveDTO, HttpStatus.CREATED);
    }

    @Secured({"ROLE_NAVCH_METHOD"})
    @DeleteMapping("/{id}")
    public ResponseEntity deleteSelectiveCourse(@PathVariable("id") int id) {
        SelectiveCourse selectiveCourse = selectiveCourseService.getSelectiveCourseById(id);
        selectiveCourseService.delete(selectiveCourse);
        return ResponseEntity.ok().build();
    }

    @Secured({"ROLE_NAVCH_METHOD"})
    @PutMapping("/{id}/restore")
    public ResponseEntity restoreSelectiveCourse(@PathVariable("id") int id) {
        SelectiveCourse selectiveCourse = selectiveCourseService.getSelectiveCourseById(id);
        selectiveCourseService.restore(selectiveCourse);
        return ResponseEntity.ok().build();
    }
    @Secured({"ROLE_NAVCH_METHOD"})
    @PutMapping("/{id}/")
    public ResponseEntity updateSelectiveCourse(@PathVariable("id") @Min(1) int id,
                                                @Validated @RequestBody SelectiveCourseWriteDTO selectiveCourseWriteDTO) {
        SelectiveCourse selectiveCourse = Mapper.strictMap(selectiveCourseWriteDTO, SelectiveCourse.class);
        selectiveCourse.setId(id);
        SelectiveCourse selectiveCourseAfterSave = selectiveCourseService.save(selectiveCourse);
        SelectiveCourseDTO selectiveCourseSavedDTO = Mapper.strictMap(selectiveCourseAfterSave, SelectiveCourseDTO.class);
        return new ResponseEntity(selectiveCourseSavedDTO, HttpStatus.CREATED);
    }
}
