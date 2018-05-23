package ua.edu.chdtu.deanoffice.service.document.diploma.supplement;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Grade;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.service.GradeService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static ua.edu.chdtu.deanoffice.service.document.DocumentIOService.TEMPLATES_PATH;

@Service
public class DiplomaSupplementService {

    private static final String TEMPLATE = TEMPLATES_PATH + "DiplomaSupplement.docx";

    private final GradeService gradeService;
    private final StudentDegreeService studentDegreeService;
    private final DocumentIOService documentIOService;
    private final SupplementTemplateFillService supplementTemplateFillService;

    public DiplomaSupplementService(GradeService gradeService,
                                    StudentDegreeService studentDegreeService,
                                    DocumentIOService documentIOService,
                                    SupplementTemplateFillService supplementTemplateFillService) {
        this.gradeService = gradeService;
        this.studentDegreeService = studentDegreeService;
        this.documentIOService = documentIOService;
        this.supplementTemplateFillService = supplementTemplateFillService;
    }

    public File formDiplomaSupplement(Integer studentDegreeId, FileFormatEnum format)
            throws Docx4JException, IOException {
        StudentDegree studentDegree = studentDegreeService.getById(studentDegreeId);
        Student student = studentDegree.getStudent();
        List<List<Grade>> grades = gradeService.getGradesByStudentDegreeId(studentDegreeId);
        StudentSummary studentSummary = new StudentSummary(studentDegree, grades);

        String fileName = student.getSurnameEng() + "_" + studentSummary.getStudent().getNameEng();
        WordprocessingMLPackage filledTemplate = supplementTemplateFillService.fill(TEMPLATE, studentSummary);
        return documentIOService.saveDocumentToTemp(filledTemplate, fileName, format);
    }


}
