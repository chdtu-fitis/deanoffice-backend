package ua.edu.chdtu.deanoffice.api.teacher;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.api.report.debtor.DebtorReportController;
import ua.edu.chdtu.deanoffice.service.TeacherService;

import java.util.List;

@RestController
@RequestMapping("/teacher")
public class TeacherController {

    TeacherService teacherService;

    @Autowired
    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping
    public ResponseEntity getTeachers(@RequestParam List<Integer> teachersIds) {
        try {
            return ResponseEntity.ok().body(teacherService.getTeachersByIds(teachersIds));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PostMapping
    public ResponseEntity addTeacher(@RequestBody TeacherDTO teacherDTO) {
        return null;
    }


    @PutMapping
    public ResponseEntity changeTeacher() {
        return null;
    }


    @DeleteMapping
    public ResponseEntity deleteTeachers(@RequestParam List<Integer> teachersIds) {
        try {
            teacherService.deleteTeachers(teachersIds);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, TeacherController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
