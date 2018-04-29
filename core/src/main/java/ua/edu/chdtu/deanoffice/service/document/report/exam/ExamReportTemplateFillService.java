package ua.edu.chdtu.deanoffice.service.document.report.exam;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.entity.Speciality;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.TemplateUtil;

import java.io.IOException;
import java.text.Collator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static ua.edu.chdtu.deanoffice.util.PersonUtil.makeInitials;

@Service
class ExamReportTemplateFillService {

    private static final int STARTING_ROW_INDEX = 7;
    private static final Logger log = LoggerFactory.getLogger(ExamReportTemplateFillService.class);
    private final DocumentIOService documentIOService;

    public ExamReportTemplateFillService(DocumentIOService documentIOService) {
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

    private void fillTableWithStudentInitials(WordprocessingMLPackage template, StudentGroup studentGroup) {
        List<Object> tables = TemplateUtil.getAllElementsFromObject(template.getMainDocumentPart(), Tbl.class);
        String tableWithGradesKey = "№";
        Tbl tempTable = TemplateUtil.findTable(tables, tableWithGradesKey);
        if (tempTable == null) {
            log.warn("Couldn't find table that contains: " + tableWithGradesKey);
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

    private Map<String, String> getCourseInfoReplacements(CourseForGroup courseForGroup) {
        Course course = courseForGroup.getCourse();
        Map<String, String> result = new HashMap<>();
        result.put("CourseName", course.getCourseName().getName());
        result.put("Hours", String.format("%d", course.getHours()));

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        if (courseForGroup.getExamDate() != null) {
            result.put("ExamDate", dateFormat.format(courseForGroup.getExamDate()));
        } else {
            result.put("ExamDate", "");
        }
        result.put("Course", String.format("%d", Calendar.getInstance().get(Calendar.YEAR) - courseForGroup.getStudentGroup().getCreationYear()));
        result.put("KCType", course.getKnowledgeControl().getName());
        result.put("TeacherName", courseForGroup.getTeacher().getFullNameUkr());
        result.put("TeacherInitials", courseForGroup.getTeacher().getInitialsUkr());
        result.put("Semester", String.format("%d-й", courseForGroup.getCourse().getSemester()));

        return result;
    }

    private Map<String, String> getGroupInfoReplacements(CourseForGroup courseForGroup) {
        Map<String, String> result = new HashMap<>();
        StudentGroup studentGroup = courseForGroup.getStudentGroup();
        result.put("GroupName", studentGroup.getName());
        Speciality speciality = studentGroup.getSpecialization().getSpeciality();
        result.put("Specialization", speciality.getCode() + " " + speciality.getName());
        result.put("FacultyAbbr", studentGroup.getSpecialization().getDepartment().getFaculty().getAbbr());
        result.put("DeanInitials", makeInitials(studentGroup.getSpecialization().getDepartment().getFaculty().getDean()));
        result.put("Degree", studentGroup.getSpecialization().getDegree().getName());
        result.put("StudyYear", getStudyYear());

        return result;
    }

    private String getStudyYear() {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        if (calendar.get(Calendar.MONTH) >= 9) {
            return String.format("%4d-%4d", currentYear, currentYear + 1);
        } else {
            return String.format("%4d-%4d", currentYear - 1, currentYear);
        }
    }
}
