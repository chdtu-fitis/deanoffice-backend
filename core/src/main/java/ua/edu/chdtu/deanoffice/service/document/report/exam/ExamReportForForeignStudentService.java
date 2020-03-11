package ua.edu.chdtu.deanoffice.service.document.report.exam;

import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.Constants;
import ua.edu.chdtu.deanoffice.entity.*;
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
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.getAllElementsFromObject;
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.replaceInRow;
import static ua.edu.chdtu.deanoffice.util.PersonUtil.makeInitialsSurnameLast;

@Service
public class ExamReportForForeignStudentService extends ExamReportBaseService {

    private static final String TEMPLATE = TEMPLATES_PATH + "ExamReportForeignStudent.docx";
    private static final int STARTING_ROW_INDEX = 3;
    private final DocumentIOService documentIOService;

    @Autowired
    private CourseForGroupService courseForGroupService;

    @Autowired
    private StudentDegreeService studentDegreeService;

    @Autowired
    private CurrentYearService currentYearService;

    @Autowired
    public ExamReportForForeignStudentService(DocumentIOService documentIOService,
                                         CurrentYearService currentYearService) {
        super(currentYearService);
        this.documentIOService = documentIOService;
    }

    public File createGroupStatementForeign(List<Integer> studentIds, int semester, FileFormatEnum format)
            throws Exception {
        if (studentIds.size() > 0) {
            String fileName = "";
            int i = 0;
            List<StudentDegree> studentDegrees = studentDegreeService.getByIds(studentIds);
            Map<StudentDegree, List<CourseForGroup>> studentsAndCourses = new HashMap<>();
            for(StudentDegree studentDegree : studentDegrees) {
                int groupId = studentDegree.getStudentGroup().getId();
                int facultyId = studentDegree.getSpecialization().getFaculty().getId();
                if (facultyId == Constants.FOREIGN_STUDENTS_FACULTY_ID) {
                    List<CourseForGroup> coursesForGroups = courseForGroupService.getCoursesForGroupBySemester(groupId,semester);
                    studentsAndCourses.put(studentDegree, coursesForGroups);
                }
                if (i < 2) {
                    fileName += LanguageUtil.transliterate(studentDegree.getStudent().getSurname() + studentDegree.getStudentGroup());
                    i++;
                }
            }
            WordprocessingMLPackage filledTemplate = fillTemplate(TEMPLATE, studentsAndCourses);
            return documentIOService.saveDocumentToTemp(filledTemplate, fileName, format);

        } else throw new Exception();
    }

    WordprocessingMLPackage fillTemplate(String templateName, Map<StudentDegree, List<CourseForGroup>> coursesToStudents)
            throws IOException, Docx4JException {
        WordprocessingMLPackage reportsDocument = documentIOService.loadTemplate(templateName);
        int i = 0;
            for (Map.Entry<StudentDegree, List<CourseForGroup>> coursesToStudent : coursesToStudents.entrySet()) {
                TemplateUtil.addPageBreak(reportsDocument);
                try {
                    if (i==0)
                        fillTemplate(reportsDocument, coursesToStudent.getKey(), coursesToStudent.getValue());
                    else
                        reportsDocument.getMainDocumentPart().getContent()
                            .addAll(fillTemplate(templateName, coursesToStudent.getKey(), coursesToStudent.getValue()).getMainDocumentPart().getContent());
                    i++;
                } catch (IOException | Docx4JException e) {
                    e.printStackTrace();
                }
            }
        return reportsDocument;
    }

    WordprocessingMLPackage fillTemplate(String templateName, StudentDegree studentDegree, List<CourseForGroup> coursesToStudent) //генерує для одного студента з багатьма предметами
            throws IOException, Docx4JException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(templateName);
        Map<String, String> commonDict = new HashMap<>();
        commonDict.putAll(getStudentInfoReplacements(studentDegree));

        fillTableWithCoursesInfo(template, coursesToStudent);
        TemplateUtil.replaceTextPlaceholdersInTemplate(template, commonDict);
        return template;
    }

    public void fillTemplate(WordprocessingMLPackage template, StudentDegree studentDegree, List<CourseForGroup> coursesToStudent) // формування інф за 1 студ із предметами
            throws IOException, Docx4JException {
        fillTableWithCoursesInfo(template, coursesToStudent);// заповнить інформацію за предмети
        removeUnfilledPlaceholders(template);
        Map<String, String> commonDict = new HashMap<>();
                commonDict.putAll(getSemester(coursesToStudent.get(0).getCourse().getSemester()));
        commonDict.putAll(getStudentInfoReplacements(studentDegree)); // заповнить інформацію за студента
        TemplateUtil.replaceTextPlaceholdersInTemplate(template, commonDict);
    }

    private void fillTableWithCoursesInfo(WordprocessingMLPackage template, List<CourseForGroup> courses) {//генерує інф по предметам у студента
        Tbl tempTable = TemplateUtil.getAllTablesFromDocument(template).get(1);
        if (tempTable == null) {
            return;
        }
        List<Object> gradeTableRows = getAllElementsFromObject(tempTable, Tr.class);
        int currentRowIndex = STARTING_ROW_INDEX;
        int numberOfRow = 1;
        Tr blanckRow = (Tr) gradeTableRows.get(currentRowIndex);

        for (CourseForGroup courseForGroup : courses) {
            Tr currentRow = XmlUtils.deepCopy(blanckRow);

            Course course = courseForGroup.getCourse();
            Map<String, String> replacements = new HashMap<>();
            replacements.put("n", String.format("" + numberOfRow));
            replacements.put("CourseName", course.getCourseName().getName());
            replacements.put("KCType", course.getKnowledgeControl().getName());
            replacements.put("Hours", String.format("%d", course.getHours()));
            replacements.put("TeacherInitials", courseForGroup.getTeacher().getInitialsUkr());
            replaceInRow(currentRow, replacements);
            tempTable.getContent().add(currentRowIndex, currentRow);
            currentRowIndex++;
            numberOfRow++;
        }
        removeUnfilledPlaceholders(template);
        tempTable.getContent().remove(currentRowIndex);
    }

    Map<String, String> getSemester(int semester) {
        Map<String, String> result = new HashMap<>();
        result.put("Semester", String.format("%d-й", semester));
        return result;
    }

    private void removeUnfilledPlaceholders(WordprocessingMLPackage template) {
        Set<String> placeholdersToRemove = new HashSet<>();
        placeholdersToRemove.add("CourseName");
        placeholdersToRemove.add("KCType");
        placeholdersToRemove.add("Hours");
        placeholdersToRemove.add("TeacherInitials");
        TemplateUtil.replacePlaceholdersWithBlank(template, placeholdersToRemove);
    }

    Map<String, String> getStudentInfoReplacements(StudentDegree studentDegree) {
        Map<String, String> result = new HashMap<>();
        int currentYear = currentYearService.get().getCurrYear();
        result.put("GroupName", studentDegree.getStudentGroup().getName());
        Speciality speciality = studentDegree.getSpecialization().getSpeciality();
        result.put("Speciality", speciality.getCode() + " " + speciality.getName());
        result.put("FAbbr", studentDegree.getSpecialization().getFaculty().getAbbr());
        result.put("DeanInitials", makeInitialsSurnameLast(studentDegree.getSpecialization().getFaculty().getDean()));/////
        result.put("StudyYear", getStudyYear());
        result.put("StudentInitials", studentDegree.getStudent().getFullNameUkr());
        result.put("RecBook", studentDegree.getRecordBookNumber());
        result.put("Course",  String.format("%d", currentYear - studentDegree.getStudentGroup().getCreationYear() + studentDegree.getStudentGroup().getBeginYears()));
        return result;
    }
}

