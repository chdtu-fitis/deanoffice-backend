package ua.edu.chdtu.deanoffice.api.course.selective;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.course.selective.dto.SelectiveCourseDTO;
import ua.edu.chdtu.deanoffice.api.course.selective.dto.SelectiveCourseWriteDTO;
import ua.edu.chdtu.deanoffice.api.general.dto.NamedDTO;
import ua.edu.chdtu.deanoffice.api.general.mapper.Mapper;
import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.service.DegreeService;
import ua.edu.chdtu.deanoffice.service.DepartmentService;
import ua.edu.chdtu.deanoffice.service.TeacherService;
import ua.edu.chdtu.deanoffice.service.course.CourseService;
import ua.edu.chdtu.deanoffice.service.course.selective.FieldOfKnowledgeService;
import ua.edu.chdtu.deanoffice.service.course.selective.SelectiveCourseService;
import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
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
        List<SelectiveCourseDTO> selectiveCourseDTOS = new ArrayList<>();
        List<SelectiveCourse> selectiveCourses = selectiveCourseService.getSelectiveCoursesByStudyYearAndDegreeAndSemester(studyYear, degreeId, semester);
        for (SelectiveCourse selectiveCourse : selectiveCourses) {
            SelectiveCourseDTO selectiveCourseDTO = map(selectiveCourse, SelectiveCourseDTO.class);
            if (selectiveCourse.getFieldOfKnowledge() != null) {
                List<FieldOfKnowledge> fieldsOfKnowledge = new ArrayList<>();
                fieldsOfKnowledge.add(selectiveCourse.getFieldOfKnowledge());
                if (selectiveCourse.getOtherFieldsOfKnowledge() != null) {
                    String[] ids = selectiveCourse.getOtherFieldsOfKnowledge().split(",");
                    List<Integer> idsInt = Arrays.asList(ids).stream().map(Integer::parseInt).collect(Collectors.toList());
                    List<FieldOfKnowledge> otherFieldsOfKnowledge = fieldOfKnowledgeService.getFieldsOfKnowledge(idsInt);
                    fieldsOfKnowledge.addAll(otherFieldsOfKnowledge);
                }
                List<NamedDTO> fieldOfKnowledgeDTOS = map(fieldsOfKnowledge, NamedDTO.class);
                selectiveCourseDTO.setFieldsOfKnowledge(fieldOfKnowledgeDTOS);
            }
            selectiveCourseDTOS.add(selectiveCourseDTO);
        }
        return ResponseEntity.ok(map(selectiveCourseDTOS, SelectiveCourseDTO.class));
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

        List<Integer> fieldsOfKnowledgeIds = selectiveCourseWriteDTO.getFieldsOfKnowledge();
        if (fieldsOfKnowledgeIds != null && fieldsOfKnowledgeIds.size() != 0) {
            FieldOfKnowledge fieldOfKnowledge = fieldOfKnowledgeService.getFieldOfKnowledgeById(fieldsOfKnowledgeIds.get(0));
            if (fieldOfKnowledge == null) {
                throw new OperationCannotBePerformedException("Неправильний ідентифікатор галузі знань");
            } else {
                selectiveCourse.setFieldOfKnowledge(fieldOfKnowledge);
            }
            if (fieldsOfKnowledgeIds.size() > 1) {
                String idsStr = "";
                for (int i = 1; i < fieldsOfKnowledgeIds.size(); i++) {
                    idsStr += fieldsOfKnowledgeIds.get(i);
                    if (i != fieldsOfKnowledgeIds.size() - 1) {
                        idsStr += ",";
                    }
                }
                selectiveCourse.setOtherFieldsOfKnowledge(idsStr);
            }
        }
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
        if (selectiveCourse.getFieldOfKnowledge() != null || selectiveCourseWriteDTO.getFieldsOfKnowledge() != null) {
            if (selectiveCourse.getFieldOfKnowledge() == null && selectiveCourseWriteDTO.getFieldsOfKnowledge() != null) {
                List<Integer> fieldsOfKnowledgeIds = selectiveCourseWriteDTO.getFieldsOfKnowledge();
                if (fieldsOfKnowledgeIds != null && fieldsOfKnowledgeIds.size() != 0) {
                    FieldOfKnowledge fieldOfKnowledge = fieldOfKnowledgeService.getFieldOfKnowledgeById(fieldsOfKnowledgeIds.get(0));
                    if (fieldOfKnowledge == null) {
                        throw new OperationCannotBePerformedException("Неправильний ідентифікатор галузі знань");
                    } else {
                        selectiveCourse.setFieldOfKnowledge(fieldOfKnowledge);
                    }
                    if (fieldsOfKnowledgeIds.size() > 1) {
                        String idsStr = "";
                        for (int i = 1; i < fieldsOfKnowledgeIds.size(); i++) {
                            idsStr += fieldsOfKnowledgeIds.get(i);
                            if (i != fieldsOfKnowledgeIds.size() - 1) {
                                idsStr += ",";
                            }
                        }
                        selectiveCourse.setOtherFieldsOfKnowledge(idsStr);
                    }
                }
            }
        }

        Mapper.strictMap(selectiveCourseWriteDTO, selectiveCourse);
        return selectiveCourse;
    }
}