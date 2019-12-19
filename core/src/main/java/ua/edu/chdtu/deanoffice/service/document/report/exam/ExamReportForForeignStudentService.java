package ua.edu.chdtu.deanoffice.service.document.report.exam;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.Constants;
import ua.edu.chdtu.deanoffice.entity.*;
//import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;
import ua.edu.chdtu.deanoffice.service.document.TemplateUtil;
import ua.edu.chdtu.deanoffice.util.LanguageUtil;


import java.io.File;
import java.io.IOException;
import java.util.*;

import static ua.edu.chdtu.deanoffice.service.document.DocumentIOService.TEMPLATES_PATH;
import static ua.edu.chdtu.deanoffice.util.PersonUtil.makeInitialsSurnameLast;

@Service
public class ExamReportForForeignStudentService extends ExamReportBaseService {

    private static final String TEMPLATE = TEMPLATES_PATH + "dddddd.docx";
    private static final int STARTING_ROW_INDEX = 3;
    private final DocumentIOService documentIOService;

    @Autowired
    private CourseForGroupService courseForGroupService;

    @Autowired
    private StudentDegreeService studentDegreeService;

    /*@Autowired
    private DocumentIOService documentIOService;*/

    /*@Autowired
    private ExamReportBaseService examReportBaseService;*/
    @Autowired
    public ExamReportForForeignStudentService(DocumentIOService documentIOService,
                                         CurrentYearService currentYearService) {
        super(currentYearService);
        this.documentIOService = documentIOService;
    }
//+- іменування файлу
    public File createGroupStatementForeign(List<Integer> studentIds, int semester, FileFormatEnum format)
            throws Exception {
        if (studentIds.size() > 0) {
            List<StudentDegree> studentDegrees = studentDegreeService.getByIds(studentIds);
            Map<StudentDegree, List<CourseForGroup>> studentsAndCourses = new HashMap<>();
            for(StudentDegree studentDegree : studentDegrees) {
                int groupId = studentDegree.getStudentGroup().getId();
                int facultyId = studentDegree.getSpecialization().getFaculty().getId();
                if (facultyId == Constants.FOREIGN_STUDENTS_FACULTY_ID) {
                    List<CourseForGroup> coursesForGroups = courseForGroupService.getCoursesForGroupBySemester(groupId,semester);
                    studentsAndCourses.put(studentDegree, coursesForGroups);
                }
            }
            String fileName = LanguageUtil.transliterate("student");
            WordprocessingMLPackage filledTemplate = fillTemplate(TEMPLATE, studentsAndCourses);
            return documentIOService.saveDocumentToTemp(filledTemplate, fileName, format);

        } else throw new Exception();
    }
//+-
    WordprocessingMLPackage fillTemplate(String templateName, Map<StudentDegree, List<CourseForGroup>> coursesToStudents)
            throws IOException, Docx4JException { //головний метод
        WordprocessingMLPackage reportsDocument = documentIOService.loadTemplate(templateName);
        /*int i = 0;
        if (i == 0 ) {
            reportsDocument = documentIOService.loadTemplate(templateName);
            i++;// виклик самого себе ?
        } else {*/
            for (Map.Entry<StudentDegree, List<CourseForGroup>> coursesToStudent : coursesToStudents.entrySet()) {
                //reportsDocument = fillTemplate(templateName, coursesToStudent.getKey(), coursesToStudent.getValue());
                TemplateUtil.addPageBreak(reportsDocument);
                try {
                    fillTemplate(reportsDocument, coursesToStudent.getKey(), coursesToStudent.getValue(), coursesToStudent.getValue().size());
                    reportsDocument.getMainDocumentPart().getContent()
                            .addAll(fillTemplate(templateName, coursesToStudent.getKey(), coursesToStudent.getValue()).getMainDocumentPart().getContent());
                } catch (IOException | Docx4JException e) {
                    e.printStackTrace();
                }
            }
        //}
        return reportsDocument;
    }
//+
    WordprocessingMLPackage fillTemplate(String templateName, StudentDegree studentDegree, List<CourseForGroup> coursesToStudent) //генерує для одного студента з багатьма предметами
            throws IOException, Docx4JException {////course 0
        WordprocessingMLPackage template = documentIOService.loadTemplate(templateName);
        fillTableWithCoursesInfo(template, coursesToStudent);
        Map<String, String> commonDict = new HashMap<>();
        commonDict.putAll(getStudentInfoReplacements(studentDegree)); //////////
        TemplateUtil.replaceTextPlaceholdersInTemplate(template, commonDict);
        return template;
    }

    private void fillTableWithCoursesInfo(WordprocessingMLPackage template, List<CourseForGroup> courses) {//генерує інф по предметам у студента
        Tbl tempTable = TemplateUtil.findTable(template, "№");
        if (tempTable == null) {
            return;
        }
        List<Object> gradeTableRows = TemplateUtil.getAllElementsFromObject(tempTable, Tr.class);

        int currentRowIndex = STARTING_ROW_INDEX;

        for (CourseForGroup courseForGroup : courses) {
            Tr currentRow = (Tr) gradeTableRows.get(currentRowIndex);
            Course course = courseForGroup.getCourse();
            Map<String, String> replacements = new HashMap<>();
            replacements.put("CourseName", course.getCourseName().getName());
            replacements.put("Semester", String.format("%d-й",courseForGroup.getCourse().getSemester()));/////
            replacements.put("KCType", course.getKnowledgeControl().getName());
            replacements.put("Hours", String.format("%d", course.getHours()));
            replacements.put("TeacherInitials", courseForGroup.getTeacher().getInitialsUkr());
            TemplateUtil.replaceInRow(currentRow, replacements);
            currentRowIndex++;
        }
        removeUnfilledPlaceholders(template);
    }

    private void fillTableWithCoursesInfo(WordprocessingMLPackage template, List<CourseForGroup> coursesToStudent, int numberOfTable, int currentRowIndex) {
        Tbl tempTable = TemplateUtil.getAllTablesFromDocument(template).get(numberOfTable); //
        if (tempTable == null) {
            return;
        }
        List<Object> gradeTableRows = TemplateUtil.getAllElementsFromObject(tempTable, Tr.class);
        Tr currentRow = (Tr) gradeTableRows.get(currentRowIndex);
        currentRowIndex++;
        Map<String, String> replacements = new HashMap<>();
        replacements.put("CourseName", "AAAAAAAAAAAA");////////////
        TemplateUtil.replaceInRow(currentRow, replacements);
        for (CourseForGroup courseForGroup : coursesToStudent) {
            Course course = courseForGroup.getCourse();
            currentRow = (Tr) gradeTableRows.get(currentRowIndex);
            replacements.clear();
            replacements.put("CourseName", course.getCourseName().getName());
            replacements.put("Semester", String.format("%d-й",courseForGroup.getCourse().getSemester()));
            replacements.put("KCType", course.getKnowledgeControl().getName());
            replacements.put("Hours", String.format("%d", course.getHours()));
            replacements.put("TeacherInitials", courseForGroup.getTeacher().getInitialsUkr());

            TemplateUtil.replaceInRow(currentRow, replacements);
            currentRowIndex++;
        }
    }

    public void fillTemplate(WordprocessingMLPackage template, StudentDegree studentDegree, List<CourseForGroup> coursesToStudent, int numberOfTable) // формування інф за 1 студ із предметами
            throws IOException, Docx4JException {
        int currentRowIndex = STARTING_ROW_INDEX;
        //for (CourseForGroup courseForGroup : coursesToStudent) {
            fillTableWithCoursesInfo(template, coursesToStudent, numberOfTable, currentRowIndex);// заповнить інформацію за предмети
            //currentRowIndex += coursesToStudent.size() + 1;
        //}
        removeUnfilledPlaceholders(template);
        Map<String, String> commonDict = new HashMap<>();
        commonDict.putAll(getStudentInfoReplacements(studentDegree)); // заповнить інформацію за студента

        TemplateUtil.replaceTextPlaceholdersInTemplate(template, commonDict);
    }

    private void removeUnfilledPlaceholders(WordprocessingMLPackage template) {
        Set<String> placeholdersToRemove = new HashSet<>();
        placeholdersToRemove.add("CourseName");
        placeholdersToRemove.add("Semester");
        placeholdersToRemove.add("KCType");
        placeholdersToRemove.add("Hours");
        placeholdersToRemove.add("TeacherInitials");
        TemplateUtil.replacePlaceholdersWithBlank(template, placeholdersToRemove);
    }

    Map<String, String> getStudentInfoReplacements(StudentDegree studentDegree) {
        Map<String, String> result = new HashMap<>();
        result.put("GroupName", studentDegree.getStudentGroup().getName());
        Speciality speciality = studentDegree.getSpecialization().getSpeciality();
        result.put("Speciality", speciality.getCode() + " " + speciality.getName());
        result.put("Specialization", studentDegree.getSpecialization().getName());
        result.put("FacultyAbbr", studentDegree.getSpecialization().getFaculty().getAbbr());
        result.put("DeanInitials", makeInitialsSurnameLast(studentDegree.getSpecialization().getFaculty().getDean().toString()));/////
        result.put("StudyYear", getStudyYear());
        result.put("StudentInitials", studentDegree.getStudent().getFullNameUkr());
        result.put("RecBook", studentDegree.getRecordBookNumber());
        result.put("dean", studentDegree.getSpecialization().getFaculty().getDean());

        return result;
    }



}

