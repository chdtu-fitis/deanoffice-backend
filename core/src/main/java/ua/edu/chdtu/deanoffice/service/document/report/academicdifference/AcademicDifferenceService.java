package ua.edu.chdtu.deanoffice.service.document.report.academicdifference;

import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Grade;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static ua.edu.chdtu.deanoffice.service.document.DocumentIOService.TEMPLATES_PATH;
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.*;
import static ua.edu.chdtu.deanoffice.util.LanguageUtil.transliterate;
@Service
public class AcademicDifferenceService {
    private static final String TEMPLATE = TEMPLATES_PATH + "AcademicDifference.docx";
    @Autowired
    private DocumentIOService documentIOService;
    @Autowired
    private StudentDegreeService studentDegreeService;
    public File formDocument(int studentDegreeId) throws Docx4JException, IOException {
        StudentDegree studentDegree = studentDegreeService.getById(studentDegreeId);
        WordprocessingMLPackage resultTemplate = formDocument(TEMPLATE,studentDegree);
        String fileName = transliterate("якась назва");
        return documentIOService.saveDocumentToTemp(resultTemplate, fileName, FileFormatEnum.DOCX);
    }
    private WordprocessingMLPackage formDocument(String templateFilepath, StudentDegree studentDegree)
            throws Docx4JException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(templateFilepath);
        prepareTable(template);
        HashMap<String, String> result = new HashMap();
        result.put("name", studentDegree.getStudent().getInitialsUkr());
        result.put("group", studentDegree.getStudentGroup().getName());
        replaceTextPlaceholdersInTemplate(template,result);
        return template;
    }

    private void prepareTable(WordprocessingMLPackage template) {
        Tbl table = findTable(template, "#num");
        prepareRows(table);
    }

    private void prepareRows(Tbl table) {
        List<Tr> tableRows = (List<Tr>) (Object) getAllElementsFromObject(table, Tr.class);
        Tr rowWithWordSemestr = tableRows.get(1);
        Tr rowWithCourse =  tableRows.get(2);
        table.getContent().remove(2);
        table.getContent().remove(1);
        int row = 1;
        for (int i=1;i<3;i++){
            HashMap<String, String> semestr = new HashMap();
            semestr.put("num",String.valueOf(i));
            Tr newRowWithSignature = XmlUtils.deepCopy(rowWithWordSemestr);
            replaceInRow(newRowWithSignature, semestr);
            table.getContent().add(row,newRowWithSignature);
            row++;
            for (int j=1;j<3;j++){
                HashMap<String, String> courses = new HashMap();
                courses.put("n","Subject");
                courses.put("h","120");
                courses.put("v","Залік");
                Tr newRow = XmlUtils.deepCopy(rowWithCourse);
                replaceInRow(newRow, courses);
                table.getContent().add(row,newRow);
                row++;
            }
        }


    }
}
