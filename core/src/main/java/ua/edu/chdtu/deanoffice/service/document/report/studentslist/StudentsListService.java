package ua.edu.chdtu.deanoffice.service.document.report.studentslist;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;
import ua.edu.chdtu.deanoffice.service.FacultyService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;
import ua.edu.chdtu.deanoffice.service.document.diploma.supplement.DiplomaSupplementService;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.*;
@Service
public class StudentsListService {

    private static final String TEMPLATES_PATH = "/docs/templates/";
    private static final String TEMPLATE = TEMPLATES_PATH + "StudentsList.docx";
    private static final String TEMPLATEHEADERS = TEMPLATES_PATH + "StudentsListHeaders.docx";
    private static final String FILE_NAME= "students-list";
    private static final String KURS= "-kurs";
    private static Logger log = LoggerFactory.getLogger(DiplomaSupplementService.class);

    private StudentGroupService groupService;
    private DocumentIOService documentIOService;
    private StudentDegreeService studentDegreeService;
    private CurrentYearService currentYearService;
    private FacultyService facultyService;


    public StudentsListService(StudentGroupService groupService,
                               DocumentIOService documentIOService,
                               StudentDegreeService studentDegreeService,
                               CurrentYearService currentYearService,
                               FacultyService facultyService) {
        this.groupService = groupService;
        this.documentIOService = documentIOService;
        this.studentDegreeService = studentDegreeService;
        this.currentYearService = currentYearService;
        this.facultyService = facultyService;
    }

    public synchronized File prepareReport(Integer degreeId,
                                           Integer year,
                                           Integer facultyId) throws Docx4JException, IOException {
        List<StudentGroup> studentGroups = groupService.getGroupsByDegreeAndYear(degreeId,year,facultyId);
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
        for(StudentGroup group:studentGroups){
            List<Student> students = group.getActiveStudents();
            List<StudentForList> studentForLists = prepareGroup(students);
            group.getTuitionForm().toString();
            if(studentGroups.get(0) == group){
                wordMLPackage = fillTemplateHeaders(TEMPLATEHEADERS,
                                                    facultyService.getById(facultyId).getAbbr(),
                                                    year,
                                                    group.getTuitionForm().getNameUkr());
                wordMLPackage.getMainDocumentPart().getContent().addAll(fillTemplate(TEMPLATE,
                studentForLists,
                group).getMainDocumentPart().getContent());
            }
            else {
                wordMLPackage.getMainDocumentPart().getContent().addAll(fillTemplate(TEMPLATE,
                studentForLists,
                group).getMainDocumentPart().getContent());
            }
        }
        return documentIOService.saveDocumentToTemp(wordMLPackage,FILE_NAME+year+KURS, FileFormatEnum.DOCX);
    }


    private List<StudentForList> prepareGroup(List<Student> students) {
        List<StudentForList> studentForLists = new ArrayList<>();
        int numberStudent = 1;
        for(Student student:students){
            studentForLists.add(new StudentForList(String.valueOf(numberStudent)+'.',
                                                   student.getFullNameUkr(),
                                                   studentDegreeService.getById(student.getId()).getRecordBookNumber(),
                                                   studentDegreeService.getById(student.getId()).getPayment() == Payment.CONTRACT ? "договір" : ""));
            numberStudent++;
        }
        return studentForLists;
    }

    private WordprocessingMLPackage fillTemplate(String templateName, List<StudentForList> studentForLists, StudentGroup group) throws Docx4JException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(templateName);
        fillTableWithGrades(template, studentForLists);
        Map<String, String> commonDict = new HashMap<>();
        commonDict.put("groupName", group.getName());
        commonDict.put("code",group.getSpecialization().getSpeciality().getCode());
        commonDict.put("direction",group.getSpecialization().getSpeciality().getName());
        commonDict.put("specialty",group.getSpecialization().getName());
        replaceTextPlaceholdersInTemplate(template, commonDict);
        return template;
    }

    private WordprocessingMLPackage fillTemplateHeaders(String templateName,
                                                        String facultyName,
                                                        Integer year,
                                                        String tuitionForm) throws Docx4JException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(templateName);
        Map<String, String> commonDict = new HashMap<>();
        commonDict.put("faculty", facultyName);
        commonDict.put("form",tuitionForm);
        commonDict.put("course",String.valueOf(year));
        commonDict.put("year",String.valueOf(currentYearService.getYear()));
        replaceTextPlaceholdersInTemplate(template, commonDict);
        return template;
    }

    private void fillTableWithGrades(WordprocessingMLPackage template, List<StudentForList> studentForLists) {
        Tbl tempTable = findTable(template, "#n");
        if (tempTable == null) {
            return;
        }
        List<Object> gradeTableRows = getAllElementsFromObject(tempTable, Tr.class);
        Tr templateRow = (Tr) gradeTableRows.get(1);
        int rowToAddIndex = 1;
        for (StudentForList student : studentForLists) {
            Map<String, String> replacements = student.getDictionary();
            addRowToTable(tempTable, templateRow, rowToAddIndex, replacements);
            rowToAddIndex++;
        }
        tempTable.getContent().remove(templateRow);
    }

}
