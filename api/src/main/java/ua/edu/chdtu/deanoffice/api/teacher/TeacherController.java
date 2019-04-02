package ua.edu.chdtu.deanoffice.api.teacher;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.api.general.dto.PersonFullNameDTO;
import ua.edu.chdtu.deanoffice.api.general.mapper.Mapper;
import ua.edu.chdtu.deanoffice.api.report.debtor.DebtorReportController;
import ua.edu.chdtu.deanoffice.entity.Teacher;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.service.TeacherService;

import java.util.List;

import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;

@RestController
public class TeacherController {

    private TeacherService teacherService;

    @Autowired
    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
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
                                      @RequestParam(required = false) int departmentId,
                                      @RequestParam(required = false) String surname) {
        try {
            List<Teacher> teachers;
            if (departmentId == 0 && surname == null) {
                teachers = teacherService.getTeachersByActive(active);
            } else {
                if (departmentId != 0 && surname != null) {
                    teachers = teacherService.getTeachersByActiveAndDepartmentIdAndSurname(active, departmentId, surname);
                } else {
                    if (departmentId == 0) {
                        teachers = teacherService.getTeachersByActiveAndSurname(active, surname);
                    } else {
                        teachers = teacherService.getTeachersByActiveAndDepartmentId(active, departmentId);
                    }
                }
            }

            return ResponseEntity.ok(map(teachers, TeacherDTO.class));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PostMapping("/teachers")
    public ResponseEntity addTeacher(@RequestBody TeacherDTO teacherDTO) {
        try {
            if (teacherDTO.getId() != 0) {
                throw new OperationCannotBePerformedException("Неправильно всказано id!");
            }
            teacherService.save(Mapper.strictMap(teacherDTO, Teacher.class));
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PutMapping("/teachers")
    public ResponseEntity changeTeacher(@RequestBody TeacherDTO teacherDTO) {
        try {
            Teacher teacher = teacherService.getTeacher(teacherDTO.getId());
            if (teacher == null) {
                throw new OperationCannotBePerformedException("Викладача з вказаним id не існує!");
            }
            teacherService.save(Mapper.strictMap(teacherDTO, Teacher.class));
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @DeleteMapping("/teachers")
    public ResponseEntity deleteTeachers(@RequestParam List<Integer> teachersIds) {
        try {
            teacherService.setTeachersInactiveByIds(teachersIds);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, TeacherController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
