package ua.edu.chdtu.deanoffice.api.course.selective;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.course.selective.dto.SelectiveCourseWriteDTO;
import ua.edu.chdtu.deanoffice.api.general.mapper.Mapper;
import ua.edu.chdtu.deanoffice.api.course.selective.dto.SelectiveCourseDTO;
import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.service.DegreeService;
import ua.edu.chdtu.deanoffice.service.DepartmentService;
import ua.edu.chdtu.deanoffice.service.TeacherService;
import ua.edu.chdtu.deanoffice.service.course.CourseService;
import ua.edu.chdtu.deanoffice.service.course.selective.FieldOfKnowledgeService;
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
    private DegreeService degreeService;
    private DepartmentService departmentService;
    private FieldOfKnowledgeService fieldOfKnowledgeService;

    public SelectiveCourseController(SelectiveCourseService selectiveCourseService,
                                     TeacherService teacherService, FieldOfKnowledgeService fieldOfKnowledgeService,
                                     CourseService courseService, DegreeService degreeService, DepartmentService departmentService) {
        this.selectiveCourseService = selectiveCourseService;
        this.teacherService = teacherService;
        this.courseService = courseService;
        this.degreeService = degreeService;
        this.departmentService = departmentService;
        this.fieldOfKnowledgeService = fieldOfKnowledgeService;
    }

    @GetMapping
    public ResponseEntity getAvailableSelectiveCoursesByStudyYearAndDegreeAndSemester(@RequestParam(required = false) Integer studyYear,
                                                                                      @RequestParam int degreeId,
                                                                                      @RequestParam int semester) {
        List<SelectiveCourse> selectiveCourses = selectiveCourseService.getSelectiveCoursesByStudyYearAndDegreeAndSemester(studyYear, degreeId, semester);
        return ResponseEntity.ok(map(selectiveCourses, SelectiveCourseDTO.class));
    }

    @Secured({"ROLE_NAVCH_METHOD"})
    @PostMapping
    public ResponseEntity createSelectiveCourse(@Validated @RequestBody SelectiveCourseWriteDTO selectiveCourseWriteDTO) throws OperationCannotBePerformedException {
        SelectiveCourse selectiveCourse = Mapper.strictMap(selectiveCourseWriteDTO, SelectiveCourse.class);
        if (selectiveCourseWriteDTO.getTeacher() != null) {
            Teacher teacher = teacherService.getTeacher(selectiveCourseWriteDTO.getTeacher().getId());
            if (teacher == null) {
                throw new OperationCannotBePerformedException("Неправильний ідентифікатор викладача");
            }
            selectiveCourse.setTeacher(teacher);
        }

        Course course = courseService.getById(selectiveCourseWriteDTO.getCourse().getId());
        if (course == null) {
            throw new OperationCannotBePerformedException("Неправильний ідентифікатор предмету");
        }
        selectiveCourse.setCourse(course);

        Degree degree = degreeService.getById(selectiveCourseWriteDTO.getDegree().getId());
        if (degree == null) {
            throw new OperationCannotBePerformedException("Неправильний ідентифікатор ступеню");
        }
        selectiveCourse.setDegree(degree);

        Department department = departmentService.getById(selectiveCourseWriteDTO.getDepartment().getId());
        if (department == null) {
            throw new OperationCannotBePerformedException("Неправильний ідентифікатор кафедри");
        }
        selectiveCourse.setDepartment(department);

        FieldOfKnowledge fieldOfKnowledge = fieldOfKnowledgeService.getFieldOfKnowledgeById(selectiveCourseWriteDTO.getBasicFieldOfKnowledge().getId());
        if (fieldOfKnowledge == null) {
            throw new OperationCannotBePerformedException("Неправильний ідентифікатор галузі знань");
        }
        selectiveCourse.setBasicFieldOfKnowledge(fieldOfKnowledge);

        SelectiveCourse selectiveCourseAfterSave = selectiveCourseService.create(selectiveCourse);
        SelectiveCourseDTO selectiveCourseAfterSaveDTO = map(selectiveCourseAfterSave, SelectiveCourseDTO.class);
        return new ResponseEntity(selectiveCourseAfterSaveDTO, HttpStatus.CREATED);
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
                                                @Validated @RequestBody SelectiveCourseWriteDTO selectiveCourseWriteDTO)
            throws OperationCannotBePerformedException {
        SelectiveCourse selectiveCourse = selectiveCourseService.getById(id);
        selectiveCourse = mapSelectiveCourseForUpdate(selectiveCourse, selectiveCourseWriteDTO);
        SelectiveCourse selectiveCourseAfterSave = selectiveCourseService.update(selectiveCourse);
        SelectiveCourseDTO selectiveCourseSavedDTO = Mapper.strictMap(selectiveCourseAfterSave, SelectiveCourseDTO.class);
        return new ResponseEntity(selectiveCourseSavedDTO, HttpStatus.OK);
    }

    private SelectiveCourse mapSelectiveCourseForUpdate(SelectiveCourse selectiveCourse, SelectiveCourseWriteDTO selectiveCourseWriteDTO)
            throws OperationCannotBePerformedException {
        if (selectiveCourse.getTeacher() != null || selectiveCourseWriteDTO.getTeacher() != null) {
            if (selectiveCourse.getTeacher() == null && selectiveCourseWriteDTO.getTeacher() != null) {
                Teacher teacher = teacherService.getTeacher(selectiveCourseWriteDTO.getTeacher().getId());
                if (teacher == null) {
                    throw new OperationCannotBePerformedException("Неправильний ідентифікатор викладача");
                }
                selectiveCourse.setTeacher(teacher);
            } else {
                if (selectiveCourse.getTeacher() != null && selectiveCourseWriteDTO.getTeacher() == null) {
                    selectiveCourse.setTeacher(null);
                } else {
                    if (selectiveCourse.getTeacher().getId() != selectiveCourseWriteDTO.getTeacher().getId()) {
                        Teacher teacher = teacherService.getTeacher(selectiveCourseWriteDTO.getTeacher().getId());
                        if (teacher == null) {
                            throw new OperationCannotBePerformedException("Неправильний ідентифікатор викладача");
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
        if (selectiveCourse.getDegree().getId() != selectiveCourseWriteDTO.getDegree().getId()) {
            Degree degree = degreeService.getById(selectiveCourseWriteDTO.getDegree().getId());
            if (degree == null) {
                throw new OperationCannotBePerformedException("Неправильний ідентифікатор ступеню");
            }
            selectiveCourse.setDegree(degree);
        }
        if (selectiveCourse.getDepartment().getId() != selectiveCourseWriteDTO.getDepartment().getId()) {
            Department department = departmentService.getById(selectiveCourseWriteDTO.getDepartment().getId());
            if (department == null) {
                throw new OperationCannotBePerformedException("Неправильний ідентифікатор кафедри");
            }
            selectiveCourse.setDepartment(department);
        }
        if (selectiveCourse.getBasicFieldOfKnowledge().getId() != selectiveCourseWriteDTO.getBasicFieldOfKnowledge().getId()) {
            FieldOfKnowledge fieldOfKnowledge = fieldOfKnowledgeService.getFieldOfKnowledgeById(selectiveCourseWriteDTO.getBasicFieldOfKnowledge().getId());
            if (fieldOfKnowledge == null) {
                throw new OperationCannotBePerformedException("Неправильний ідентифікатор галузі знань");
            }
            selectiveCourse.setBasicFieldOfKnowledge(fieldOfKnowledge);
        }
        Mapper.strictMap(selectiveCourseWriteDTO, selectiveCourse);
        return selectiveCourse;
    }
}
