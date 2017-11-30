package ua.edu.chdtu.deanoffice.service.document.diploma.supplement;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Grade;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.service.GradeService;
import ua.edu.chdtu.deanoffice.service.StudentService;

import java.io.File;
import java.util.List;

@Service
public class DiplomaSupplementService {

    private StudentService studentService;
    private GradeService gradeService;

    public DiplomaSupplementService(StudentService studentService, GradeService gradeService) {
        this.studentService = studentService;
        this.gradeService = gradeService;
    }

    private static final String TEMPLATE = "DiplomaSupplementTemplate.docx";

    public File formDiplomaSupplement(Integer studentId) {
        Student student = studentService.get(studentId);
        List<List<Grade>> grades = gradeService.getGradesByStudentId(student.getId());
        StudentSummary studentSummary = new StudentSummary(student, grades);
        return DiplomaSupplementTemplateFiller.fillWithStudentInformation(TEMPLATE, studentSummary);
    }


}
