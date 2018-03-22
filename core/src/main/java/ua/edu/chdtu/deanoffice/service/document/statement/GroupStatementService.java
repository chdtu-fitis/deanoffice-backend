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
public class GroupStatementService {
    private static final String TEMPLATES_PATH = "docs/templates/";
    private static final String TEMPLATE = TEMPLATES_PATH + "SingleGroupStatement.docx";

    private final DocumentIOService documentIOService;
    private final CourseForGroupService courseForGroupService;
    private final StatementTemplateFillService statementTemplateFillService;

    public GroupStatementService(DocumentIOService documentIOService,
                                 CourseForGroupService courseForGroupService,
                                 StatementTemplateFillService statementTemplateFillService) {
        this.documentIOService = documentIOService;
        this.courseForGroupService = courseForGroupService;
        this.statementTemplateFillService = statementTemplateFillService;
    }

    public File createGroupStatement(Integer groupId, Integer courseId)
            throws IOException, Docx4JException, NullPointerException {
        CourseForGroup courseForGroup = courseForGroupService.getCourseForGroup(groupId, courseId);
        StudentGroup group = courseForGroup.getStudentGroup();
        Course course = courseForGroup.getCourse();

        String fileName = LanguageUtil.transliterate(group.getName() + "_" + course.getCourseName().getNameEng());
        WordprocessingMLPackage filledTemplate = statementTemplateFillService.fillTemplate(TEMPLATE, courseForGroup);
        return documentIOService.saveDocumentToTemp(filledTemplate, fileName + ".docx");
    }
}
