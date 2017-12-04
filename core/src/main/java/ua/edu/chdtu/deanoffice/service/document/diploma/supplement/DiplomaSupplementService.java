package ua.edu.chdtu.deanoffice.service.document.diploma.supplement;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tr;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Grade;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.service.GradeService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;
import ua.edu.chdtu.deanoffice.service.StudentService;

import java.io.File;
import java.util.*;

import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.*;

@Service
public class DiplomaSupplementService {

    private static final String TEMPLATE = "DiplomaSupplementTemplate.docx";

    private StudentService studentService;
    private GradeService gradeService;
    private StudentGroupService groupService;
    private StudentSummary studentSummary;

    public DiplomaSupplementService(StudentService studentService, GradeService gradeService, StudentGroupService groupService) {
        this.studentService = studentService;
        this.gradeService = gradeService;
        this.groupService = groupService;
    }

    public StudentSummary getStudentSummary() {
        return studentSummary;
    }

    public void setStudentSummary(StudentSummary studentSummary) {
        this.studentSummary = studentSummary;
    }

    public File formDiplomaSupplementForStudent(Integer studentId) {
        Student student = studentService.get(studentId);
        List<List<Grade>> grades = gradeService.getGradesByStudentId(student.getId());
        this.studentSummary = new StudentSummary(student, grades);
        return saveDocument(fillWithStudentInformation(TEMPLATE),
                studentSummary.getStudent().getInitialsUkr() + ".docx");
    }

    public WordprocessingMLPackage fillWithStudentInformation(String templateFilepath) {
        WordprocessingMLPackage template = loadTemplate(templateFilepath);
        Map<String, String> commonDict = new HashMap<>();
        commonDict.putAll(studentSummary.getStudentInfoDictionary());
        commonDict.putAll(studentSummary.getTotalDictionary());
        replacePlaceholders(template, commonDict);

        fillTableWithGrades(template);

        return template;
    }

    private void fillTableWithGrades(WordprocessingMLPackage template) {
        Set<String> placeholdersToRemove = new HashSet<>();

        List<Object> tables = getAllElementFromObject(template.getMainDocumentPart(), Tbl.class);
        Tbl tempTable = findTable(tables, "#CourseNum");
        List<Object> gradeTableRows = getAllElementFromObject(tempTable, Tr.class);

        Tr templateRow = (Tr) gradeTableRows.get(1);
        int rowToAddIndex;

        //The table is filling upwards
        List<List<Grade>> grades = getGradesReverseCopyFromStudentSummary();
        int sectionNumber = 3;

        for (List<Grade> gradesSection : grades) {
            if (sectionNumber > 0) {
                String sectionPlaceholderKey = "#Section" + sectionNumber;
                placeholdersToRemove.add(sectionPlaceholderKey);
                rowToAddIndex = gradeTableRows.indexOf(findRowInTable(tempTable, sectionPlaceholderKey)) + 1;
            } else {
                rowToAddIndex = 2;
            }
            for (Grade grade : gradesSection) {
                Map<String, String> replacements = StudentSummary.getGradeDictionary(grade);
                replacements.put("#CourseNum", getGradeNumberFromBeginning(studentSummary.getGrades(), gradesSection, grade) + "");
                addRowToTable(tempTable, templateRow, rowToAddIndex, replacements);
                rowToAddIndex++;
            }
            sectionNumber--;
        }
        tempTable.getContent().remove(templateRow);
        replacePlaceholdersWithBlank(template, placeholdersToRemove);
    }

    private static int getGradeNumberFromBeginning(List<List<Grade>> masterList, List<Grade> sublist, Object item) {
        int result = 0;
        int sublistIndex = masterList.indexOf(sublist);
        int itemIndex = sublist.indexOf(item);
        for (int i = 0; i <= sublistIndex; i++) {
            if (i == sublistIndex)
                result += itemIndex + 1;
            else result += masterList.get(i).size();
        }
        return result;
    }

    private List<List<Grade>> getGradesReverseCopyFromStudentSummary() {
        List<List<Grade>> grades = new ArrayList<>();
        grades.add(new ArrayList<>());
        grades.add(new ArrayList<>());
        grades.add(new ArrayList<>());
        grades.add(new ArrayList<>());
        Collections.copy(grades, this.studentSummary.getGrades());
        Collections.reverse(grades);
        return grades;
    }

    public File formDiplomaSupplementForGroup(Integer groupId) {
        StudentGroup studentGroup = groupService.getById(groupId);
        if (studentGroup == null || studentGroup.getStudents().isEmpty())
            return null;
        List<Student> students = new ArrayList<>(studentGroup.getStudents());
        WordprocessingMLPackage groupTemplate = null;
        for (Student student : students) {
            this.studentSummary = new StudentSummary(student, gradeService.getGradesByStudentId(student.getId()));
            WordprocessingMLPackage studentFilledTemplate = fillWithStudentInformation(TEMPLATE);
            if (groupTemplate == null) {
                groupTemplate = studentFilledTemplate;
                continue;
            }
            for (Object o : studentFilledTemplate.getMainDocumentPart().getContent()) {
                groupTemplate.getMainDocumentPart().addObject(o);
            }
        }
        return saveDocument(groupTemplate, studentGroup.getName() + ".docx");
    }
}
