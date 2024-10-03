package ua.edu.chdtu.deanoffice.service.document.diploma.supplement;

import com.google.common.base.Strings;
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
import ua.edu.chdtu.deanoffice.service.document.TemplateUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ua.edu.chdtu.deanoffice.service.document.DocumentIOService.TEMPLATES_PATH;

@Service
public class DiplomaSupplementService {
//    private static final String STATE_EXAM = "Кваліфікаційний іспит.";

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
        List<List<Grade>> grades = gradeService.getGradesByStudentDegreeIdWithSelective(studentDegreeId);
        List<List<Grade>> recreditedGrades = gradeService.getRecreditedGradesByStudentDegreeId(studentDegreeId);
        StudentSummary studentSummary = new StudentSummary(studentDegree, grades, recreditedGrades);
        complementQualificationWorkDescription(studentSummary);

        String fileName = student.getSurnameEng() + "_" + studentSummary.getStudent().getNameEng();
        WordprocessingMLPackage filledTemplate = supplementTemplateFillService.fill(TEMPLATE, studentSummary);
        return documentIOService.saveDocumentToTemp(filledTemplate, fileName, format);
    }

    private void complementQualificationWorkDescription(StudentSummary studentSummary) {
        if (studentSummary.getGrades().get(StudentSummary.QUALIFICATION_SECTION_NUMBER_IN_GRADES) == null)
            return;

        studentSummary.getGrades().get(StudentSummary.QUALIFICATION_SECTION_NUMBER_IN_GRADES).stream().forEach(grade -> {
            String courseNameUkr = grade.getCourse().getCourseName().getName();
            if (!Strings.isNullOrEmpty(courseNameUkr) && (!courseNameUkr.contains("іспит") && !courseNameUkr.contains("екзамен"))) {
                String degreeName = "";
                String degreeNameEng = "";
                switch (studentSummary.getStudentGroup().getSpecialization().getDegree().getId()) {
                    case 1: {
                        degreeName = "бакалавра";
                        degreeNameEng = "bachelor's";
                        break;
                    }
                    case 2: {
                        degreeName = "спеціаліста";
                        degreeNameEng = "specialists's";
                        break;
                    }

                    case 3: {
                        degreeName = "магістра";
                        degreeNameEng = "master's";
                        break;
                    }
                }
                grade.getCourse().getCourseName()
                        .setName("Кваліфікаційна робота " + degreeName + " на тему: «" + TemplateUtil.getValueSafely(grade.getStudentDegree().getThesisName()) + "»");
                grade.getCourse().getCourseName()
                        .setNameEng("Qualification work of a " + degreeNameEng + " degree on a subject: \"" + TemplateUtil.getValueSafely(grade.getStudentDegree().getThesisNameEng()) + "\"");
            }
        });
    }
}
