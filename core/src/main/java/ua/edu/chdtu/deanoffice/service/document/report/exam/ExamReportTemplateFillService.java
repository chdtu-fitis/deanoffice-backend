package ua.edu.chdtu.deanoffice.service.document.report.exam;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.TemplateUtil;

import java.io.IOException;
import java.text.Collator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
class ExamReportTemplateFillService extends ExamReportBaseService {

    private static final int STARTING_ROW_INDEX = 7;
    private static final Logger log = LoggerFactory.getLogger(ExamReportTemplateFillService.class);
    private final DocumentIOService documentIOService;

    public ExamReportTemplateFillService(DocumentIOService documentIOService,
                                         CurrentYearService currentYearService) {
        super(currentYearService);
        this.documentIOService = documentIOService;
    }

    WordprocessingMLPackage fillTemplate(String templateName, CourseForGroup courseForGroup)
            throws IOException, Docx4JException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(templateName);
        fillTableWithStudentInitials(template, courseForGroup.getStudentGroup());
        Map<String, String> commonDict = new HashMap<>();
        commonDict.putAll(getGroupInfoReplacements(courseForGroup));
        commonDict.putAll(getCourseInfoReplacements(courseForGroup));
        TemplateUtil.replaceTextPlaceholdersInTemplate(template, commonDict);
        return template;
    }

    WordprocessingMLPackage fillTemplate(String templateName, List<CourseForGroup> coursesForGroups)
            throws IOException, Docx4JException {
        WordprocessingMLPackage reportsDocument = fillTemplate(templateName, coursesForGroups.get(0));
        coursesForGroups.remove(0);
        if (coursesForGroups.size() > 0) {
            coursesForGroups.forEach(courseForGroup -> {
                TemplateUtil.addPageBreak(reportsDocument);
                try {
                    reportsDocument.getMainDocumentPart().getContent()
                            .addAll(fillTemplate(templateName, courseForGroup).getMainDocumentPart().getContent());
                } catch (IOException | Docx4JException e) {
                    e.printStackTrace();
                }
            });
        }
        return reportsDocument;
    }

    private void fillTableWithStudentInitials(WordprocessingMLPackage template, StudentGroup studentGroup) {
        Tbl tempTable = TemplateUtil.findTable(template, "№");
        if (tempTable == null) {
            return;
        }
        List<Object> gradeTableRows = TemplateUtil.getAllElementsFromObject(tempTable, Tr.class);

        int currentRowIndex = STARTING_ROW_INDEX;
        List<Student> students = studentGroup.getActiveStudents();
        sortStudentsByInitials(students);
        for (Student student : students) {
            Tr currentRow = (Tr) gradeTableRows.get(currentRowIndex);
            Map<String, String> replacements = new HashMap<>();
            replacements.put("StudentInitials", student.getInitialsUkr());
            replacements.put("RecBook", studentGroup.getStudentDegrees().stream().filter(studentDegree ->
                    studentDegree.getStudent().equals(student)).findFirst().get().getRecordBookNumber());
            TemplateUtil.replaceInRow(currentRow, replacements);
            currentRowIndex++;
        }
        removeUnfilledPlaceholders(template);
    }

    private void sortStudentsByInitials(List<Student> students) {
        students.sort((o1, o2) -> {
            Collator ukrainianCollator = Collator.getInstance(new Locale("uk", "UA"));
            return ukrainianCollator.compare(o1.getInitialsUkr(), o2.getInitialsUkr());
        });
    }

    private void removeUnfilledPlaceholders(WordprocessingMLPackage template) {
        Set<String> placeholdersToRemove = new HashSet<>();
        placeholdersToRemove.add("#StudentInitials");
        placeholdersToRemove.add("#RecBook");
        TemplateUtil.replacePlaceholdersWithBlank(template, placeholdersToRemove);
    }
}
