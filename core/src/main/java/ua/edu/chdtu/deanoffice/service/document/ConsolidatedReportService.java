package ua.edu.chdtu.deanoffice.service.document;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.entity.Degree;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.service.document.report.exam.ExamReportTemplateFillService;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static ua.edu.chdtu.deanoffice.service.document.DocumentIOService.TEMPLATES_PATH;

@Service
public class ConsolidatedReportService {
    private static final String TEMPLATE = TEMPLATES_PATH + "SingleGroupStatement.docx";

    private DocumentIOService documentIOService;
    private ExamReportTemplateFillService examReportTemplateFillService;

    @Autowired
    public ConsolidatedReportService(DocumentIOService documentIOService, ExamReportTemplateFillService examReportTemplateFillService) {
        this.documentIOService = documentIOService;
        this.examReportTemplateFillService = examReportTemplateFillService;
    }

    //TODO
    public synchronized File formConsolidatedReportDocx(Map<CourseForGroup, List<StudentGroup>> coursesToStudentGroups, ApplicationUser user)
            throws Docx4JException, IOException, OperationCannotBePerformedException{
        validateData(coursesToStudentGroups);

        return documentIOService.saveDocumentToTemp(loadTemplate(coursesToStudentGroups, user),
                "ZVEDENA-VIDOMIST", FileFormatEnum.DOCX);
    }

    //TODO
    private WordprocessingMLPackage loadTemplate(Map<CourseForGroup, List<StudentGroup>> coursesToStudentGroups, ApplicationUser user)
            throws Docx4JException, IOException, OperationCannotBePerformedException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(TEMPLATE);
        WordprocessingMLPackage document = documentIOService.loadTemplate(TEMPLATE);

        Set<CourseForGroup> courseForGroups = coursesToStudentGroups.keySet();
        Iterator iterator = courseForGroups.iterator();
        CourseForGroup courseForGroup;
        int numberOfTable = 5;

        while (iterator.hasNext()) {
            courseForGroup = (CourseForGroup) iterator.next();
            List<StudentGroup> studentGroups = coursesToStudentGroups.get(courseForGroup);
            Degree degree = studentGroups.get(0).getSpecialization().getDegree();
            checkIsAllGroupsHaveSameDegree(studentGroups, degree);
            fillTemplate(document, courseForGroup, coursesToStudentGroups.get(courseForGroup), user, numberOfTable);
            if (iterator.hasNext()) {
                TemplateUtil.addPageBreak(document);
                document.getMainDocumentPart().getContent().addAll(template.getMainDocumentPart().getContent());
                numberOfTable = numberOfTable + 9;
            }
        }

        return document;
    }

    //TODO
    private void fillTemplate(WordprocessingMLPackage document, CourseForGroup courseForGroup, List<StudentGroup> studentGroups, ApplicationUser user, int numberOfTable)
        throws Docx4JException, IOException {
       examReportTemplateFillService.fillTemplate(document, courseForGroup, studentGroups, numberOfTable, user);
    }

    private void validateData(Map<CourseForGroup, List<StudentGroup>> coursesToStudentGroups) throws OperationCannotBePerformedException {
        if (coursesToStudentGroups.size() == 0) {
            throw new OperationCannotBePerformedException("Для формування документу потрібно передати хоча б один курс");
        }
        if (coursesToStudentGroups.values().stream().anyMatch(Objects::isNull) || coursesToStudentGroups.values().stream().anyMatch(List::isEmpty)) {
            throw new OperationCannotBePerformedException("Для формування документу потрібно, щоб кожному предмету відповідала хоча б одна група");
        }
    }

    private void checkIsAllGroupsHaveSameDegree(List<StudentGroup> studentGroups, Degree degree) throws OperationCannotBePerformedException {
        for (StudentGroup studentGroup : studentGroups) {
            if (studentGroup.getSpecialization().getDegree().getId() != degree.getId()) {
                throw new OperationCannotBePerformedException("В межах одного курсу всі групи повинні мати один ступінь");
            }
        }
    }
}
