package ua.edu.chdtu.deanoffice.service.document.report.gradepercentage;

import lombok.Getter;
import lombok.Setter;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.service.GradeService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;
import ua.edu.chdtu.deanoffice.service.document.diploma.supplement.DiplomaSupplementService;
import ua.edu.chdtu.deanoffice.util.LanguageUtil;

import java.io.File;
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

    @Getter
    @Setter
    private List<StudentsReport> studentsReports;

    public GradePercentageReportService(GradeService gradeService, StudentGroupService groupService) {
        this.gradeService = gradeService;
        this.groupService = groupService;
    }

    public synchronized File prepareReportForGroup(Integer groupId) {
        studentsReports = new ArrayList<>();
        StudentGroup group = groupService.getById(groupId);
        List<Student> students = new ArrayList<>(group.getStudents());
        students.sort((s1, s2) -> {
            Collator ukrainianCollator = Collator.getInstance(new Locale("uk", "UA"));
            return ukrainianCollator.compare(s1.getSurname(), s2.getSurname());
        });
        students.forEach(student -> {
            studentsReports.add(new StudentsReport(gradeService.getAllDifferentiatedGradesByStudentId(student.getId())));
        });
        return saveDocument(fillTemplate(), LanguageUtil.transliterate(group.getName()) + ".docx");
    }

    private WordprocessingMLPackage fillTemplate() {
        WordprocessingMLPackage template = loadTemplate(TEMPLATE);
        fillTableWithGrades(template);
        Map<String, String> commonDict = new HashMap<>();
        commonDict.put("#GroupName", studentsReports.get(0).getStudent().getStudentGroup().getName());
        replaceTextPlaceholdersInTemplate(template, commonDict);
        return template;
    }

    private void fillTableWithGrades(WordprocessingMLPackage template) {
        Set<String> placeholdersToRemove = new HashSet<>();

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
            replacements.put("#Num", String.format("%2d", this.studentsReports.indexOf(report) + 1));
            addRowToTable(tempTable, templateRow, rowToAddIndex, replacements);
            rowToAddIndex++;
        }
        tempTable.getContent().remove(templateRow);
        replacePlaceholdersWithBlank(template, placeholdersToRemove);
    }


}
