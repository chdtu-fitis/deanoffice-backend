package ua.edu.chdtu.deanoffice.service.document.diploma.supplement;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Grade;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.service.GradeService;
import ua.edu.chdtu.deanoffice.service.StudentService;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.*;

@Service
public class DiplomaSupplementService {

    private StudentService studentService;
    private GradeService gradeService;

    public DiplomaSupplementService(StudentService studentService, GradeService gradeService) {
        this.studentService = studentService;
        this.gradeService = gradeService;
    }

    private StudentSummary studentSummary;

    public StudentSummary getStudentSummary() {
        return studentSummary;
    }

    public void setStudentSummary(StudentSummary studentSummary) {
        this.studentSummary = studentSummary;
    }

    private static final String TEMPLATE = "DiplomaSupplementTemplate.docx";

    public File formDiplomaSupplement(Integer studentId) {
        Student student = studentService.get(studentId);
        List<List<Grade>> grades = gradeService.getGradesByStudentId(student.getId());
        this.studentSummary = new StudentSummary(student, grades);
        return fillWithStudentInformation(TEMPLATE);
    }

    public File fillWithStudentInformation(String templateFilepath) {
        WordprocessingMLPackage template = loadTemplate(templateFilepath);
        Map<String, String> commonDict = new HashMap<>();
        commonDict.putAll(studentSummary.getStudentInfoDictionary());
        commonDict.putAll(studentSummary.getTotalDictionary());
        replacePlaceholders(template, commonDict);
        return saveDocument(template, studentSummary.getStudent().getInitialsUkr() + ".docx");
    }
}
