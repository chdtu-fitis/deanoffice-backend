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
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;
import ua.edu.chdtu.deanoffice.service.document.TemplateUtil;
import ua.edu.chdtu.deanoffice.service.document.diploma.supplement.DiplomaSupplementService;
import ua.edu.chdtu.deanoffice.util.LanguageUtil;

import java.io.File;
import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static ua.edu.chdtu.deanoffice.service.document.DocumentIOService.TEMPLATES_PATH;


@Service
public class GradePercentageReportService {

    private static final String TEMPLATE = TEMPLATES_PATH + "GradesPercentage.docx";
    private static final Logger log = LoggerFactory.getLogger(DiplomaSupplementService.class);

    private final GradeService gradeService;
    private final StudentGroupService groupService;
    private final DocumentIOService documentIOService;

    public GradePercentageReportService(GradeService gradeService, StudentGroupService groupService, DocumentIOService documentIOService) {
        this.gradeService = gradeService;
        this.groupService = groupService;
        this.documentIOService = documentIOService;
    }

    public File prepareReportForGroup(Integer groupId, FileFormatEnum format)
            throws Docx4JException, IOException {
        List<StudentsReport> studentsReports = new ArrayList<>();
        StudentGroup group = groupService.getById(groupId);
        List<StudentDegree> studentDegrees = new ArrayList<>(group.getStudentDegrees());
        studentDegrees.removeIf(student -> !student.isActive());
        studentDegrees.sort((sd1, sd2) -> {
            Collator ukrainianCollator = Collator.getInstance(new Locale("uk", "UA"));
            return ukrainianCollator.compare(sd1.getStudent().getSurname(), sd2.getStudent().getSurname());
        });
        studentDegrees.forEach(studentDegree ->
                studentsReports.add(new StudentsReport(studentDegree, gradeService.getAllDifferentiatedGrades(studentDegree.getId())))
        );

        WordprocessingMLPackage filledTemplate = fillTemplate(TEMPLATE, studentsReports);
        String fileName = LanguageUtil.transliterate(group.getName());
        return documentIOService.saveDocumentToTemp(filledTemplate, fileName, format);
    }

    private WordprocessingMLPackage fillTemplate(String templateName,
                                                 List<StudentsReport> studentsReports)
            throws IOException, Docx4JException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(templateName);
        fillTableWithGrades(template, studentsReports);
        Map<String, String> commonDict = new HashMap<>();
        commonDict.put("GroupName", studentsReports.get(0).getStudentDegree().getStudentGroup().getName());
        TemplateUtil.replaceTextPlaceholdersInTemplate(template, commonDict);
        return template;
    }

    private void fillTableWithGrades(WordprocessingMLPackage template, List<StudentsReport> studentsReports) {
        Tbl tempTable = TemplateUtil.findTable(template, "№");
        if (tempTable == null) {
            return;
        }
        List<Object> gradeTableRows = TemplateUtil.getAllElementsFromObject(tempTable, Tr.class);

        Tr templateRow = (Tr) gradeTableRows.get(1);
        int rowToAddIndex = 1;
        for (StudentsReport report : studentsReports) {
            Map<String, String> replacements = report.getDictionary();
            replacements.put("Num", String.format("%2d", studentsReports.indexOf(report) + 1));
            TemplateUtil.addRowToTable(tempTable, templateRow, rowToAddIndex, replacements);
            rowToAddIndex++;
        }
        tempTable.getContent().remove(templateRow);
    }


}
