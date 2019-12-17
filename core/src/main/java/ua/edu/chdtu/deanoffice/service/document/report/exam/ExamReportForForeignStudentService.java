package ua.edu.chdtu.deanoffice.service.document.report.exam;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ua.edu.chdtu.deanoffice.service.document.DocumentIOService.TEMPLATES_PATH;
import static ua.edu.chdtu.deanoffice.util.PersonUtil.makeInitialsSurnameLast;

public class ExamReportForForeignStudentService extends ExamReportBaseService {

    private static final String TEMPLATE = TEMPLATES_PATH + "abcd.docx";

    @Autowired
    private CourseForGroupService courseForGroupService;

    @Autowired
    private StudentDegreeService studentDegreeService;

    @Autowired
    private DocumentIOService documentIOService;

    @Autowired
    private ExamReportBaseService examReportBaseService;

    public ExamReportForForeignStudentService(CurrentYearService currentYearService) {
        super(currentYearService);
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
        WordprocessingMLPackage reportsDocumen = documentIOService.loadTemplate(TEMPLATE);
        studentsAndCourses.forEach(studentAndCourses -> {
            WordprocessingMLPackage reportsDocument = fillTemplate(templateName, studentAndCourses, studentsAndCourses.get(studentAndCourses)); ///
            TemplateUtil.addPageBreak(reportsDocument);
            try {
                reportsDocument.getMainDocumentPart().getContent()
                        .addAll(fillTemplate(templateName, studentsAndCourses).getMainDocumentPart().getContent());
            } catch (IOException | Docx4JException e) {
                e.printStackTrace();
            }
        });
        return reportsDocument;
    }

    WordprocessingMLPackage fillTemplate(String templateName, StudentDegree studentDegree, List<CourseForGroup> studentsAndCourses) //генерує для одного
            throws IOException, Docx4JException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(templateName);///
                studentDegree.getStudent();
        Map<String, String> commonDict = new HashMap<>();
        for(CourseForGroup courseForGroup : studentsAndCourses) { ////
            commonDict.putAll(getGroupInfoReplacements(studentDegree));
            TemplateUtil.replaceTextPlaceholdersInTemplate(template, commonDict);
        }
        return template;
    }

    Map<String, String> getGroupInfoReplacements(StudentDegree courseForGroup) {
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
    }



}

