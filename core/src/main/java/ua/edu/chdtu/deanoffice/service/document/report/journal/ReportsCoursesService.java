package ua.edu.chdtu.deanoffice.service.document.report.journal;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;
import ua.edu.chdtu.deanoffice.service.GradeService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;
import ua.edu.chdtu.deanoffice.service.document.diploma.supplement.DiplomaSupplementService;
import ua.edu.chdtu.deanoffice.util.LanguageUtil;

import java.io.File;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ua.edu.chdtu.deanoffice.service.document.DocumentIOService.TEMPLATES_PATH;
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.addRowToTable;
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.findTable;
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.getAllElementsFromObject;
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.replaceTextPlaceholdersInTemplate;

@Service
public class ReportsCoursesService {

    private static final String TEMPLATE = TEMPLATES_PATH + "PredmJourn.docx";
    private static Logger log = LoggerFactory.getLogger(DiplomaSupplementService.class);

    private GradeService gradeService;
    private StudentGroupService groupService;
    private CourseForGroupService courseForGroupService;
    private DocumentIOService documentIOService;

    public ReportsCoursesService(
            GradeService gradeService,
            StudentGroupService groupService,
            DocumentIOService documentIOService,
            CourseForGroupService courseForGroupService
    ) {
        this.gradeService = gradeService;
        this.groupService = groupService;
        this.courseForGroupService = courseForGroupService;
        this.documentIOService = documentIOService;
    }

    public synchronized File prepareReportForGroup(Integer groupId, Integer semester) throws Docx4JException, IOException {
        List<CourseReport> courseReports = new ArrayList<>();
        StudentGroup group = groupService.getById(groupId);
        List<CourseForGroup> courseForGroups = courseForGroupService.getCoursesForGroupBySemester(groupId, semester);
        Format formatter = new SimpleDateFormat("dd.MM.yyyy");
        courseForGroups.forEach(courseForGroup -> {
            courseReports.add(new CourseReport(courseForGroup.getCourse().getCourseName().getName(),
                    courseForGroup.getCourse().getHours().toString(),
                    courseForGroup.getTeacher() != null ? courseForGroup.getTeacher().getSurname() + " "
                            + courseForGroup.getTeacher().getName().charAt(0) + "."
                            + courseForGroup.getTeacher().getPatronimic().charAt(0) + "." : "",
                    courseForGroup.getExamDate() == null ? "" : formatter.format(courseForGroup.getExamDate())));
        });
        return documentIOService.saveDocumentToTemp(fillTemplate(TEMPLATE, courseReports), LanguageUtil.transliterate(group.getName()) + ".docx", FileFormatEnum.DOCX);
    }

    private WordprocessingMLPackage fillTemplate(String templateName, List<CourseReport> courseReports) throws IOException, Docx4JException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(templateName);
        fillTableWithGrades(template, courseReports);
        Map<String, String> commonDict = new HashMap<>();
        commonDict.put("GroupName", "PZ-154");
        replaceTextPlaceholdersInTemplate(template, commonDict);
        return template;
    }

    private void fillTableWithGrades(WordprocessingMLPackage template, List<CourseReport> courseReports) {
        Tbl tempTable = findTable(template, "#Pred");
        if (tempTable == null) {
            return;
        }
        List<Object> gradeTableRows = getAllElementsFromObject(tempTable, Tr.class);

        Tr templateRow = (Tr) gradeTableRows.get(0);
        int rowToAddIndex = 1;
        for (CourseReport report : courseReports) {
            Map<String, String> replacements = report.getDictionary();
            addRowToTable(tempTable, templateRow, rowToAddIndex, replacements);
            rowToAddIndex++;
        }
        tempTable.getContent().remove(templateRow);
    }


}
