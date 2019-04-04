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
    public ResponseEntity getTeachers(@RequestParam(required = false, defaultValue = "true") boolean active) {
        try {
            List<Teacher> teachers = teacherService.getTeachersByActive(active);
            return ResponseEntity.ok(map(teachers, TeacherDTO.class));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PostMapping("/teachers")
    public ResponseEntity addTeacher(@RequestBody TeacherDTO teacherDTO) {
        try {
            String errorMassage = null;
            if (teacherDTO == null)
                errorMassage = "Не отримані дані для збереження!";

            if (teacherDTO.getId() != 0)
                errorMassage = "Неправильно всказано id!";

            if (teacherDTO.getName() == null)
                errorMassage = "Не вказано ім'я!";

            if (teacherDTO.getSex() == null)
                errorMassage = "Не вказана стать!";

            if (teacherDTO.getSurname() == null)
                errorMassage = "Не вказано прізвище!";

            if (teacherDTO.getDepartment() == null)
                errorMassage = "Не вказана кафедра!";

            if (teacherDTO.getDepartment().getId() == 0)
                errorMassage = "Неправильно вказана кафедра!";//Можливо зробити щоб була перевірка на всі неіснуючі кафедри, а не тільки на 0

            if (errorMassage != null)
                throw new OperationCannotBePerformedException(errorMassage);

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
