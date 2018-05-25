package ua.edu.chdtu.deanoffice.service.document.report.exam;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.service.CourseService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;
import ua.edu.chdtu.deanoffice.util.LanguageUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ua.edu.chdtu.deanoffice.service.document.DocumentIOService.TEMPLATES_PATH;

@Service
public class MultiGroupExamReportService {

    private static final String TEMPLATE = TEMPLATES_PATH + "MultiGroupExamReport.docx";

    private DocumentIOService documentIOService;
    private StudentGroupService studentGroupService;
    private MultiGroupReportTemplateFillService templateFillService;
    private CourseService courseService;

    public MultiGroupExamReportService(DocumentIOService documentIOService,
                                       StudentGroupService studentGroupService,
                                       MultiGroupReportTemplateFillService templateFillService,
                                       CourseService courseService) {
        this.documentIOService = documentIOService;
        this.studentGroupService = studentGroupService;
        this.templateFillService = templateFillService;
        this.courseService = courseService;
    }

    public File prepareReport(List<Integer> groupIds, Integer courseId, FileFormatEnum format)
            throws IOException, Docx4JException {
        Course course = courseService.getById(courseId);
        List<StudentGroup> groups = new ArrayList<>();
        groupIds.forEach(id -> groups.add(studentGroupService.getById(id)));

        String fileName = "Зведена_";
        for (StudentGroup group : groups) {
            fileName += group.getName() + "_";
        }
        fileName = fileName.substring(0, fileName.length() - 2);
        fileName = LanguageUtil.transliterate(fileName);
        WordprocessingMLPackage filledTemplate = templateFillService.fillTemplate(TEMPLATE, groups, course);
        return documentIOService.saveDocumentToTemp(filledTemplate, fileName, format);
    }
}
