package ua.edu.chdtu.deanoffice.api.teacher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.api.general.dto.PersonFullNameDTO;
import ua.edu.chdtu.deanoffice.api.general.mapper.Mapper;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.Department;
import ua.edu.chdtu.deanoffice.entity.Position;
import ua.edu.chdtu.deanoffice.entity.Teacher;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.service.DataVerificationService;
import ua.edu.chdtu.deanoffice.service.DepartmentService;
import ua.edu.chdtu.deanoffice.service.PositionService;
import ua.edu.chdtu.deanoffice.service.TeacherService;
import ua.edu.chdtu.deanoffice.service.security.FacultyAuthorizationService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.util.List;

import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;

@RestController
public class TeacherController {

    private TeacherService teacherService;
    private DepartmentService departmentService;
    private PositionService positionService;

    @Autowired
    public TeacherController(TeacherService teacherService,
                             DepartmentService departmentService, PositionService positionService) {
        this.teacherService = teacherService;
        this.departmentService = departmentService;
        this.positionService = positionService;
    }

    @GetMapping("/teachers-short")
    public ResponseEntity getAllActiveTeachers(){
        try {
            List<Teacher> teachers = teacherService.getTeachersByActive(true);
            return ResponseEntity.ok(map(teachers, PersonFullNameDTO.class));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/teachers")
    public ResponseEntity getTeachers(@RequestParam(required = false, defaultValue = "true") boolean active,
                                      @CurrentUser ApplicationUser user) {
        try {
            List<Teacher> teachers = teacherService.getTeachersByActiveAndFacultyId(active, user.getFaculty().getId());
            return ResponseEntity.ok(map(teachers, TeacherDTO.class));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PostMapping("/teachers")
    public ResponseEntity addTeacher(@RequestBody TeacherDTO teacherDTO,
                                     @CurrentUser ApplicationUser user) {
        try {
            if (teacherDTO == null)
                throw new OperationCannotBePerformedException("Не отримані дані для збереження!");
            if (teacherDTO.getId() != 0)
                throw new OperationCannotBePerformedException("Неправильно всказаний ідентифікатор, ідентифікатор повинен бути 0!");
            Teacher teacher = Mapper.strictMap(teacherDTO, Teacher.class);
            setCorrectDepartmentAndPositionFromDataBase(teacher, teacherDTO);
            Teacher teacherAfterSave = teacherService.saveTeacher(user, teacher);
            TeacherDTO teacherAfterSaveDTO = map(teacherAfterSave, TeacherDTO.class);
            return new ResponseEntity(teacherAfterSaveDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PutMapping("/teachers")
    public ResponseEntity changeTeacher(@RequestBody TeacherDTO teacherDTO,
                                        @CurrentUser ApplicationUser user) {
        try {
            if (teacherDTO == null)
                throw new OperationCannotBePerformedException("Не отримані дані для зміни!");
            Teacher teacherFromDB = teacherService.getTeacher(teacherDTO.getId());
            if (teacherFromDB == null)
                throw new OperationCannotBePerformedException("Викладача з вказаним ідентифікатором не існує!");
            Teacher teacher = Mapper.strictMap(teacherDTO, Teacher.class);
            setCorrectDepartmentAndPositionFromDataBase(teacher, teacherDTO);
            teacherService.updateTeacher(user, teacher, teacherFromDB);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @DeleteMapping("/teachers/{teachersIds}")
    public ResponseEntity deleteTeachers(@PathVariable List<Integer> teachersIds,
                                         @CurrentUser ApplicationUser user) {
        try {
            teacherService.deleteByIds(user, teachersIds);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, TeacherController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }

    private void setCorrectDepartmentAndPositionFromDataBase(Teacher teacher, TeacherDTO teacherDTO) throws OperationCannotBePerformedException {
        Department department = departmentService.getById(teacherDTO.getDepartmentId());
        if (department == null)
            throw new OperationCannotBePerformedException("Вказана неіснуюча кафедра!");
        Position position = positionService.getById(teacherDTO.getPositionId());
        if (position == null)
            throw new OperationCannotBePerformedException("Вказана неіснуюча посада!");
        teacher.setDepartment(department);
        teacher.setPosition(position);
    }
}
