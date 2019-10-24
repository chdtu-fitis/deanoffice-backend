package ua.edu.chdtu.deanoffice.service.document.report.academic.reference;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Grade;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentExpel;
import ua.edu.chdtu.deanoffice.service.GradeService;
import ua.edu.chdtu.deanoffice.service.StudentExpelService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static ua.edu.chdtu.deanoffice.service.document.DocumentIOService.TEMPLATES_PATH;
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.*;
import static ua.edu.chdtu.deanoffice.util.LanguageUtil.transliterate;

@Service
public class AcademicReferenceService extends AcademicCertificateBaseService {

    @Autowired
    private DocumentIOService documentIOService;

    @Autowired
    private GradeService gradeService;

    @Autowired
    private StudentExpelService studentExpelService;

    private static final String TEMPLATE = TEMPLATES_PATH + "AcademicCertificate.docx";

    public File formDocument(int studentExpelId) throws Docx4JException, IOException {
        StudentExpel studentExpel = studentExpelService.getById(studentExpelId);
        StudentDegree studentDegree = studentExpel.getStudentDegree();
        Student student = studentDegree.getStudent();
        List<List<Grade>> grades = gradeService.getGradesByStudentDegreeId(studentDegree.getId());
        StudentSummaryForAcademicReference studentSummary = new StudentSummaryForAcademicReference(studentDegree, grades);
        WordprocessingMLPackage resultTemplate = formDocument(TEMPLATE, studentSummary, studentExpel);
        String fileName = transliterate(student.getName() + " " + student.getSurname());
        return documentIOService.saveDocumentToTemp(resultTemplate, fileName, FileFormatEnum.DOCX);
    }

    private WordprocessingMLPackage formDocument(String templateFilepath, StudentSummaryForAcademicReference studentSummary, StudentExpel studentExpel)
            throws Docx4JException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(templateFilepath);
        prepareTable(template, studentSummary);
        replaceTextPlaceholdersInTemplate(template, getStudentInfoDictionary(studentSummary));
        replaceTextPlaceholdersInTemplate(template, getStudentExpelInfoDictionary(studentExpel));
        return template;
    }

    private HashMap<String, String> getStudentExpelInfoDictionary(StudentExpel studentExpel){
        HashMap<String, String> result = new HashMap();
        result.put("endStudy", formatDate(studentExpel.getExpelDate()));
        result.put("expelReasonUkr", studentExpel.getOrderReason().getName());
        result.put("expelReasonEng", studentExpel.getOrderReason().getNameEng());
        result.put("orderUkr", " від "+formatDate(studentExpel.getOrderDate())+" № "+studentExpel.getOrderNumber());
        result.put("orderEng", formatDate(studentExpel.getOrderDate())+", № "+studentExpel.getOrderNumber());
        return result;
    }


}
