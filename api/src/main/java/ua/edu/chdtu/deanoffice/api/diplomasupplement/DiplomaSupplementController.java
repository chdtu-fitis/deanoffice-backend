package ua.edu.chdtu.deanoffice.api.diplomasupplement;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.entity.Grade;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.service.GradeService;
import ua.edu.chdtu.deanoffice.service.StudentService;
import ua.edu.chdtu.deanoffice.service.document.diploma.supplement.StudentSummary;
import ua.edu.chdtu.deanoffice.service.document.diploma.supplement.TemplateFillFactory;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/diplsuppl")
public class DiplomaSupplementController {

    private GradeService gradeService;
    private StudentService studentService;

    public DiplomaSupplementController(GradeService gradeService, StudentService studentService) {
        this.gradeService = gradeService;
        this.studentService = studentService;
    }

    private static final String TEMPLATE = "DiplomaSupplementTemplate.docx";

    @RequestMapping(path = "/s")
    public ResponseEntity<File> generateForStudent(@RequestParam("id") Integer id) {
        Student student = studentService.get(id);
        List<List<Grade>> grades = gradeService.getGradesByStudentId(student.getId());
        StudentSummary studentSummary = new StudentSummary(student, grades);
        File f = TemplateFillFactory.fillWithStudentInformation(TEMPLATE, studentSummary);
        return ResponseEntity.ok(f);
    }
}
