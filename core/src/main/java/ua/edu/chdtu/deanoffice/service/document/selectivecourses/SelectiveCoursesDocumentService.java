package ua.edu.chdtu.deanoffice.service.document.selectivecourses;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.SelectiveCourse;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.service.DegreeService;
import ua.edu.chdtu.deanoffice.service.course.selective.SelectiveCoursesStudentDegreesService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ua.edu.chdtu.deanoffice.service.document.DocumentIOService.TEMPLATES_PATH;
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.addRowToTable;
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.createParagraph;
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.createR;
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.createText;
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.findTable;
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.getAllElementsFromObject;
import static ua.edu.chdtu.deanoffice.service.document.TemplateUtil.replaceTextPlaceholdersInTemplate;

@Service
public class SelectiveCoursesDocumentService {

    private static final String TEMPLATE_PATH = TEMPLATES_PATH + "SelectiveCoursesGroupsHeader.docx";
    private static final String TEMPLATE_GENERAL_TABLE = TEMPLATES_PATH + "SelectiveCoursesGeneralTable.docx";

    private final DocumentIOService documentIOService;
    private DegreeService degreeService;
    private final SelectiveCoursesStudentDegreesService selectiveCoursesStudentDegreesService;

    public SelectiveCoursesDocumentService(DocumentIOService documentIOService,
                                           DegreeService degreeService, SelectiveCoursesStudentDegreesService selectiveCoursesStudentDegreesService) {
        this.documentIOService = documentIOService;
        this.degreeService = degreeService;
        this.selectiveCoursesStudentDegreesService = selectiveCoursesStudentDegreesService;
    }

    public File formDocument(int studyYear, int course, int degreeId)
            throws Docx4JException, IOException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(TEMPLATE_PATH);

        int firstSemester =  (course * 2) - 1;
        int secondSemester = (course * 2);

        Map<String, String> commonDict = new HashMap<>();
        commonDict.put("yearFrom", String.valueOf(studyYear));
        commonDict.put("yearTo", String.valueOf(studyYear+1));
        commonDict.put("degree", degreeService.getById(degreeId).getName().toLowerCase());
        replaceTextPlaceholdersInTemplate(template, commonDict);

        fillGeneralTablesBySemester(template,firstSemester,studyYear,degreeId);

        fillGeneralTablesBySemester(template,secondSemester,studyYear,degreeId);

        return documentIOService.saveDocumentToTemp(template, "Selective", FileFormatEnum.DOCX);
    }

    private void fillGeneralTablesBySemester(WordprocessingMLPackage template,
                                             int semester,
                                             int studyYear,
                                             int degreeId
                                             ) throws Docx4JException {
        template.getMainDocumentPart()
                .getContent()
                .add(createParagraphSemesterText(semester));

        Map<SelectiveCourse, List<StudentDegree>> studentDegreesBySelectiveCourses = selectiveCoursesStudentDegreesService.getStudentDegreesBySelectiveCourses(studyYear, semester, degreeId);

        fillGeneralTablesBySelectiveCourses(template, studentDegreesBySelectiveCourses);
    }

    public WordprocessingMLPackage fillGeneralTablesBySelectiveCourses(WordprocessingMLPackage template,
                                                                       Map<SelectiveCourse, List<StudentDegree>> studentDegreesBySelectiveCourses) throws Docx4JException {
        for (SelectiveCourse selectiveCourse: studentDegreesBySelectiveCourses.keySet()) {
            template.getMainDocumentPart()
                    .getContent()
                    .addAll(fillGeneralTableTemplate(TEMPLATE_GENERAL_TABLE,studentDegreesBySelectiveCourses.get(selectiveCourse),selectiveCourse)
                            .getMainDocumentPart()
                            .getContent());
        }
        return template;
    }

    public P createParagraphSemesterText(int semester) {
        P paragraph = createParagraph();
        R run = createR();
        Text semesterText = createText(semester + " СЕМЕСТР");
        run.getContent().add(semesterText);
        paragraph.getContent().add(run);

        return paragraph;
    }

    private WordprocessingMLPackage fillGeneralTableTemplate(
            String templateName,
            List<StudentDegree> studentDegrees,
            SelectiveCourse selectiveCourse
    ) throws Docx4JException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(templateName);
        fillGeneralTable(template, studentDegrees);
        Map<String, String> commonDict = new HashMap<>();
        commonDict.put("courseName", selectiveCourse.getCourse().getCourseName().getName());
        commonDict.put("groupName", selectiveCourse.getGroupName());
        replaceTextPlaceholdersInTemplate(template, commonDict);
        return template;
    }

    private void fillGeneralTable(WordprocessingMLPackage template, List<StudentDegree> studentDegrees) {
        Tbl tempTable = findTable(template, "#n");
        if (tempTable == null) {
            return;
        }
        List<Object> gradeTableRows = getAllElementsFromObject(tempTable, Tr.class);
        Tr templateRow = (Tr) gradeTableRows.get(0);
        int rowToAddIndex = 1;
        for (StudentDegree studentDegree : studentDegrees) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("n", rowToAddIndex + ".");
            replacements.put("studentName", studentDegree.getStudent().getFullNameUkr());
            replacements.put("group",studentDegree.getStudentGroup().getName());
            replacements.put("facultyAbr",studentDegree.getStudentGroup().getSpecialization().getFaculty().getAbbr());

            addRowToTable(tempTable, templateRow, rowToAddIndex, replacements);
            rowToAddIndex++;
        }
        tempTable.getContent().remove(templateRow);
    }
}
