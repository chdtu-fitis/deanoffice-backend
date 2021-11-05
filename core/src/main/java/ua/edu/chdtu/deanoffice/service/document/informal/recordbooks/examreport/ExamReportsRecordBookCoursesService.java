package ua.edu.chdtu.deanoffice.service.document.informal.recordbooks.examreport;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.Constants;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.entity.TuitionForm;
import ua.edu.chdtu.deanoffice.entity.TuitionTerm;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;
import ua.edu.chdtu.deanoffice.service.document.diploma.supplement.DiplomaSupplementService;
import ua.edu.chdtu.deanoffice.service.document.informal.recordbooks.StudentGroupSpecification;
import ua.edu.chdtu.deanoffice.util.FacultyUtil;
import ua.edu.chdtu.deanoffice.util.GradeUtil;
import ua.edu.chdtu.deanoffice.util.LanguageUtil;

import java.io.File;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.*;

@Service
public class ExamReportsRecordBookCoursesService {

    private static final String TEMPLATES_PATH = "/docs/templates/";
    private static final String TEMPLATE = TEMPLATES_PATH + "ExamReportRecordBookCourses.docx";
    private static final String HEADER_TEMPLATE = TEMPLATES_PATH + "ExamReportRecordBookHeader.docx";
    private static final String FILE_NAME= "jurnal-vidom-";
    private static final String KURS= "-kurs";
    private static Logger log = LoggerFactory.getLogger(DiplomaSupplementService.class);

    private StudentGroupService groupService;
    private CourseForGroupService courseForGroupService;
    private CurrentYearService currentYearService;
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

    public ExamReportsRecordBookCoursesService(StudentGroupService groupService,
                                               DocumentIOService documentIOService,
                                               CourseForGroupService courseForGroupService,
                                               CurrentYearService currentYearService) {
        this.groupService = groupService;
        this.courseForGroupService = courseForGroupService;
        this.documentIOService = documentIOService;
        this.currentYearService = currentYearService;
        formatter = new SimpleDateFormat("dd.MM.yy");
    }

    public synchronized File prepareReportForGroup(Integer groupId, Integer semesterId, int initialNumber) throws Docx4JException, IOException {
        StudentGroup group = groupService.getById(groupId);
        return documentIOService.saveDocumentToTemp(fillTemplate(TEMPLATE,
                prepareGroup(initialNumber, group, semesterId), group.getName()),
                LanguageUtil.transliterate(group.getName())+".docx", FileFormatEnum.DOCX);
    }

    public synchronized File prepareReportForYear(int degreeId,
                                                  int year,
                                                  Integer semester,
                                                  TuitionForm tuitionForm,
                                                  int groupId,
                                                  int initialNumber) throws Docx4JException, IOException {
        int facultyId = FacultyUtil.getUserFacultyIdInt();
        Specification<StudentGroup> specification = StudentGroupSpecification.getStudentGroupsWithImportFilters(
                degreeId, currentYearService.getYear(), year, tuitionForm, facultyId, groupId);
        List<StudentGroup> studentGroups = groupService.getGroupsBySelectionCriteria(specification);
        WordprocessingMLPackage wordMLPackage = fillHeader(HEADER_TEMPLATE, year  + " курс");
        for (StudentGroup group : studentGroups) {
            List<CourseReport> courseReports = prepareGroup(initialNumber, group, semester);
            wordMLPackage.getMainDocumentPart().getContent().addAll(fillTemplate(TEMPLATE, courseReports,
                    group.getName()).getMainDocumentPart().getContent());
            initialNumber += courseReports.size();
        }
        return documentIOService.saveDocumentToTemp(wordMLPackage,FILE_NAME+year+KURS, FileFormatEnum.DOCX);
    }

    private List<CourseReport> prepareGroup(int initialNumber, StudentGroup group, Integer semester) {
        List<CourseReport> courseReports = new ArrayList<>();
        if (group.getTuitionTerm() == TuitionTerm.SHORTENED) {
            semester = semester - (group.getRealBeginYear() - 1) * 2;
        }
        List<CourseForGroup> courseForGroups = courseForGroupService.getCoursesForGroupBySemester(group.getId(), semester);
        courseForGroups.sort(comparator);
        for (CourseForGroup courseForGroup : courseForGroups) {
            courseReports.add(new CourseReport(""+initialNumber++, courseForGroup.getCourse().getCourseName().getName(),
                    fillFieldHours(courseForGroup),
                    GradeUtil.getKCUkrShort(courseForGroup.getCourse().getKnowledgeControl().getId()),
                    courseForGroup.getTeacher() == null ? "" : courseForGroup.getTeacher().getInitialsUkr(),
                    courseForGroup.getExamDate() == null ? "" : formatter.format(courseForGroup.getExamDate())));
        }
        return courseReports;
    }

    private WordprocessingMLPackage fillHeader(String templateName, String studyYear) throws Docx4JException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(templateName);
        Map<String, String> commonDict = new HashMap<>();
        commonDict.put("StudyYear", studyYear);
        replaceTextPlaceholdersInTemplate(template, commonDict);
        return template;
    }

    private WordprocessingMLPackage fillTemplate(String templateName, List<CourseReport> courseReports, String groupName) throws Docx4JException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(templateName);
        fillTableWithCourses(template, courseReports);
        Map<String, String> commonDict = new HashMap<>();
        commonDict.put("GroupName", groupName);
        replaceTextPlaceholdersInTemplate(template, commonDict);
        return template;
    }

    private void fillTableWithCourses(WordprocessingMLPackage template, List<CourseReport> courseReports) {
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
