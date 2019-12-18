package ua.edu.chdtu.deanoffice.service.document.report.exam;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tr;
import org.springframework.beans.factory.annotation.Autowired;
import ua.edu.chdtu.deanoffice.Constants;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ua.edu.chdtu.deanoffice.service.document.DocumentIOService.TEMPLATES_PATH;
import static ua.edu.chdtu.deanoffice.util.PersonUtil.makeInitialsSurnameLast;

public class ExamReportForForeignStudentService extends ExamReportBaseService {

    private static final String TEMPLATE = TEMPLATES_PATH + "abcd.docx";
    private static final int STARTING_ROW_INDEX = 7;
    private final DocumentIOService documentIOService;

    @Autowired
    private CourseForGroupService courseForGroupService;

    @Autowired
    private StudentDegreeService studentDegreeService;

    /*@Autowired
    private DocumentIOService documentIOService;*/

    @Autowired
    private ExamReportBaseService examReportBaseService;

    public ExamReportForForeignStudentService(DocumentIOService documentIOService,
                                         CurrentYearService currentYearService) {
        super(currentYearService);
        this.documentIOService = documentIOService;
    }

    public File createGroupStatementForeign(List<Integer> studentId, Integer semester, FileFormatEnum format)
            throws Exception {
        if (studentId.size() > 0) {
            List<StudentDegree> studentDegrees = studentDegreeService.getByIds(studentId);
            Map<StudentDegree, List<CourseForGroup>> studentsAndCourses = new HashMap<StudentDegree,List<CourseForGroup>>();
            for(StudentDegree studentDegree : studentDegrees) {
                int groupId = studentDegree.getStudentGroup().getId();
                int facultyId = studentDegree.getSpecialization().getFaculty().getId();
                if (facultyId == Constants.FOREIGN_STUDENTS_FACULTY_ID) {
                    List<CourseForGroup> coursesForGroups = courseForGroupService.getCoursesForGroupBySemester(groupId, semester);
                    studentsAndCourses.put(studentDegree, coursesForGroups);
                }
            }

            String fileName = LanguageUtil.transliterate("student");
            WordprocessingMLPackage filledTemplate = fillTemplate(TEMPLATE, studentsAndCourses);
            return documentIOService.saveDocumentToTemp(filledTemplate, fileName, format);

        } else throw new Exception();
    }

    WordprocessingMLPackage fillTemplate(String templateName, Map<StudentDegree, List<CourseForGroup>> studentsAndCourses)
            throws IOException, Docx4JException {
        WordprocessingMLPackage reportsDocument = null;
        int i = 0;
        if (i > 0 ) {
            for (Map.Entry<StudentDegree, List<CourseForGroup>> studentAndCourses : studentsAndCourses.entrySet()) {
                reportsDocument = fillTemplate(templateName, studentAndCourses.getKey(), studentAndCourses.getValue());
                TemplateUtil.addPageBreak(reportsDocument);
                try {
                    reportsDocument.getMainDocumentPart().getContent()
                            .addAll(fillTemplate(templateName, studentsAndCourses).getMainDocumentPart().getContent());
                } catch (IOException | Docx4JException e) {
                    e.printStackTrace();
                }
            }
        } else {

        }

        return reportsDocument;
    }

    WordprocessingMLPackage fillTemplate(String templateName, StudentDegree studentDegree, List<CourseForGroup> studentsAndCourses) //генерує для одного
            throws IOException, Docx4JException {
        int currentRowIndex = STARTING_ROW_INDEX;
        WordprocessingMLPackage template = documentIOService.loadTemplate(TEMPLATE);
        Map<String, String> commonDict = new HashMap<>();
        for(CourseForGroup courseForGroup : studentsAndCourses) { //// цикл щоб пройти по всім предметам даного студента
            commonDict.putAll(getGroupInfoReplacements(courseForGroup));////////
            fillTableWithCourses(template, courseForGroup, 8, currentRowIndex);
            currentRowIndex +=  1;
        }


        TemplateUtil.replaceTextPlaceholdersInTemplate(template, commonDict);
        return template;
    }

    private void fillTableWithCourses(WordprocessingMLPackage template, CourseForGroup courseForGroup, int numberOfTable, int currentRowIndex) {
        Tbl tempTable = TemplateUtil.getAllTablesFromDocument(template).get(numberOfTable);
        if (tempTable == null) {
            return;
        }
        List<Object> gradeTableRows = TemplateUtil.getAllElementsFromObject(tempTable, Tr.class);

        Tr currentRow = (Tr) gradeTableRows.get(currentRowIndex);
        currentRowIndex++;
        Map<String, String> replacements = new HashMap<>();
            replacements.put("Course name", courseForGroup.getCourse().toString());
            TemplateUtil.replaceInRow(currentRow, replacements);
            currentRow = (Tr) gradeTableRows.get(currentRowIndex);
            replacements.clear();
            TemplateUtil.replaceInRow(currentRow, replacements);
            currentRowIndex++;
    }

    /*Map<String, String> getGroupInfoReplacements(CourseForGroup courseForGroup) {
        Map<String, String> result = new HashMap<>();
        StudentGroup studentGroup = courseForGroup.getStudentGroup();
        result.put("GroupName", studentGroup.getName());
        Speciality speciality = studentGroup.getSpecialization().getSpeciality();
        result.put("Speciality", speciality.getCode() + " " + speciality.getName());
        result.put("Specialization", studentGroup.getSpecialization().getName());
        result.put("FacultyAbbr", studentGroup.getSpecialization().getFaculty().getAbbr());
        result.put("DeanInitials", makeInitialsSurnameLast(studentGroup.getSpecialization().getFaculty().getDean()));/////
        result.put("Degree", studentGroup.getSpecialization().getDegree().getName());///////
        result.put("StudyYear", getStudyYear());

        return result;
    }*/



}

