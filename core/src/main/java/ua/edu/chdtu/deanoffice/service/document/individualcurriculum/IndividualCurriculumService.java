package ua.edu.chdtu.deanoffice.service.document.individualcurriculum;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


@Service
public class IndividualCurriculumService {
    private static final String FILE_NAME = "Individual curriculum of the higher educators";

    private final StudentDegreeService studentDegreeService;
    private final IndividualCurriculumFillTemplateService fillTemplateService;
    private final DocumentIOService documentIOService;

    public IndividualCurriculumService(StudentDegreeService studentDegreeService,
                                       IndividualCurriculumFillTemplateService fillTemplateService,
                                       DocumentIOService documentIOService) {
        this.studentDegreeService = studentDegreeService;
        this.fillTemplateService = fillTemplateService;
        this.documentIOService = documentIOService;
    }

    public File createIndividualCurriculumDocx(int groupId, List<Integer> studentIds) throws FileNotFoundException, Docx4JException {
        Set<StudentDegree> studentDegrees = getStudentDegreesByGroup(groupId);
        studentDegrees.addAll(getStudentDegreesByStudentIds(studentIds));

        WordprocessingMLPackage file = fillTemplateService.fillTemplate(studentDegrees);

        return documentIOService.saveDocumentToTemp(file, FILE_NAME, FileFormatEnum.DOCX);
    }

    private Set<StudentDegree> getStudentDegreesByGroup(int groupId) {
        Set<StudentDegree> degrees = new HashSet<>();

        if (groupId > 0) {
            degrees.addAll(studentDegreeService.getAllByGroupId(groupId));
        }

        return degrees;
    }

    private Set<StudentDegree> getStudentDegreesByStudentIds(List<Integer> studentIds) {
        Set<StudentDegree> degrees = new HashSet<>();

        if (Objects.nonNull(studentIds) && studentIds.size() > 0) {
            degrees.addAll(studentDegreeService.getByStudentIds(studentIds));
        }

        return degrees;
    }
}
