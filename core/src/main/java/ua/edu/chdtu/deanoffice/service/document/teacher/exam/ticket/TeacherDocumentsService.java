package ua.edu.chdtu.deanoffice.service.document.teacher.exam.ticket;

import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Tbl;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.Department;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.entity.Teacher;
import ua.edu.chdtu.deanoffice.service.DepartmentService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;
import ua.edu.chdtu.deanoffice.service.TeacherService;
import ua.edu.chdtu.deanoffice.service.course.CourseService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;
import ua.edu.chdtu.deanoffice.service.document.TemplateUtil;
import ua.edu.chdtu.deanoffice.service.document.report.personalstatement.GroupStudentDegreeComparator;
import ua.edu.chdtu.deanoffice.util.PersonUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ua.edu.chdtu.deanoffice.service.document.DocumentIOService.TEMPLATES_PATH;
import static ua.edu.chdtu.deanoffice.util.LanguageUtil.transliterate;

@Service
public class TeacherDocumentsService {
    private static final String TEMPLATE_PATH = TEMPLATES_PATH + "ExamTasks.docx";
    private static final String QUESTIONS_PATH = "/docs/data/kpz.txt";

    private DocumentIOService documentIOService;
    private TeacherService teacherService;
    private CourseService courseService;
    private DepartmentService departmentService;
    private StudentGroupService groupService;

    private String questions[][] = {{"Життєвий цикл розробки програмного забезпечення", "Принцип програмування DRY. Приклади імплементації DRY"},
            {"Що таке конструювання ПЗ", "Набір принципів ООП SOLID"}};

    public TeacherDocumentsService(DocumentIOService documentIOService, TeacherService teacherService, CourseService courseService,
                                   DepartmentService departmentService, StudentGroupService groupService) {
        this.documentIOService = documentIOService;
        this.teacherService = teacherService;
        this.courseService = courseService;
        this.departmentService = departmentService;
        this.groupService = groupService;
    }

    public File generateCourseTicketsDocument(int teacherId, int courseId, int groupId, int departmentId, String protocolNumber, String protocolDate)
            throws Docx4JException, IOException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(TEMPLATE_PATH);
        Teacher teacher = teacherService.getTeacher(teacherId);
        Course course = courseService.getById(courseId);
        Department department = departmentService.getById(departmentId);
        StudentGroup group = groupService.getById(groupId);
        ExamTicketsData etData = new ExamTicketsData(teacher, course, department, group, protocolNumber, protocolDate);
        generateTicketsDoc(template, etData, QUESTIONS_PATH);
        return documentIOService.saveDocumentToTemp(template, transliterate("exam-tickets"), FileFormatEnum.DOCX);
    }

    private void generateTicketsDoc(WordprocessingMLPackage template, ExamTicketsData etData, String filename) throws Docx4JException, IOException {
        List<String> questions = readQuestionsFile(filename);
        String[][] questionBlocks = breakQuestionsIntoBlocks(questions);
        for (int i = 0; i < questionBlocks.length; i++) {
            Map<String, String> commonDict = new HashMap<>();
            commonDict.put("degree", etData.getGroup().getSpecialization().getDegree().getName());
            commonDict.put("speciality", etData.getGroup().getSpecialization().getSpeciality().getCode()+" - "+etData.getGroup().getSpecialization().getSpeciality().getName());
            commonDict.put("education_program", etData.getGroup().getSpecialization().getName());
            commonDict.put("semester", "" + etData.getCourse().getSemester());
            commonDict.put("course_name", etData.getCourse().getCourseName().getName());
            commonDict.put("number", "" + (i + 1));
            commonDict.put("tasks", "1. "+questionBlocks[i][0] + "2. " + questionBlocks[i][1]);
            commonDict.put("department", etData.getDepartment().getName());
            commonDict.put("teacher", "/" + PersonUtil.makeInitialsSurnameFirst(etData.getTeacher().getSurname() + " " + etData.getTeacher().getName() + " " + etData.getTeacher().getPatronimic()));
            commonDict.put("dept_chief", "/Первунінський С.М.");
            commonDict.put("protocol", etData.getProtocolNumber());
            commonDict.put("protocol_date", etData.getProtocolDate());

            if (i != 0) {
                WordprocessingMLPackage templateCopy = documentIOService.loadTemplate(TEMPLATE_PATH);
                TemplateUtil.replaceTextPlaceholdersInTemplate(templateCopy, commonDict);
                template.getMainDocumentPart().getContent().addAll(templateCopy.getMainDocumentPart().getContent());
            } else {
                TemplateUtil.replaceTextPlaceholdersInTemplate(template, commonDict);
            }
        }
//        template.getMainDocumentPart().getContent().remove(0);
    }

    private List<String> readQuestionsFile(String filename) throws IOException {
        List<String> result = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(filename)))) {
            while (br.ready()) {
                result.add(br.readLine());
            }
        }
        return result;
    }

    private String[][] breakQuestionsIntoBlocks(List<String> questions) {
        int n = questions.size() % 2 == 0 ? questions.size() / 2 : questions.size() / 2 + 1;
        String[][] questionBlocks = new String[n][2];
        for (int i = 0; i < n; i++) {
            if (n % 2 == 1 && i == n - 1) {
                questionBlocks[i][0] = questions.get(i);
                questionBlocks[i][1] = "";
            } else {
                questionBlocks[i][0] = questions.get(i);
                questionBlocks[i][1] = questions.get(n/2 + i);
            }
        }
        return questionBlocks;
    }
}
