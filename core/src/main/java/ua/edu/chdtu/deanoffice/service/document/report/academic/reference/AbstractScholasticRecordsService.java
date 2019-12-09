package ua.edu.chdtu.deanoffice.service.document.report.academic.reference;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.beans.factory.annotation.Autowired;
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
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.replaceTextPlaceholdersInTemplate;
import static ua.edu.chdtu.deanoffice.util.LanguageUtil.transliterate;

@Service
public class AbstractScholasticRecordsService extends AcademicCertificateBaseService{
    private static final String TEMPLATE = TEMPLATES_PATH + "AbstractScholasticRecords.docx";

    @Autowired
    private DocumentIOService documentIOService;

    @Autowired
    private GradeService gradeService;

    @Autowired
    private StudentDegreeService studentDegreeService;

    public File formTranscriptOfRecordsDocument(int studentDegreeId) throws Docx4JException, IOException {
        StudentDegree studentDegree = studentDegreeService.getById(studentDegreeId);
        Student student = studentDegree.getStudent();
        List<List<Grade>> grades = gradeService.getGradesByStudentDegreeId(studentDegree.getId());
        StudentSummaryForAcademicReference studentSummary = new StudentSummaryForAcademicReference(studentDegree, grades);
        WordprocessingMLPackage resultTemplate = formTranscriptOfRecordsDocument(TEMPLATE, studentSummary);
        String fileName = transliterate(student.getName() + " " + student.getSurname());
        return documentIOService.saveDocumentToTemp(resultTemplate, fileName, FileFormatEnum.DOCX);
    }

    private WordprocessingMLPackage formTranscriptOfRecordsDocument(String templateFilepath, StudentSummaryForAcademicReference studentSummary)
            throws Docx4JException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(templateFilepath);
        prepareTable(template, studentSummary);
        replaceTextPlaceholdersInTemplate(template, getStudentInfoDictionary(studentSummary));
        return template;
    }
}
