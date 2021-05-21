package ua.edu.chdtu.deanoffice.api.course.selective;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.course.selective.dto.SelectiveCourseDTO;
import ua.edu.chdtu.deanoffice.api.course.selective.dto.SelectiveCourseStudentDegreesDTO;
import ua.edu.chdtu.deanoffice.api.course.selective.dto.SelectiveCourseWriteDTO;
import ua.edu.chdtu.deanoffice.api.course.selective.dto.SelectiveCoursesStudentDegreeDTO;
import ua.edu.chdtu.deanoffice.api.course.selective.dto.SelectiveCoursesStudentDegreeIdDTO;
import ua.edu.chdtu.deanoffice.api.course.selective.dto.SelectiveCoursesStudentDegreeWriteDTO;
import ua.edu.chdtu.deanoffice.api.course.selective.dto.StudentDegreeDTO;
import ua.edu.chdtu.deanoffice.api.course.selective.dto.SelectiveCourseWithStudentsCountDTO;
import ua.edu.chdtu.deanoffice.api.course.selective.dto.SelectiveCoursesSelectionRulesDTO;
import ua.edu.chdtu.deanoffice.api.general.dto.NamedDTO;
import ua.edu.chdtu.deanoffice.api.general.dto.validation.ExistingIdDTO;
import ua.edu.chdtu.deanoffice.api.general.mapper.Mapper;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.Degree;
import ua.edu.chdtu.deanoffice.entity.Department;
import ua.edu.chdtu.deanoffice.entity.FieldOfKnowledge;
import ua.edu.chdtu.deanoffice.entity.PeriodCaseEnum;
import ua.edu.chdtu.deanoffice.entity.SelectiveCourse;
import ua.edu.chdtu.deanoffice.entity.SelectiveCoursesStudentDegrees;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.Teacher;
import ua.edu.chdtu.deanoffice.entity.SelectiveCoursesYearParameters;
import ua.edu.chdtu.deanoffice.entity.TuitionTerm;
import ua.edu.chdtu.deanoffice.entity.TypeCycle;
import ua.edu.chdtu.deanoffice.exception.NotFoundException;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.service.DegreeService;
import ua.edu.chdtu.deanoffice.service.DepartmentService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.TeacherService;
import ua.edu.chdtu.deanoffice.service.course.CourseService;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;
import ua.edu.chdtu.deanoffice.service.course.selective.FieldOfKnowledgeService;
import ua.edu.chdtu.deanoffice.service.course.selective.SelectiveCourseService;
import ua.edu.chdtu.deanoffice.service.course.selective.SelectiveCoursesStudentDegreesService;
import ua.edu.chdtu.deanoffice.service.SelectiveCoursesYearParametersService;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.Date;

import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;
import static ua.edu.chdtu.deanoffice.service.course.selective.SelectiveCourseConstants.SELECTIVE_COURSES_NUMBER;

@RestController
@RequestMapping("/selective-courses")
public class SelectiveCourseController {

    private SelectiveCourseService selectiveCourseService;
    private TeacherService teacherService;
    private CourseService courseService;
    private DegreeService degreeService;
    private DepartmentService departmentService;
    private FieldOfKnowledgeService fieldOfKnowledgeService;
    private StudentDegreeService studentDegreeService;
    private SelectiveCoursesStudentDegreesService selectiveCoursesStudentDegreesService;
    private SelectiveCoursesYearParametersService selectiveCoursesYearParametersService;
    private CurrentYearService currentYearService;

    public SelectiveCourseController(SelectiveCourseService selectiveCourseService,
                                     TeacherService teacherService, FieldOfKnowledgeService fieldOfKnowledgeService,
                                     CourseService courseService, DegreeService degreeService, DepartmentService departmentService,
                                     StudentDegreeService studentDegreeService, SelectiveCoursesStudentDegreesService selectiveCoursesStudentDegreesService,
                                     SelectiveCoursesYearParametersService selectiveCoursesYearParametersService,
                                     CurrentYearService currentYearService) {
        this.selectiveCourseService = selectiveCourseService;
        this.teacherService = teacherService;
        this.courseService = courseService;
        this.degreeService = degreeService;
        this.departmentService = departmentService;
        this.fieldOfKnowledgeService = fieldOfKnowledgeService;
        this.studentDegreeService = studentDegreeService;
        this.selectiveCoursesStudentDegreesService = selectiveCoursesStudentDegreesService;
        this.selectiveCoursesYearParametersService = selectiveCoursesYearParametersService;
        this.currentYearService = currentYearService;
    }

    @GetMapping
    public ResponseEntity getAvailableSelectiveCoursesByStudyYearAndDegreeAndSemester(@RequestParam(required = false) Integer studyYear,
                                                                                      @RequestParam int degreeId,
                                                                                      @RequestParam int semester,
                                                                                      @RequestParam(required = false) boolean thisYear) {
        List<SelectiveCourseDTO> selectiveCourseDTOS = new ArrayList<>();
        List<SelectiveCourse> selectiveCourses = selectiveCourseService.getSelectiveCoursesByStudyYearAndDegreeAndSemester(studyYear, degreeId, semester, thisYear);
        for (SelectiveCourse selectiveCourse : selectiveCourses) {
            SelectiveCourseDTO selectiveCourseDTO = map(selectiveCourse, SelectiveCourseDTO.class);
            setFieldsOfKnowledge(selectiveCourse, selectiveCourseDTO);
            selectiveCourseDTOS.add(selectiveCourseDTO);
        }
        return ResponseEntity.ok(map(selectiveCourseDTOS, SelectiveCourseDTO.class));
    }

    private void setFieldsOfKnowledge(SelectiveCourse selectiveCourse, SelectiveCourseDTO selectiveCourseDTO) {
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
            setOtherFieldsOfKnowledge(fieldsOfKnowledgeIds, selectiveCourse);
        }
        SelectiveCourse selectiveCourseAfterSave = selectiveCourseService.create(selectiveCourse);
        SelectiveCourseDTO selectiveCourseAfterSaveDTO = map(selectiveCourseAfterSave, SelectiveCourseDTO.class);
        setFieldsOfKnowledge(selectiveCourseAfterSave, selectiveCourseAfterSaveDTO);
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
        setFieldsOfKnowledge(selectiveCourseAfterSave, selectiveCourseSavedDTO);
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
                setFieldsOfKnowledge(selectiveCourse, selectiveCourseWriteDTO);
            } else {
                if (selectiveCourse.getFieldOfKnowledge() != null && selectiveCourseWriteDTO.getFieldsOfKnowledge() == null) {
                    selectiveCourse.setFieldOfKnowledge(null);
                    selectiveCourse.setOtherFieldsOfKnowledge(null);
                } else {
                    String other = selectiveCourse.getOtherFieldsOfKnowledge();
                    String fullFieldsOfKnowledge = selectiveCourse.getFieldOfKnowledge().getId() + (other == null ? "" : "," + other);
                    String fullFieldsOfKnowledgeDtoStr = selectiveCourseWriteDTO.getFieldsOfKnowledge().toString()
                            .replaceAll("(^\\[|\\]$)", "").replaceAll("\\s+", "");
                    if (!fullFieldsOfKnowledge.equals(fullFieldsOfKnowledgeDtoStr)) {
                        setFieldsOfKnowledge(selectiveCourse, selectiveCourseWriteDTO);
                    }
                }
            }
        }
        Mapper.strictMap(selectiveCourseWriteDTO, selectiveCourse);
        return selectiveCourse;
    }

    private void setFieldsOfKnowledge(SelectiveCourse selectiveCourse, SelectiveCourseWriteDTO selectiveCourseWriteDTO) throws OperationCannotBePerformedException {
        List<Integer> fieldsOfKnowledgeIds = selectiveCourseWriteDTO.getFieldsOfKnowledge();
        if (fieldsOfKnowledgeIds.size() != 0) {
            FieldOfKnowledge fieldOfKnowledge = fieldOfKnowledgeService.getFieldOfKnowledgeById(fieldsOfKnowledgeIds.get(0));
            if (fieldOfKnowledge == null) {
                throw new OperationCannotBePerformedException("Неправильний ідентифікатор галузі знань");
            }
            selectiveCourse.setFieldOfKnowledge(fieldOfKnowledge);
            setOtherFieldsOfKnowledge(fieldsOfKnowledgeIds, selectiveCourse);
        }
    }

    private void setOtherFieldsOfKnowledge(List<Integer> fieldsOfKnowledgeIds, SelectiveCourse selectiveCourse) {
        if (fieldsOfKnowledgeIds.size() > 1) {
            String idsStr = "";
            for (int i = 1; i < fieldsOfKnowledgeIds.size(); i++) {
                idsStr += fieldsOfKnowledgeIds.get(i);
                if (i != fieldsOfKnowledgeIds.size() - 1) {
                    idsStr += ",";
                }
            }
            selectiveCourse.setOtherFieldsOfKnowledge(idsStr);
        } else {
            selectiveCourse.setOtherFieldsOfKnowledge(null);
        }
    }

    @Secured({"ROLE_NAVCH_METHOD", "ROLE_STUDENT"})
    @PostMapping("/registration")
    public ResponseEntity<SelectiveCoursesStudentDegreeIdDTO> recordOnSelectiveCourse(@Validated @RequestBody SelectiveCoursesStudentDegreeWriteDTO selectiveCoursesStudentDegreesDTO)
            throws OperationCannotBePerformedException {

        int selectiveCoursesRegistrationYear = currentYearService.getYear();

        StudentDegree studentDegree = studentDegreeService.getById(selectiveCoursesStudentDegreesDTO.getStudentDegree().getId());
        if (studentDegree == null || !studentDegree.isActive()) {
            return new ResponseEntity("Неправильний ідентифікатор студента", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        else if (selectiveCoursesStudentDegreesDTO.getSelectiveCourses().size() == 0) {
            return new ResponseEntity("Не надіслано дані для збереження", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        Date today = new Date();
        List<SelectiveCoursesYearParameters> selectiveCoursesYearParametersFromDB =
                    selectiveCoursesYearParametersService.getSelectiveCoursesYearParametersByYear(selectiveCoursesRegistrationYear);

        PeriodCaseEnum periodCase = selectiveCourseService.getPeriodCaseByStudentDegree(studentDegree);
        if (periodCase == null)
            return new ResponseEntity("Даний студент не може реєструватись на вибіркові дисципліни" ,HttpStatus.UNPROCESSABLE_ENTITY);

        SelectiveCoursesYearParameters selectiveCoursesYearParameters = selectiveCoursesYearParametersFromDB.stream()
                .filter(elem -> elem.getPeriodCase() == periodCase)
                .findFirst().orElse(null);

        if (selectiveCoursesYearParameters == null)
            return new ResponseEntity("Для даного студента відсутні параметри вибору вибіркових дисциплін", HttpStatus.UNPROCESSABLE_ENTITY);

        List<SelectiveCourse> selectiveCourses = selectiveCourseService.getSelectiveCourses(selectiveCoursesStudentDegreesDTO.getSelectiveCourses());

        if (today.after(selectiveCoursesYearParameters.getFirstRoundStartDate()) && today.before(selectiveCoursesYearParameters.getFirstRoundEndDate())) {
            checkRecordOnSelectiveCoursesData(studentDegree, false, selectiveCourses);
            return recordOnSelectiveCoursesByRules(studentDegree, selectiveCourses);
        }
        else if (today.after(selectiveCoursesYearParameters.getSecondRoundStartDate()) && today.before(selectiveCoursesYearParameters.getSecondRoundEndDate())) {
            List<SelectiveCourse> selectiveCoursesFromDB = selectiveCoursesStudentDegreesService
                    .getSelectiveCoursesStudentDegreeIdByStudentDegreeId(false, selectiveCoursesRegistrationYear + 1, studentDegree.getId())
                    .getSelectiveCourses();

            if (selectiveCoursesFromDB == null)
                selectiveCoursesFromDB = selectiveCourses;
            else
                selectiveCoursesFromDB.addAll(selectiveCourses);

            checkRecordOnSelectiveCoursesData(studentDegree, true, selectiveCoursesFromDB);

            return recordOnSelectiveCoursesByRules(studentDegree, selectiveCourses);
        }
        else
            return ResponseEntity.ok().build();
    }

    private boolean checkRecordOnSelectiveCoursesData(StudentDegree studentDegree, boolean isSecondRound, List<SelectiveCourse> selectiveCourses)
            throws OperationCannotBePerformedException {

        if (!isSecondRound) {
            if (selectiveCoursesStudentDegreesService
                    .getSelectiveCoursesStudentDegreeIdByStudentDegreeId(false,currentYearService.getYear() + 1, studentDegree.getId())
                    .getSelectiveCourses().size() > 0) {
                throw new OperationCannotBePerformedException("Даний студент вже зареєстрований на вибіркові дисципліни");
            }
        }
        if (selectiveCourses == null || selectiveCourses.size() == 0) {
            throw new OperationCannotBePerformedException("Неправильні ідентифікатори предметів");
        }
        if (!selectiveCourseService.checkSelectiveCoursesIntegrity(studentDegree, selectiveCourses)) {
            throw new OperationCannotBePerformedException("Кількість або семестри вибіркових предметів не відповідають правилам");
        }
        return true;
    }

    private ResponseEntity<SelectiveCoursesStudentDegreeIdDTO> recordOnSelectiveCoursesByRules(StudentDegree studentDegree, List<SelectiveCourse> selectiveCourses) {
        List<SelectiveCoursesStudentDegrees> selectiveCoursesStudentDegrees = new ArrayList<>();

        for (SelectiveCourse selectiveCourse : selectiveCourses) {
            SelectiveCoursesStudentDegrees selectiveCoursesForStudentDegree = new SelectiveCoursesStudentDegrees();
            selectiveCoursesForStudentDegree.setStudentDegree(studentDegree);
            selectiveCoursesForStudentDegree.setSelectiveCourse(selectiveCourse);
            selectiveCoursesStudentDegrees.add(selectiveCoursesForStudentDegree);
        }

        List<SelectiveCoursesStudentDegrees> selectiveCoursesStudDegreeAfterSave = selectiveCoursesStudentDegreesService.create(selectiveCoursesStudentDegrees);

        ExistingIdDTO studentDegreeDTO = map(selectiveCoursesStudDegreeAfterSave.get(0).getStudentDegree(), ExistingIdDTO.class);
        List<SelectiveCourse> selectiveCoursesSaved = selectiveCoursesStudDegreeAfterSave.stream()
                .map(selectiveCourseStudDegree -> selectiveCourseStudDegree.getSelectiveCourse()).collect(Collectors.toList());
        List<SelectiveCourseDTO> selectiveCoursesSavedDTO = map(selectiveCoursesSaved, SelectiveCourseDTO.class);
        SelectiveCoursesStudentDegreeIdDTO afterSaveDTO = new SelectiveCoursesStudentDegreeIdDTO();
        afterSaveDTO.setSelectiveCourses(selectiveCoursesSavedDTO);
        afterSaveDTO.setStudentDegree(studentDegreeDTO);
        return new ResponseEntity(afterSaveDTO, HttpStatus.CREATED);
    }

    /*Повертає об'єкт зі значеннями полів null, якщо студент не зареєстрований на жодну вибіркову дисципліну*/
    @GetMapping("/student-courses")
    public ResponseEntity<SelectiveCoursesStudentDegreeIdDTO> getStudentSelectiveCourses(@RequestParam int studyYear, @RequestParam int studentDegreeId, @RequestParam(required = false) boolean all) {
        return ResponseEntity.ok(map(selectiveCoursesStudentDegreesService.
                getSelectiveCoursesStudentDegreeIdByStudentDegreeId(all, studyYear, studentDegreeId), SelectiveCoursesStudentDegreeIdDTO.class));
    }

    /*Повертає об'єкт зі значеннями полів null, якщо жоден студент не зареєстрований на дану вибіркову дисципліну*/
    @GetMapping("/course-students")
    public ResponseEntity<SelectiveCourseStudentDegreesDTO> getSelectiveCourseStudents(@RequestParam int selectiveCourseId,
                                                                                       @RequestParam(required = false) boolean forFaculty) {
        SelectiveCourseStudentDegreesDTO selectiveCourseStudentDegreesDTO = new SelectiveCourseStudentDegreesDTO();
        List<SelectiveCoursesStudentDegrees> studentDegreesForSelectiveCourse = selectiveCoursesStudentDegreesService.getStudentDegreesForSelectiveCourse(selectiveCourseId, forFaculty);
        if (studentDegreesForSelectiveCourse.size() > 0) {
            selectiveCourseStudentDegreesDTO.setSelectiveCourse(map(studentDegreesForSelectiveCourse.get(0).getSelectiveCourse(), SelectiveCourseDTO.class));
            List<StudentDegreeDTO> studentDegreeDTOs = studentDegreesForSelectiveCourse.stream().map(selectiveCourseStudentDegree ->
                    map(selectiveCourseStudentDegree.getStudentDegree(), StudentDegreeDTO.class)).collect(Collectors.toList());
            selectiveCourseStudentDegreesDTO.setStudentDegrees(studentDegreeDTOs);
        }
        return ResponseEntity.ok(selectiveCourseStudentDegreesDTO);
    }

    @GetMapping("/students-count")
    public ResponseEntity<List<SelectiveCourseWithStudentsCountDTO>> getSelectiveCoursesWithStudentsCount(@RequestParam @NotNull @Min(2010) int studyYear,
                                                                                                          @RequestParam @NotNull int semester,
                                                                                                          @RequestParam @NotNull int degreeId) {
        Map<SelectiveCourse, Long> selectiveCoursesStudentDegrees = selectiveCoursesStudentDegreesService.getSelectiveCoursesWithStudentsCount(studyYear, semester, degreeId);
        List<SelectiveCourseWithStudentsCountDTO> selectiveCourseWithStudentsCountDTOS = selectiveCoursesStudentDegrees.entrySet().stream()
                .map(entry -> {
                    SelectiveCourseWithStudentsCountDTO selectiveCourseWithStudentsCountDTO = map(entry.getKey(), SelectiveCourseWithStudentsCountDTO.class);
                    selectiveCourseWithStudentsCountDTO.setStudentsCount(entry.getValue().intValue());
                    return selectiveCourseWithStudentsCountDTO;
                }).collect(Collectors.toList());

        Collections.sort(selectiveCourseWithStudentsCountDTOS, Collections.reverseOrder());

        return ResponseEntity.ok(selectiveCourseWithStudentsCountDTOS);
    }

    @Secured({"ROLE_NAVCH_METHOD"})
    @PatchMapping("/disqualification")
    public ResponseEntity updateSelectiveCoursesStudentDegrees(@RequestParam @NotNull int semester,
                                                               @RequestParam @NotNull int degreeId) throws OperationCannotBePerformedException {
        selectiveCoursesStudentDegreesService.disqualifySelectiveCoursesAndCancelStudentRegistrations(semester, degreeId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/selection-rules")
    public ResponseEntity<List<SelectiveCoursesSelectionRulesDTO>> getSelectiveCoursesSelectionRules(@RequestParam int studentDegreeId) throws NotFoundException {
        StudentDegree studentDegree = studentDegreeService.getById(studentDegreeId);
        if (studentDegree == null)
           return new ResponseEntity("Не існує studentDegree з таким id", HttpStatus.UNPROCESSABLE_ENTITY);

        int studentDegreeYear = studentDegree.getTuitionTerm() == TuitionTerm.SHORTENED ?
                studentDegreeService.getShortenedRealStudentDegreeYear(studentDegree) : studentDegreeService.getStudentDegreeYear(studentDegree);

        PeriodCaseEnum periodCase = selectiveCourseService.getPeriodCaseByStudentDegree(studentDegree);
        if (periodCase == null) {
            return new ResponseEntity("Для даного студента відсутні правила вибору вибіркових дисциплін" ,HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if (periodCase == PeriodCaseEnum.LATE)
            studentDegreeYear -= 1;

        List<SelectiveCoursesSelectionRulesDTO> selectiveCoursesSelectionRulesDTO = new ArrayList<>();

        for (Map.Entry<String, Integer[]> entry : SELECTIVE_COURSES_NUMBER.get(studentDegree.getSpecialization().getDegree().getId())[studentDegreeYear].entrySet()) {
            TypeCycle typeCycle = TypeCycle.getTypeCycleByName(entry.getKey());
            selectiveCoursesSelectionRulesDTO.add(new SelectiveCoursesSelectionRulesDTO(typeCycle, entry.getValue()));
        }

        return ResponseEntity.ok(selectiveCoursesSelectionRulesDTO);
    }

    @GetMapping("/student-courses-by-surname")
    public ResponseEntity<List<SelectiveCoursesStudentDegreeDTO>> getStudentSelectiveCoursesBySurname(@RequestParam(required = false) boolean all, int studyYear, String surname) {
        List<Integer> studentDegreeIds = studentDegreeService.getAllStudentDegreesByStudentSurname(surname).stream()
                .map(studentDegree -> studentDegree.getId()).collect(Collectors.toList());

        return ResponseEntity.ok(map(selectiveCoursesStudentDegreesService.
                getSelectiveCoursesStudentDegreesByStudentDegreeIds(all, studyYear, studentDegreeIds), SelectiveCoursesStudentDegreeDTO.class));
    }
}
