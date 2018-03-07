package ua.edu.chdtu.deanoffice.service.document.report.gradepercentage;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.service.GradeService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.diploma.supplement.DiplomaSupplementService;
import ua.edu.chdtu.deanoffice.util.LanguageUtil;

import java.io.File;
import java.io.IOException;
import java.text.Collator;
import java.util.*;

import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.*;

@Service
public class GradePercentageReportService {

    private static final String TEMPLATES_PATH = "docs/templates/";
    private static final String TEMPLATE = TEMPLATES_PATH + "GradesPercentage.docx";
    private static Logger log = LoggerFactory.getLogger(DiplomaSupplementService.class);

    private GradeService gradeService;
    private StudentGroupService groupService;
    private DocumentIOService documentIOService;

    public GradePercentageReportService(GradeService gradeService, StudentGroupService groupService, DocumentIOService documentIOService) {
        this.gradeService = gradeService;
        this.groupService = groupService;
        this.documentIOService = documentIOService;
    }

    public synchronized File prepareReportForGroup(Integer groupId) throws Docx4JException, IOException {
        List<StudentsReport> studentsReports = new ArrayList<>();
        StudentGroup group = groupService.getStudentGroupById(groupId);
        List<StudentDegree> studentDegrees = new ArrayList<>(group.getStudentDegrees());
        studentDegrees.removeIf(student -> !student.isActive());
        studentDegrees.sort((sd1, sd2) -> {
            Collator ukrainianCollator = Collator.getInstance(new Locale("uk", "UA"));
            return ukrainianCollator.compare(sd1.getStudent().getSurname(), sd2.getStudent().getSurname());
        });
        studentDegrees.forEach(studentDegree -> {
            studentsReports.add(new StudentsReport(studentDegree, gradeService.getAllDifferentiatedGradesByStudentDegreeId(studentDegree.getId())));
        });
        return documentIOService.saveDocumentToTemp(fillTemplate(TEMPLATE, studentsReports), LanguageUtil.transliterate(group.getName()) + ".docx");
    }

    private WordprocessingMLPackage fillTemplate(String templateName, List<StudentsReport> studentsReports) throws IOException, Docx4JException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(templateName);
        fillTableWithGrades(template, studentsReports);
        Map<String, String> commonDict = new HashMap<>();
        commonDict.put("GroupName", studentsReports.get(0).getStudentDegree().getStudentGroup().getName());
        replaceTextPlaceholdersInTemplate(template, commonDict);
        return template;
    }

    private void fillTableWithGrades(WordprocessingMLPackage template, List<StudentsReport> studentsReports) {
        List<Object> tables = getAllElementsFromObject(template.getMainDocumentPart(), Tbl.class);
        String tableWithGradesKey = "№";
        Tbl tempTable = findTable(tables, tableWithGradesKey);
        if (tempTable == null) {
            log.warn("Couldn't find table that contains: " + tableWithGradesKey);
            return;
        }
        List<Object> gradeTableRows = getAllElementsFromObject(tempTable, Tr.class);

        Tr templateRow = (Tr) gradeTableRows.get(1);
        int rowToAddIndex = 1;
        for (StudentsReport report : studentsReports) {
            Map<String, String> replacements = report.getDictionary();
            replacements.put("Num", String.format("%2d", studentsReports.indexOf(report) + 1));
            addRowToTable(tempTable, templateRow, rowToAddIndex, replacements);
            rowToAddIndex++;
        }
        tempTable.getContent().remove(templateRow);
    }


}
