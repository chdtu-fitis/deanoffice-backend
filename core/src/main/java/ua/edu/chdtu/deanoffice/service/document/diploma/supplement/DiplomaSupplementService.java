package ua.edu.chdtu.deanoffice.service.document.diploma.supplement;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tr;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Grade;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.service.GradeService;
import ua.edu.chdtu.deanoffice.service.StudentService;

import java.io.File;
import java.util.*;

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

    private static final String TEMPLATE = "DiplomaSupplementTemplate nc.docx";

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

        fillTableWithGrades(template);

        return saveDocument(template, studentSummary.getStudent().getInitialsUkr() + ".docx");
    }

    private void fillTableWithGrades(WordprocessingMLPackage template) {
        Set<String> placeholdersToRemove = new HashSet<>();

        List<Object> tables = getAllElementFromObject(template.getMainDocumentPart(), Tbl.class);
        Tbl tempTable = findTable(tables, "#CourseNum");
        List<Object> gradeTableRows = getAllElementFromObject(tempTable, Tr.class);

        Tr templateRow = (Tr) gradeTableRows.get(1);
        int rowToAddIndex;

        //The table is filling upwards
        Collections.reverse(studentSummary.getGrades());
        int sectionNumber = 3;
        int courseNumber = studentSummary.getGrades().get(0).size()
                + studentSummary.getGrades().get(1).size()
                + studentSummary.getGrades().get(2).size()
                + studentSummary.getGrades().get(3).size();

        for (List<Grade> gradesSection : studentSummary.getGrades()) {
            if (sectionNumber > 0) {
                String sectionPlaceholderKey = "#Section" + sectionNumber;
                placeholdersToRemove.add(sectionPlaceholderKey);
                rowToAddIndex = gradeTableRows.indexOf(findRowInTable(tempTable, sectionPlaceholderKey)) + 1;
            } else {
                rowToAddIndex = 2;
            }
            for (Grade grade : gradesSection) {
                Map<String, String> replacements = StudentSummary.getGradeDictionary(grade);
                replacements.put("#CourseNum", courseNumber + "");
                courseNumber--;
                addRowToTable(tempTable, templateRow, rowToAddIndex, replacements);
                rowToAddIndex++;
            }
            sectionNumber--;
        }
        tempTable.getContent().remove(templateRow);
        replacePlaceholdersWithBlank(template, placeholdersToRemove);
    }
}
