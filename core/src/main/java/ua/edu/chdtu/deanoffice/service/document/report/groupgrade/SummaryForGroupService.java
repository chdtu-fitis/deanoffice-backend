package ua.edu.chdtu.deanoffice.service.document.report.groupgrade;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Grade;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.service.GradeService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.diploma.supplement.StudentSummary;
import ua.edu.chdtu.deanoffice.util.LanguageUtil;

import java.io.File;
import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.*;

@Service
public class SummaryForGroupService {

    private static final String TEMPLATES_PATH = "docs/templates/";
    private static final String TEMPLATE = TEMPLATES_PATH + "GradesTable.docx";

    @Autowired
    private DocumentIOService documentIOService;

    @Autowired
    private SummaryForGroupService summaryForGroupService;

    @Autowired
    StudentDegreeService studentDegreeService;

    @Autowired
    StudentGroupService studentGroupService;

    @Autowired
    GradeService gradeService;

    SummaryForGroupService() {

    }

    public File formDocument(Integer groupId, String format)
            throws Docx4JException, IOException {
        List<StudentSummary> studentsSummaries = new ArrayList<>();
        StudentGroup group = studentGroupService.getById(groupId);
        List<StudentDegree> studentDegrees = new ArrayList<>(group.getStudentDegrees());
        studentDegrees.removeIf(student -> !student.isActive());
        studentDegrees.sort((sd1, sd2) -> {
            Collator ukrainianCollator = Collator.getInstance(new Locale("uk", "UA"));
            return ukrainianCollator.compare(sd1.getStudent().getSurname(), sd2.getStudent().getSurname());
        });
        studentDegrees.forEach((studentDegree) -> {
                    List<List<Grade>> grades = gradeService.getGradesByStudentDegreeId(studentDegree.getId());
                    studentsSummaries.add(new StudentSummaryForGroup(studentDegree, grades));
                }
        );

//        StudentSummaryForGroup studentSummary = new StudentSummary(studentDegree, grades);

        WordprocessingMLPackage filledTemplate = fillTemplate(TEMPLATE, studentsSummaries);
        String fileName = LanguageUtil.transliterate(group.getName());
        return documentIOService.saveDocument(filledTemplate, fileName, format);
    }


    public WordprocessingMLPackage fillTemplate(String templateFilepath, List<StudentSummary> studentSummaries)
            throws IOException, Docx4JException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(templateFilepath);
        fillTableWithGrades(template, studentSummaries);
//        Map<String, String> commonDict = getReplacementsDictionary(studentSummaries);
//        replaceTextPlaceholdersInTemplate(template, commonDict);
//        replacePlaceholdersInFooter(template, commonDict);
        prepareTable(template,studentSummaries);
        return template;
    }

    private void fillTableWithGrades(WordprocessingMLPackage template, List<StudentSummary> studentSummaries) {


    }

    private void prepareTable(WordprocessingMLPackage template, List<StudentSummary> studentSummaries) {
        Tbl table = (Tbl) getAllElementsFromObject(template, Tbl.class).get(0);
        List<Tr> tableRows = (List<Tr>) (Object) getAllElementsFromObject(table, Tr.class);
        studentSummaries.forEach((studentSummary) -> {
            cloneLastCellInRow(tableRows.get(0));
        });
    }


}
