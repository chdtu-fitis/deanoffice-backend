package ua.edu.chdtu.deanoffice.service.document.statement;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class SingleGroupStatementService {
    //TODO Потрібно прибрати
    private static Logger log = LoggerFactory.getLogger(SingleGroupStatementService.class);
    private static final String TEMPLATES_PATH = "docs/templates/";
    private static final String TEMPLATE = TEMPLATES_PATH + "SingleGroupStatement.docx";

    private DocumentIOService documentIOService;
    private CourseForGroupService courseForGroupService;
    private FillService fillService;

    public SingleGroupStatementService(DocumentIOService documentIOService,
                                       CourseForGroupService courseForGroupService,
                                       FillService fillService) {
        this.documentIOService = documentIOService;
        this.courseForGroupService = courseForGroupService;
        this.fillService = fillService;
    }
    //TODO Важковато читається, можливо потрібно розбити на декілька підметодів
    public File formGroupStatement(Integer groupId, Integer courseId) throws IOException, Docx4JException {
        CourseForGroup courseForGroup = courseForGroupService.getCourseForGroup(groupId, courseId);
        StudentGroup group = courseForGroup.getStudentGroup();
        Course course = courseForGroup.getCourse();
        String fileName = LanguageUtil.transliterate(group.getName() + "_" + course.getCourseName().getNameEng());
        WordprocessingMLPackage filledTemplate = fillService.fillTemplate(TEMPLATE, courseForGroup);
        return documentIOService.saveDocumentToTemp(filledTemplate, fileName + ".docx");
    }

//TODO Загальна рекомендація: Старайся робити менші коміти
}
