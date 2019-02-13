package ua.edu.chdtu.deanoffice.service.document.report.academicdifference;

import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.entity.Grade;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.service.GradeService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
    @Autowired
    private GradeService gradeService;
    private Comparator comparator = new Comparator<UnpassedSubject>() {
        @Override
        public int compare(UnpassedSubject u1, UnpassedSubject u2) {
            if (u2.getKnowledgeControl().equals("залік") && u1.getKnowledgeControl().equals("іспит")){
                return -1;
            }
            return 0;
        }
    };
    public File formDocument(int studentDegreeId) throws Docx4JException, IOException {
        StudentDegree studentDegree = studentDegreeService.getById(studentDegreeId);
        WordprocessingMLPackage resultTemplate = formDocument(TEMPLATE,studentDegree);
        String fileName = transliterate("якась назва");
        return documentIOService.saveDocumentToTemp(resultTemplate, fileName, FileFormatEnum.DOCX);
    }
    private WordprocessingMLPackage formDocument(String templateFilepath, StudentDegree studentDegree)
            throws Docx4JException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(templateFilepath);
        prepareTable(template,studentDegree);
        HashMap<String, String> result = new HashMap();
        result.put("name", studentDegree.getStudent().getInitialsUkr());
        result.put("group", studentDegree.getStudentGroup().getName());
        replaceTextPlaceholdersInTemplate(template,result);
        return template;
    }

    private void prepareTable(WordprocessingMLPackage template,StudentDegree studentDegree) {
        Tbl table = findTable(template, "#num");
        Map<Integer,List<UnpassedSubject>> semesters = new HashMap();;
        List<List<Grade>> grades = gradeService.getGradesByStudentDegreeId(studentDegree.getId());
        for(List<Grade> GradesWithСertainKC:grades){
            for(Grade grade: GradesWithСertainKC){
                System.out.println(grade.getCourse().getCourseName().getName()+"|"+grade.getCourse().getKnowledgeControl().getName());
                if (grade.getPoints() == null){
                    int numberSemester = grade.getCourse().getSemester();
                    if (semesters.get(numberSemester) == null){
                        semesters.put(numberSemester,new ArrayList<UnpassedSubject>());
                    }
                    UnpassedSubject unpassedSubject = new UnpassedSubject(
                        grade.getCourse().getCourseName().getName(),
                        grade.getCourse().getHours(),
                        grade.getCourse().getKnowledgeControl().getName());
                    semesters.get(numberSemester).add(unpassedSubject);
                }

            }
        }
        prepareRows(table,semesters);
    }

    private void prepareRows(Tbl table, Map<Integer,List<UnpassedSubject>> semesters) {
        List<Tr> tableRows = (List<Tr>) (Object) getAllElementsFromObject(table, Tr.class);
        Tr rowWithWordSemestr = tableRows.get(1);
        Tr rowWithCourse =  tableRows.get(2);
        table.getContent().remove(2);
        table.getContent().remove(1);
        int row = 1;
        for ( int key:semesters.keySet()){

            HashMap<String, String> title = new HashMap();
            title.put("num",String.valueOf(key));
            Tr newRowWithSignature = XmlUtils.deepCopy(rowWithWordSemestr);
            replaceInRow(newRowWithSignature, title);
            table.getContent().add(row,newRowWithSignature);
            row++;
            semesters.get(key).sort(comparator);
            for (UnpassedSubject unpassedSubject:semesters.get(key)){
                Tr newRow = XmlUtils.deepCopy(rowWithCourse);
                replaceInRow(newRow, unpassedSubject.getDictionary());
                table.getContent().add(row,newRow);
                row++;
            }
        }


    }
}
