package ua.edu.chdtu.deanoffice.service.document.diploma.supplement;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Grade;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.service.GradeService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class DiplomaSupplementService {

    private static final String TEMPLATES_PATH = "docs/templates/";
    private static final String TEMPLATE = TEMPLATES_PATH + "DiplomaSupplement.docx";

    private static Logger log = LoggerFactory.getLogger(DiplomaSupplementService.class);

    private GradeService gradeService;
    private StudentDegreeService studentDegreeService;
    private DocumentIOService documentIOService;
    private TemplateFillService templateFillService;

    public DiplomaSupplementService(GradeService gradeService,
                                    StudentDegreeService studentDegreeService,
                                    DocumentIOService documentIOService,
                                    TemplateFillService templateFillService) {
        this.gradeService = gradeService;
        this.studentDegreeService = studentDegreeService;
        this.documentIOService = documentIOService;
        this.templateFillService = templateFillService;
    }

    public File formDiplomaSupplementForStudent(Integer studentDegreeId) throws Docx4JException, IOException {
        StudentDegree studentDegree = studentDegreeService.getById(studentDegreeId);
        Student student = studentDegree.getStudent();
        List<List<Grade>> grades = gradeService.getGradesByStudentDegreeId(studentDegreeId);
        StudentSummary studentSummary = new StudentSummary(studentDegree, grades);
        String fileName = student.getSurnameEng() + "_" + studentSummary.getStudent().getNameEng();
        WordprocessingMLPackage filledTemplate = templateFillService.fill(TEMPLATE, studentSummary);
        return documentIOService.saveDocumentToTemp(filledTemplate, fileName + ".docx");
    }


}
