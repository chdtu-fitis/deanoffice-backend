package ua.edu.chdtu.deanoffice.service.document.report.journal;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.Constants;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;
import ua.edu.chdtu.deanoffice.service.document.diploma.supplement.DiplomaSupplementService;
import ua.edu.chdtu.deanoffice.util.LanguageUtil;

import java.io.File;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.*;

@Service
public class ReportsCoursesService {

    private static final String TEMPLATES_PATH = "/docs/templates/";
    private static final String TEMPLATE = TEMPLATES_PATH + "PredmJourn.docx";
    private static final String FILE_NAME= "jurnal-vidom-";
    private static final String KURS= "-kurs";
    private static Logger log = LoggerFactory.getLogger(DiplomaSupplementService.class);

    private StudentGroupService groupService;
    private CourseForGroupService courseForGroupService;
    private DocumentIOService documentIOService;
    private Format formatter;
    private Comparator comparator = new Comparator<CourseForGroup>() {
        @Override
        public int compare(CourseForGroup o1, CourseForGroup o2) {
            if (o1.getExamDate() != null && o2.getExamDate() != null) {
                if (!o1.getExamDate().equals(o2.getExamDate()))
                    if (o1.getExamDate().before(o2.getExamDate()))
                        return -1;
                    else
                        return 1;
                if (o1.getCourse().getKnowledgeControl().getId() != o2.getCourse().getKnowledgeControl().getId())
                    return o1.getCourse().getKnowledgeControl().getId() - o2.getCourse().getKnowledgeControl().getId();
                return o1.getCourse().getCourseName().getName().compareTo(o2.getCourse().getCourseName().getName());
            } else if (o1.getExamDate() != null && o2.getExamDate() == null) {
                return -1;
            } else if (o1.getExamDate() == null && o2.getExamDate() != null) {
                return 1;
            } else if (o1.getExamDate() == null && o2.getExamDate() == null) {
                if (o1.getCourse().getKnowledgeControl().getId() != o2.getCourse().getKnowledgeControl().getId())
                    return o1.getCourse().getKnowledgeControl().getId() - o2.getCourse().getKnowledgeControl().getId();
                else
                    return o1.getCourse().getCourseName().getName().compareTo(o2.getCourse().getCourseName().getName());
            }
            return 0;
        }
    };

    public ReportsCoursesService(StudentGroupService groupService,
                                 DocumentIOService documentIOService,
                                 CourseForGroupService courseForGroupService) {
        this.groupService = groupService;
        this.courseForGroupService = courseForGroupService;
        this.documentIOService = documentIOService;
        formatter = new SimpleDateFormat("dd.MM.yyyy");
    }

    public synchronized File prepareReportForGroup(Integer groupId, Integer semesterId) throws Docx4JException, IOException {
        StudentGroup group = groupService.getById(groupId);
        return documentIOService.saveDocumentToTemp(fillTemplate(TEMPLATE,
                                                    prepareGroup(groupId,semesterId),group.getName()),
                                           LanguageUtil.transliterate(group.getName())+".docx", FileFormatEnum.DOCX);
    }
    public synchronized File prepareReportForYear(Integer degreeId,
                                                  Integer year,
                                                  Integer semesterId,
                                                  Integer facultyId) throws Docx4JException, IOException {
        List<StudentGroup> studentGroups = groupService.getGroupsByDegreeAndYear(degreeId,year,facultyId);
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
        for(StudentGroup groups:studentGroups){
            List<CourseReport> courseReports = prepareGroup(groups.getId(),(int)semesterId);
            if(studentGroups.get(0) == groups){
                 wordMLPackage = fillTemplate(TEMPLATE, courseReports,groups.getName());
            }
            else {
                wordMLPackage.getMainDocumentPart().getContent().addAll(fillTemplate(TEMPLATE,
                                                                        courseReports,
                                                                        groups.getName()).getMainDocumentPart().getContent());

            }
        }
        return documentIOService.saveDocumentToTemp(wordMLPackage,FILE_NAME+year+KURS, FileFormatEnum.DOCX);
    }

    private List<CourseReport> prepareGroup(Integer groupId,Integer semesterId) {
        List<CourseReport> courseReports = new ArrayList<>();
        List<CourseForGroup> courseForGroups = courseForGroupService.getCoursesForGroupBySemester(groupId, semesterId);
        courseForGroups.sort(comparator);
        for(CourseForGroup courseForGroup:courseForGroups){
            courseReports.add(new CourseReport(courseForGroup.getCourse().getCourseName().getName(),
                    fillFieldHours(courseForGroup),
                    courseForGroup.getTeacher() == null ? "" : courseForGroup.getTeacher().getInitialsUkr(),
                    courseForGroup.getExamDate() == null? "" : formatter.format(courseForGroup.getExamDate())));
        }
        return courseReports;
    }

    private WordprocessingMLPackage fillTemplate(String templateName, List<CourseReport> courseReports, String groupName) throws Docx4JException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(templateName);
        fillTableWithGrades(template, courseReports);
        Map<String, String> commonDict = new HashMap<>();
        commonDict.put("GroupName", groupName);
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

    private String fillFieldHours(CourseForGroup courseForGroup) {
        if(courseForGroup.getCourse().getKnowledgeControl().getId() == Constants.COURSEWORK)
            return "КР";
        if(courseForGroup.getCourse().getKnowledgeControl().getId() == Constants.COURSE_PROJECT)
            return "КП";
        return courseForGroup.getCourse().getHours().toString();
    }
}
