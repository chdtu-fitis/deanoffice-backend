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
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.SelectiveCourse;
import ua.edu.chdtu.deanoffice.entity.Teacher;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.service.TeacherService;
import ua.edu.chdtu.deanoffice.service.course.CourseService;
import ua.edu.chdtu.deanoffice.service.course.selective.SelectiveCourseService;

import javax.validation.constraints.Min;
import java.util.List;

import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;

@RestController
@RequestMapping("/selective-courses")
public class SelectiveCourseController {

    private SelectiveCourseService selectiveCourseService;
    private TeacherService teacherService;
    private CourseService courseService;

    @Autowired
    public SelectiveCourseController(SelectiveCourseService selectiveCourseService, TeacherService teacherService, CourseService courseService) {
        this.selectiveCourseService = selectiveCourseService;
        this.teacherService = teacherService;
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity getAllAvailableSelectiveCourses(@RequestParam(required = false) Integer studyYear) {
        List<SelectiveCourse> selectiveCourses = selectiveCourseService.getSelectiveCoursesInCurrentYear(studyYear);
        return ResponseEntity.ok(map(selectiveCourses, SelectiveCourseDTO.class));
    }

    @Secured({"ROLE_NAVCH_METHOD"})
    @PostMapping
    public ResponseEntity createSelectiveCourse(@Validated @RequestBody SelectiveCourseWriteDTO selectiveCourseWriteDTO) throws OperationCannotBePerformedException {
        SelectiveCourse selectiveCourse = Mapper.strictMap(selectiveCourseWriteDTO, SelectiveCourse.class);
        Teacher teacher = teacherService.getTeacher(selectiveCourseWriteDTO.getTeacher().getId());
        if (selectiveCourse.getTeacher() != null || selectiveCourseWriteDTO.getTeacher() != null) {
            if (selectiveCourse.getTeacher() == null && selectiveCourseWriteDTO.getTeacher() != null) {
                if (teacher == null) {
                    throw new OperationCannotBePerformedException("Неправильний ідентифікатор викладача");
                }
            }
        }
        selectiveCourse.setTeacher(teacher);
        if (selectiveCourse.getCourse().getId() != selectiveCourseWriteDTO.getCourse().getId()) {
            Course course = courseService.getById(selectiveCourseWriteDTO.getCourse().getId());
            if (course == null) {
                throw new OperationCannotBePerformedException("Неправильний ідентифікатор предмету");
            }
            selectiveCourse.setCourse(course);
        }
        SelectiveCourse selectiveCourseAfterSave = selectiveCourseService.create(selectiveCourse);
        SelectiveCourseDTO selectiveCourseAfterSaveDTO = map(selectiveCourseAfterSave, SelectiveCourseDTO.class);
        return ResponseEntity.ok(selectiveCourseAfterSaveDTO);
    }

    @Secured({"ROLE_NAVCH_METHOD"})
    @DeleteMapping("/{id}")
    public ResponseEntity deleteSelectiveCourse(@PathVariable("id") int id) {
        SelectiveCourse selectiveCourse = selectiveCourseService.getById(id);
        selectiveCourseService.delete(selectiveCourse);
        return ResponseEntity.ok().build();
    }

    @Secured({"ROLE_NAVCH_METHOD"})
    @PatchMapping("/{id}")
    public ResponseEntity restoreSelectiveCourse(@PathVariable("id") int id) {
        SelectiveCourse selectiveCourse = selectiveCourseService.getById(id);
        selectiveCourseService.restore(selectiveCourse);
        return ResponseEntity.ok().build();
    }

    @Secured({"ROLE_NAVCH_METHOD"})
    @PutMapping("/{id}")
    public ResponseEntity updateSelectiveCourse(@PathVariable("id") @Min(1) int id,
                                                @Validated @RequestBody SelectiveCourseWriteDTO selectiveCourseWriteDTO) throws
            OperationCannotBePerformedException {
        SelectiveCourse selectiveCourse = selectiveCourseService.getById(id);
        selectiveCourse = mapSelectiveCourseForUpdate(selectiveCourse, selectiveCourseWriteDTO);
        SelectiveCourse selectiveCourseAfterSave = selectiveCourseService.update(selectiveCourse);
        SelectiveCourseDTO selectiveCourseSavedDTO = Mapper.strictMap(selectiveCourseAfterSave, SelectiveCourseDTO.class);
        return new ResponseEntity(selectiveCourseSavedDTO, HttpStatus.OK);
    }

    private SelectiveCourse mapSelectiveCourseForUpdate(SelectiveCourse
                                                                selectiveCourse, SelectiveCourseWriteDTO selectiveCourseWriteDTO) throws OperationCannotBePerformedException {
        Teacher teacher = teacherService.getTeacher(selectiveCourseWriteDTO.getTeacher().getId());
        if (selectiveCourse.getTeacher() != null || selectiveCourseWriteDTO.getTeacher() != null) {
            if (selectiveCourse.getTeacher() == null && selectiveCourseWriteDTO.getTeacher() != null) {
                if (teacher == null) {
                    throw new OperationCannotBePerformedException("Неправильний ідентифікатор предмету");
                }
                selectiveCourse.setTeacher(teacher);
            } else {
                if (selectiveCourse.getTeacher() != null && selectiveCourseWriteDTO.getTeacher() == null) {
                    selectiveCourse.setTeacher(null);
                } else {
                    if (selectiveCourse.getTeacher().getId() != selectiveCourseWriteDTO.getTeacher().getId()) {
                        if (teacher == null) {
                            throw new OperationCannotBePerformedException("Неправильний ідентифікатор предмету");
                        }
                        selectiveCourse.setTeacher(teacher);
                    }
                }
            }
        }
        if (selectiveCourse.getCourse().getId() != selectiveCourseWriteDTO.getCourse().getId()) {
            Course course = courseService.getById(selectiveCourseWriteDTO.getCourse().getId());
            if (course == null) {
                throw new OperationCannotBePerformedException("Неправильний ідентифікатор предмету");
            }
            selectiveCourse.setCourse(course);
        }
        Mapper.strictMap(selectiveCourseWriteDTO, selectiveCourse);
        return selectiveCourse;
    }
}
