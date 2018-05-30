package ua.edu.chdtu.deanoffice.service.document.report.exam;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;
import ua.edu.chdtu.deanoffice.util.LanguageUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static ua.edu.chdtu.deanoffice.service.document.DocumentIOService.TEMPLATES_PATH;

@Service
public class ExamReportService {

    private static final String TEMPLATE = TEMPLATES_PATH + "SingleGroupStatement.docx";

    private final DocumentIOService documentIOService;
    private final CourseForGroupService courseForGroupService;
    private final ExamReportTemplateFillService examReportTemplateFillService;

    public ExamReportService(DocumentIOService documentIOService,
                             CourseForGroupService courseForGroupService,
                             ExamReportTemplateFillService examReportTemplateFillService) {
        this.documentIOService = documentIOService;
        this.courseForGroupService = courseForGroupService;
        this.examReportTemplateFillService = examReportTemplateFillService;
    }

    public File createGroupStatement(Integer groupId, List<Integer> courseIds, FileFormatEnum format)
            throws Exception {
        if (courseIds.size() > 0) {
            List<CourseForGroup> coursesForGroups = new ArrayList<>();
            courseIds.forEach(courseId -> coursesForGroups.add(courseForGroupService.getCourseForGroup(groupId, courseId)));

            String fileName = LanguageUtil.transliterate(coursesForGroups.get(0).getStudentGroup().getName());
            WordprocessingMLPackage filledTemplate = examReportTemplateFillService.fillTemplate(TEMPLATE, coursesForGroups);
            return documentIOService.saveDocumentToTemp(filledTemplate, fileName, format);
        } else throw new Exception();
    }
}
