package ua.edu.chdtu.deanoffice.service.document.statement;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.util.LanguageUtil;

import java.io.File;
import java.io.IOException;

@Service
public class ExamReportService {
    private static final String TEMPLATES_PATH = "docs/templates/";
    private static final String TEMPLATE = TEMPLATES_PATH + "SingleGroupStatement.docx";

    private final DocumentIOService documentIOService;
    private final CourseForGroupService courseForGroupService;
    private final StatementTemplateFillService examReportTemplateFillService;

    public ExamReportService(DocumentIOService documentIOService,
                             CourseForGroupService courseForGroupService,
                             StatementTemplateFillService examReportTemplateFillService) {
        this.documentIOService = documentIOService;
        this.courseForGroupService = courseForGroupService;
        this.examReportTemplateFillService = examReportTemplateFillService;
    }

    public File createGroupStatement(Integer groupId, Integer courseId, String format)
            throws IOException, Docx4JException {
        CourseForGroup courseForGroup = courseForGroupService.getCourseForGroup(groupId, courseId);
        StudentGroup group = courseForGroup.getStudentGroup();
        Course course = courseForGroup.getCourse();

        String fileName = LanguageUtil.transliterate(group.getName() + "_" + course.getCourseName().getNameEng());
        WordprocessingMLPackage filledTemplate = examReportTemplateFillService.fillTemplate(TEMPLATE, courseForGroup);
        return documentIOService.saveDocument(filledTemplate, fileName, format);
    }
}
