package ua.edu.chdtu.deanoffice.api.teacher;


import org.springframework.beans.factory.annotation.Autowired;
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
import ua.edu.chdtu.deanoffice.repository.DepartmentRepository;
import ua.edu.chdtu.deanoffice.repository.PositionRepository;
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
    private DataVerificationService dataVerificationService;
    private DepartmentService departmentService;
    private PositionService positionService;
    private FacultyAuthorizationService facultyAuthorizationService;

    @Autowired
    public TeacherController(TeacherService teacherService, DataVerificationService dataVerificationService,
                             DepartmentService departmentService, PositionService positionService,
                             FacultyAuthorizationService facultyAuthorizationService) {
        this.teacherService = teacherService;
        this.dataVerificationService = dataVerificationService;
        this.departmentService = departmentService;
        this.positionService = positionService;
        this.facultyAuthorizationService = facultyAuthorizationService;
    }

    @GetMapping("/teachers-short")
    public ResponseEntity getAllActiveTeachers(@CurrentUser ApplicationUser user){
        try {
            List<Teacher> teachers = teacherService.getTeachersByActiveAndFacultyId(true, user.getFaculty().getId());
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
                throw new OperationCannotBePerformedException("Неправильно всказано id, id повинно бути 0!");
            Teacher teacher = Mapper.strictMap(teacherDTO, Teacher.class);
            dataVerificationService.isCorrectTeacherFromDTO(teacher);
            facultyAuthorizationService.verifyAccessibilityOfDepartment(user, teacher.getDepartment());
            existDepartmentAndPositionInDataBase(teacher);
            teacherService.save(teacher);
            return ResponseEntity.noContent().build();
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
            if (teacherFromDB == null) {
                throw new OperationCannotBePerformedException("Викладача з вказаним id не існує!");
            }
            Teacher teacher = Mapper.strictMap(teacherDTO, Teacher.class);
            dataVerificationService.isCorrectTeacherFromDTO(teacher);
            facultyAuthorizationService.verifyAccessibilityOfDepartment(user, teacherFromDB.getDepartment());
            facultyAuthorizationService.verifyAccessibilityOfDepartment(user, teacher.getDepartment());
            existDepartmentAndPositionInDataBase(teacher);
            teacherService.save(Mapper.strictMap(teacherDTO, Teacher.class));
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private void existDepartmentAndPositionInDataBase(Teacher teacher) throws OperationCannotBePerformedException {
        String errorMassage = null;
        Department department = departmentService.getById(teacher.getDepartment().getId());
        if (department == null)
            errorMassage = "Вказана неіснуюча кафедра!";
        Position position = positionService.getById(teacher.getPosition().getId());
        if (position == null)
            errorMassage = "Вказана неіснуюча позиція!";
        if (errorMassage != null)
            throw new OperationCannotBePerformedException(errorMassage);
        setCorrectDepartmentAndPositionFromDataBase(teacher, department, position);
    }

    private void setCorrectDepartmentAndPositionFromDataBase(Teacher teacher, Department department, Position position) {
        teacher.setDepartment(department);
        teacher.setPosition(position);
    }

    @DeleteMapping("/teachers")
    public ResponseEntity deleteTeachers(@RequestParam List<Integer> teachersIds,
                                         @CurrentUser ApplicationUser user) {
        try {
            if (teachersIds.size() == 0)
                throw new OperationCannotBePerformedException("Невказані ідентифікатори викладачів!");
            List<Teacher> teachers = teacherService.getTeachers(teachersIds);
            if (teachers.size() != teachersIds.size())
                throw new OperationCannotBePerformedException("Серед даних ідентифікаторів викладачів є неіснуючі!");
            dataVerificationService.areTheseTeachersActive(teachersIds);
            facultyAuthorizationService.verifyAccessibilityOfDepartments(user, teachersIds);
            teacherService.deleteByIds(teachersIds);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, TeacherController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
