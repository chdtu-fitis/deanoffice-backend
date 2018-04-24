package ua.edu.chdtu.deanoffice.api.general.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.general.dto.PersonFullNameDTO;
import ua.edu.chdtu.deanoffice.entity.Teacher;
import ua.edu.chdtu.deanoffice.service.TeacherService;

import java.util.List;

import static ua.edu.chdtu.deanoffice.api.general.parser.Parser.parse;

@RestController
public class TeacherController {
    @Autowired
    TeacherService teacherService;

    @GetMapping("/teachers")
    public ResponseEntity getAllTeachers(){
        List<Teacher> teachers = teacherService.getTeachers();
        return ResponseEntity.ok(parse(teachers, PersonFullNameDTO.class));
    }
}
