package ua.edu.chdtu.deanoffice.api.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.service.StudentAcademicVacationService;

@RestController
@RequestMapping("/students/academic-vacations")
public class StudentAcademicVacationController {
    private final StudentAcademicVacationService studentAcademicVacationService;

    @Autowired
    public StudentAcademicVacationController(StudentAcademicVacationService studentAcademicVacationService) {
        this.studentAcademicVacationService = studentAcademicVacationService;
    }
}
