package ua.edu.chdtu.deanoffice.service.document.report.studentslist;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ua.edu.chdtu.deanoffice.entity.Payment;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.entity.TuitionForm;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;
import ua.edu.chdtu.deanoffice.service.FacultyService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;
import ua.edu.chdtu.deanoffice.service.document.diploma.supplement.DiplomaSupplementService;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.addRowToTable;
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.findTable;
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.getAllElementsFromObject;
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.replaceTextPlaceholdersInTemplate;

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
    private CurrentYearService currentYearService;
    private FacultyService facultyService;

    public StudentsListService(
        StudentGroupService groupService,
        DocumentIOService documentIOService,
        CurrentYearService currentYearService,
        FacultyService facultyService
    ) {
        this.groupService = groupService;
        this.documentIOService = documentIOService;
        this.currentYearService = currentYearService;
        this.facultyService = facultyService;
    }

    public synchronized File prepareReport(
        Integer degreeId,
        Integer year,
        Integer facultyId,
        String tuitionFormText
    ) throws Docx4JException, IOException {
        TuitionForm tuitionForm = TuitionForm.valueOf(tuitionFormText);
        List<StudentGroup> studentGroups = groupService.getGroupsByDegreeAndYearAndTuitionForm(degreeId,year,facultyId,tuitionForm);
        WordprocessingMLPackage wordMLPackage = fillTemplateHeaders(
            TEMPLATEHEADERS,
            facultyService.getById(facultyId).getAbbr(),
            year,
            tuitionForm.getNameUkr()
        );
        for (StudentGroup group:studentGroups){
            List<StudentDegree> students = group.getStudentDegrees();
            List<StudentForList> studentForLists = prepareGroup(students);
            group.getTuitionForm().toString();
            wordMLPackage.
            getMainDocumentPart().
            getContent().
            addAll(fillTemplate(TEMPLATE,studentForLists,group).
            getMainDocumentPart().
            getContent());
        }
        return documentIOService.saveDocumentToTemp(wordMLPackage,FILE_NAME+year+KURS, FileFormatEnum.DOCX);
    }

    private List<StudentForList> prepareGroup(List<StudentDegree> students) {
        List<StudentForList> studentForLists = new ArrayList<>();
        int numberStudent = 1;
        for (StudentDegree student:students){
            String numberStudentWithPoint = String.valueOf(numberStudent)+'.';
            String contract = student.getPayment() == Payment.CONTRACT ? "договір" : "";
            studentForLists.add(new StudentForList(
                numberStudentWithPoint,
                student.getStudent().getFullNameUkr(),
                student.getRecordBookNumber(),
                contract
            ));
            numberStudent++;
        }
        return studentForLists;
    }

    private WordprocessingMLPackage fillTemplate(
        String templateName,
        List<StudentForList> studentForLists,
        StudentGroup group
    ) throws Docx4JException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(templateName);
        fillTable(template, studentForLists);
        Map<String, String> commonDict = new HashMap<>();
        commonDict.put("groupName", group.getName());
        commonDict.put("code",group.getSpecialization().getSpeciality().getCode());
        commonDict.put("direction",group.getSpecialization().getSpeciality().getName());
        commonDict.put("specialty",group.getSpecialization().getName());
        replaceTextPlaceholdersInTemplate(template, commonDict);
        return template;
    }

    private WordprocessingMLPackage fillTemplateHeaders(
        String templateName,
        String facultyName,
        Integer year,
        String tuitionForm
    ) throws Docx4JException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(templateName);
        Map<String, String> commonDict = new HashMap<>();
        commonDict.put("faculty", facultyName);
        commonDict.put("form",tuitionForm);
        commonDict.put("course",String.valueOf(year));
        commonDict.put("year",String.valueOf(currentYearService.getYear()));
        replaceTextPlaceholdersInTemplate(template, commonDict);
        return template;
    }

    private void fillTable(WordprocessingMLPackage template, List<StudentForList> studentForLists) {
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
