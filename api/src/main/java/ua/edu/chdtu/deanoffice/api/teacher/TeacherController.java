package ua.edu.chdtu.deanoffice.api.teacher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.api.general.dto.PersonFullNameDTO;
import ua.edu.chdtu.deanoffice.api.general.mapper.Mapper;
import ua.edu.chdtu.deanoffice.entity.Teacher;
import ua.edu.chdtu.deanoffice.service.TeacherService;
import javax.validation.Valid;
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
    public ResponseEntity getAllActiveTeachersShort() {
        try {
            List<Teacher> teachers = teacherService.getTeachersByActive(true);
            return ResponseEntity.ok(map(teachers, PersonFullNameDTO.class));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/teachers-full")
    public ResponseEntity getAllActiveTeachers(@RequestParam(required = false, defaultValue = "true") boolean active) {
        try {
            List<Teacher> teachers = teacherService.getTeachersByActive(active);
            return ResponseEntity.ok(map(teachers, TeacherDTO.class));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/inactive-teachers")
    public ResponseEntity getInactiveTeachers() {
        try {
            List<Teacher> teachers = teacherService.getTeachersByActive(false);
            return ResponseEntity.ok(map(teachers, TeacherDTO.class));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/teachers")
    public ResponseEntity getTeachers(@RequestParam(required = false, defaultValue = "true") boolean active) {
        try {
            List<Teacher> teachers = teacherService.getActiveFacultyTeachers(active);
            return ResponseEntity.ok(map(teachers, TeacherDTO.class));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PostMapping("/teachers")
    public ResponseEntity addTeacher(@Valid @RequestBody TeacherInsertDTO teacherDTO) {
        try {
            Teacher teacher = Mapper.strictMap(teacherDTO, Teacher.class);
            Teacher teacherAfterSave = teacherService.createTeacher(teacher);
            TeacherDTO teacherAfterSaveDTO = map(teacherAfterSave, TeacherDTO.class);
            return new ResponseEntity(teacherAfterSaveDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PutMapping("/teachers")
    public ResponseEntity changeTeacher(@Valid @RequestBody TeacherUpdateDTO teacherDTO) {
        try {
            Teacher teacher = Mapper.strictMap(teacherDTO, Teacher.class);
            Teacher savedTeacher = teacherService.updateTeacher(teacher);
            return new ResponseEntity(map(savedTeacher, TeacherDTO.class), HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @DeleteMapping("/teachers/{teachersIds}")
    public ResponseEntity deleteTeachers(@PathVariable List<Integer> teachersIds) {
        try {
            teacherService.deleteByIds(teachersIds);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PutMapping("/teachers/restore")
    public ResponseEntity restoreTeachers(@RequestParam List<Integer> teachersIds) {
        try {
            teacherService.restoreByIds(teachersIds);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, TeacherController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
