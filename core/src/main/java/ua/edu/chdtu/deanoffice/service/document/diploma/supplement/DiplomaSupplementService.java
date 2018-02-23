package ua.edu.chdtu.deanoffice.service.document.diploma.supplement;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Grade;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.service.GradeService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;
import ua.edu.chdtu.deanoffice.service.StudentService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;

import java.io.File;
import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class DiplomaSupplementService {

    private static final String TEMPLATES_PATH = "docs/templates/";
    private static final String TEMPLATE = TEMPLATES_PATH + "DiplomaSupplement.docx";

    private static Logger log = LoggerFactory.getLogger(DiplomaSupplementService.class);

    private StudentService studentService;
    private GradeService gradeService;
    private StudentGroupService groupService;
    private DocumentIOService documentIOService;
    private TemplateFillService templateFillService;

    public DiplomaSupplementService(StudentService studentService,
                                    GradeService gradeService,
                                    StudentGroupService groupService,
                                    DocumentIOService documentIOService,
                                    TemplateFillService templateFillService) {
        this.studentService = studentService;
        this.gradeService = gradeService;
        this.groupService = groupService;
        this.documentIOService = documentIOService;
        this.templateFillService = templateFillService;
    }

    public File formDiplomaSupplementForStudent(Integer studentId) throws Docx4JException, IOException {
        Student student = studentService.get(studentId);
        List<List<Grade>> grades = gradeService.getGradesByStudentId(studentId);
        StudentSummary studentSummary = new StudentSummary(student, grades);
        String fileName = student.getSurnameEng() + "_" + studentSummary.getStudent().getNameEng();
        fileName = cleanFileName(fileName);
        WordprocessingMLPackage filledTemplate = templateFillService.fill(TEMPLATE, studentSummary);
        return documentIOService.saveDocumentToTemp(filledTemplate, fileName + ".docx");
    }

    public String cleanFileName(String fileName) {
        return fileName.replaceAll("[\\W]*", "");
    }

    public File formDiplomaSupplementForGroup(Integer groupId) throws Docx4JException, IOException {
        StudentGroup studentGroup = groupService.getById(groupId);
        if (studentGroup == null || studentGroup.getStudents().isEmpty()) {
            return null;
        }
        List<Student> students = new ArrayList<>(studentGroup.getStudents());
        students.sort((s1, s2) -> {
            Collator ukrainianCollator = Collator.getInstance(new Locale("uk", "UA"));
            return ukrainianCollator.compare(s1.getSurname(), s2.getSurname());
        });
        WordprocessingMLPackage groupTemplate = null;
        int studentNumber = 1;
        for (Student student : students) {
            StudentSummary studentSummary = new StudentSummary(student, gradeService.getGradesByStudentId(student.getId()));
            WordprocessingMLPackage studentFilledTemplate = templateFillService.fill(TEMPLATE, studentSummary);
            if (groupTemplate == null) {
                groupTemplate = studentFilledTemplate;
                studentNumber++;
                continue;
            }
//            List<Relationship> relationships = studentFilledTemplate.getMainDocumentPart()
//                    .relationships.getRelationshipsByType(Namespaces.FOOTER);
//            PartName footerPartName = null;
//            PartName newFooterPartName = null;
//            try {
//                footerPartName = new PartName("/word/footer1.xml");
//                newFooterPartName = new PartName("/word/footer" + studentNumber + ".xml");
//            } catch (InvalidFormatException e) {
//            }
//            Part footerPart = studentFilledTemplate.getParts().get(footerPartName);
//            footerPart.setPartName(newFooterPartName);
//            footerPart.setPackage(groupTemplate);
//            groupTemplate.getParts().put(footerPart);
//            for (Relationship r : relationships) {
//                r.setId(String.format("%d", studentNumber + 20));
//                r.setTarget("/word/footer" + studentNumber + ".xml");
//                //groupTemplate.getRelationshipsPart().addRelationship(r);
//                groupTemplate.getMainDocumentPart().getRelationshipsPart().addRelationship(r);
//                r.setParent(groupTemplate.getRelationshipsPart());
//            }
            for (Object o : studentFilledTemplate.getMainDocumentPart().getContent()) {
                groupTemplate.getMainDocumentPart().addObject(o);
            }
            studentNumber++;
        }
        return documentIOService.saveDocumentToTemp(groupTemplate, studentGroup.getName() + ".docx");
    }

}
