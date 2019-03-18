package ua.edu.chdtu.deanoffice.service.document.report.academicdifference;

import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Grade;
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
    private static final int ROW_SECOND = 1;
    private static final int ROW_THIRD = 2;
    @Autowired
    private DocumentIOService documentIOService;
    @Autowired
    private StudentDegreeService studentDegreeService;
    @Autowired
    private GradeService gradeService;
    private Comparator comparator = new Comparator<UnpassedCourse>() {
        @Override
        public int compare(UnpassedCourse u1, UnpassedCourse u2) {
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
        Map<Integer,List<UnpassedCourse>> semesters = new HashMap();;
        List<List<Grade>> grades = gradeService.getGradesByStudentDegreeId(studentDegree.getId());
        for(List<Grade> GradesWithСertainKC:grades){
            for(Grade grade: GradesWithСertainKC){
                if (grade.getPoints() == null){
                    int numberSemester = grade.getCourse().getSemester();
                    if (semesters.get(numberSemester) == null){
                        semesters.put(numberSemester,new ArrayList<UnpassedCourse>());
                    }
                    UnpassedCourse unpassedCourse = new UnpassedCourse(
                        grade.getCourse().getCourseName().getName(),
                        grade.getCourse().getHours(),
                        grade.getCourse().getKnowledgeControl().getName());
                    semesters.get(numberSemester).add(unpassedCourse);
                }
            }
        }
        prepareRows(table,semesters);
    }

    private void prepareRows(Tbl table, Map<Integer,List<UnpassedCourse>> semesters) {
        List<Tr> tableRows = (List<Tr>) (Object) getAllElementsFromObject(table, Tr.class);
        Tr rowWithWordSemester = tableRows.get(ROW_SECOND);
        Tr rowWithUnpassedCourse =  tableRows.get(ROW_THIRD);
        table.getContent().remove(ROW_THIRD);
        table.getContent().remove(ROW_SECOND);
        int row = ROW_SECOND;
        for ( int key:semesters.keySet()){
            HashMap<String, String> title = new HashMap();
            title.put("num",String.valueOf(key));
            Tr newRowWithWordSemester = XmlUtils.deepCopy(rowWithWordSemester);
            replaceInRow(newRowWithWordSemester, title);
            table.getContent().add(row,newRowWithWordSemester);
            row++;
            semesters.get(key).sort(comparator);
            for (UnpassedCourse unpassedCourse :semesters.get(key)){
                Tr newRow = XmlUtils.deepCopy(rowWithUnpassedCourse);
                replaceInRow(newRow, unpassedCourse.getDictionary());
                table.getContent().add(row,newRow);
                row++;
            }
        }
    }
}
