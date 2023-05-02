package ua.edu.chdtu.deanoffice.service.document.report.exam;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.TemplateUtil;
import ua.edu.chdtu.deanoffice.service.document.report.exam.beans.ExamReportDataBean;
import ua.edu.chdtu.deanoffice.service.document.report.exam.beans.StudentExamReportDataBean;
import ua.edu.chdtu.deanoffice.util.PersonUtil;

import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class ExamReportTemplateFillService extends ExamReportBaseService {

    private static final int STARTING_ROW_INDEX = 7;
    private static final int EXAM_REPORT_STUDENTS_LINES = 57;
    private static final Logger log = LoggerFactory.getLogger(ExamReportTemplateFillService.class);
    private final DocumentIOService documentIOService;

    public ExamReportTemplateFillService(DocumentIOService documentIOService,
                                         CurrentYearService currentYearService) {
        super(currentYearService);
        this.documentIOService = documentIOService;
    }

    WordprocessingMLPackage fillTemplate(String templateName, ExamReportDataBean examReportDataBean)
            throws IOException, Docx4JException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(templateName);
        fillTableWithStudentInitials(template, examReportDataBean.getStudentExamReportDataBeans());
        Map<String, String> commonDict = new HashMap<>();
        commonDict.putAll(getGroupInfoReplacements(examReportDataBean.getGroupExamReportDataBean()));
        commonDict.putAll(getCourseInfoReplacements(examReportDataBean.getCourseExamReportDataBean()));
        TemplateUtil.replaceTextPlaceholdersInTemplate(template, commonDict);
        return template;
    }

    WordprocessingMLPackage fillTemplate(String templateName, List<ExamReportDataBean> examReportDataBeans, int x)
            throws IOException, Docx4JException {
        complementBeansWhenTooManyStudentsInGroup(examReportDataBeans);

        WordprocessingMLPackage reportsDocument = fillTemplate(templateName, examReportDataBeans.get(0));
        examReportDataBeans.remove(0);
        if (examReportDataBeans.size() > 0) {
            examReportDataBeans.forEach(examReportDataBean -> {
                TemplateUtil.addPageBreak(reportsDocument);
                try {
                    reportsDocument.getMainDocumentPart().getContent()
                            .addAll(fillTemplate(templateName, examReportDataBean).getMainDocumentPart().getContent());
                } catch (IOException | Docx4JException e) {
                    e.printStackTrace();
                }
            });
        }
        return reportsDocument;
    }

    private void complementBeansWhenTooManyStudentsInGroup(List<ExamReportDataBean> examReportDataBeans) {
        List<ExamReportDataBean> tooManyStudentsReportData = new ArrayList<>();
        for (ExamReportDataBean erdb: examReportDataBeans) {
            List<StudentExamReportDataBean> studs = erdb.getStudentExamReportDataBeans();
            if (studs.size() > EXAM_REPORT_STUDENTS_LINES) {
                ExamReportDataBean newExamReportDataBean = new ExamReportDataBean(
                        erdb.getCourseExamReportDataBean(),
                        erdb.getGroupExamReportDataBean(),
                        studs.subList(EXAM_REPORT_STUDENTS_LINES, studs.size()));
                tooManyStudentsReportData.add(newExamReportDataBean);
                erdb.setStudentExamReportDataBeans(studs.subList(0, EXAM_REPORT_STUDENTS_LINES));
            }
        }
        examReportDataBeans.addAll(tooManyStudentsReportData);
    }

    private void fillTableWithStudentInitials(WordprocessingMLPackage template, List<StudentExamReportDataBean> students) {
        Tbl tempTable = TemplateUtil.findTable(template, "№");
        if (tempTable == null) {
            return;
        }
        List<Object> gradeTableRows = TemplateUtil.getAllElementsFromObject(tempTable, Tr.class);

        int currentRowIndex = STARTING_ROW_INDEX;
        for (StudentExamReportDataBean student : students) {
            Tr currentRow = (Tr) gradeTableRows.get(currentRowIndex);
            Map<String, String> replacements = new HashMap<>();
            replacements.put("StudentInitials", PersonUtil.getInitials(student.getSurname(), student.getName(), student.getPatronimic()));
            replacements.put("RecBook", student.getRecordBookNumber());
            TemplateUtil.replaceInRow(currentRow, replacements);
            currentRowIndex++;
        }
        removeUnfilledPlaceholders(template);
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

    public void fillTemplate(WordprocessingMLPackage template, CourseForGroup courseForGroup, List<StudentGroup> studentGroups, int numberOfTable,
                             ApplicationUser user)
            throws IOException, Docx4JException {
        int currentRowIndex = STARTING_ROW_INDEX;
        for (StudentGroup studentGroup : studentGroups) {
            fillTableWithStudentInitials(template, studentGroup, numberOfTable, currentRowIndex);
            currentRowIndex += studentGroup.getActiveStudents().size() + 1;
        }
        removeUnfilledPlaceholders(template);
        Map<String, String> commonDict = new HashMap<>();
        commonDict.putAll(getGroupInfoReplacements(studentGroups, user));
        commonDict.putAll(getCourseInfoReplacements(courseForGroup));
        TemplateUtil.replaceTextPlaceholdersInTemplate(template, commonDict);
    }

    private void fillTableWithStudentInitials(WordprocessingMLPackage template, StudentGroup studentGroup, int numberOfTable, int currentRowIndex) {
        Tbl tempTable = TemplateUtil.getAllTablesFromDocument(template).get(numberOfTable);
        if (tempTable == null) {
            return;
        }
        List<Object> gradeTableRows = TemplateUtil.getAllElementsFromObject(tempTable, Tr.class);
        List<Student> students = studentGroup.getActiveStudents();
        sortStudentsByInitials(students);
        Tr currentRow = (Tr) gradeTableRows.get(currentRowIndex);
        currentRowIndex++;
        Map<String, String> replacements = new HashMap<>();
        replacements.put("StudentInitials", studentGroup.getName());
        TemplateUtil.replaceInRow(currentRow, replacements);
        for (Student student : students) {
            currentRow = (Tr) gradeTableRows.get(currentRowIndex);
            replacements.clear();
            replacements.put("StudentInitials", student.getInitialsUkr());
            replacements.put("RecBook", studentGroup.getStudentDegrees().stream().filter(studentDegree ->
                    studentDegree.getStudent().equals(student)).findFirst().get().getRecordBookNumber());
            TemplateUtil.replaceInRow(currentRow, replacements);
            currentRowIndex++;
        }
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
