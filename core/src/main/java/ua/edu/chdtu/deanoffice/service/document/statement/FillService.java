package ua.edu.chdtu.deanoffice.service.document.statement;
//TODO краще скоротити список імпортів
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;

import java.io.IOException;
import java.text.Collator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.*;

//TODO Занадто неточна та розпливчаста назва, раще замінити
@Service
public class FillService {

    private static final int STARTING_ROW_INDEX = 7;

    private DocumentIOService documentIOService;
    private static Logger log = LoggerFactory.getLogger(FillService.class);

    public FillService(DocumentIOService documentIOService) {
        this.documentIOService = documentIOService;
    }

    public WordprocessingMLPackage fillTemplate(String templateName, CourseForGroup courseForGroup)
            throws IOException, Docx4JException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(templateName);
        fillTableWithStudentInitials(template, courseForGroup.getStudentGroup());
        Map<String, String> commonDict = new HashMap<>();
        commonDict.putAll(getGroupInfoReplacements(courseForGroup));
        commonDict.putAll(getCourseInfoReplacements(courseForGroup));
        replaceTextPlaceholdersInTemplate(template, commonDict);
        return template;
    }

    //TODO Краще розбити на декілька методів. Тіло циклів рекомендується виносити в свій метод, це полегшує читання та розуміння коду
    private void fillTableWithStudentInitials(WordprocessingMLPackage template, StudentGroup studentGroup) {
        List<Object> tables = getAllElementsFromObject(template.getMainDocumentPart(), Tbl.class);
        String tableWithGradesKey = "№";
        Tbl tempTable = findTable(tables, tableWithGradesKey);
        if (tempTable == null) {
            log.warn("Couldn't find table that contains: " + tableWithGradesKey);
            return;
        }
        List<Object> gradeTableRows = getAllElementsFromObject(tempTable, Tr.class);

        int currentRowIndex = STARTING_ROW_INDEX;
        List<Student> students = studentGroup.getActiveStudents();
        students.sort((o1, o2) -> {
            Collator ukrainianCollator = Collator.getInstance(new Locale("uk", "UA"));
            return ukrainianCollator.compare(o1.getInitialsUkr(), o2.getInitialsUkr());
        });
        for (Student student : students) {
            Tr currentRow = (Tr) gradeTableRows.get(currentRowIndex);
            Map<String, String> replacements = new HashMap<>();
            replacements.put("StudentInitials", student.getInitialsUkr());
            replacements.put("RecBook", studentGroup.getStudentDegrees().stream().filter(studentDegree ->
                    studentDegree.getStudent().equals(student)).findFirst().get().getRecordBookNumber());
            replaceInRow(currentRow, replacements);
            currentRowIndex++;
        }
        Set<String> placeholdersToRemove = new HashSet<>();
        placeholdersToRemove.add("#StudentInitials");
        placeholdersToRemove.add("#RecBook");
        replacePlaceholdersWithBlank(template, placeholdersToRemove);
    }

    private Map<String, String> getCourseInfoReplacements(CourseForGroup courseForGroup) {
        Course course = courseForGroup.getCourse();
        Map<String, String> result = new HashMap<>();
        result.put("CourseName", course.getCourseName().getName());
        result.put("Hours", String.format("%d", course.getHours()));

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        result.put("ExamDate", dateFormat.format(courseForGroup.getExamDate()));
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
        result.put("Specialization", studentGroup.getSpecialization().getName());
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

    //TODO краще винести в інший клас, де буде можливість перевикористання цього методу
    private String makeInitials(String fullName) {
        List<String> fullNameParts = Arrays.asList(fullName.split(" "));
        String result = fullNameParts.get(0) + " "
                + fullNameParts.get(1).substring(0, 1).toUpperCase() + ". "
                + fullNameParts.get(2).substring(0, 1).toUpperCase() + ".";
        return result;
    }
}
