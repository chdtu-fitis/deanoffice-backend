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
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class IndividualCurriculumService {
    private static final String FILE_NAME = "Individual_curriculum_of_the_higher_educators";

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

    public File createIndividualCurriculumDocx(int groupId,
                                               List<Integer> studentIds,
                                               String studyYear) throws FileNotFoundException, Docx4JException {
        Set<StudentDegree> studentDegrees = getStudentDegreesByGroup(groupId);
        studentDegrees.addAll(getStudentDegreesByStudentIds(studentIds));

        Set<StudentDegree> collect = getFilteredStudentDegrees(studentDegrees);

        WordprocessingMLPackage file = fillTemplateService.fillTemplate(collect, studyYear);

        return documentIOService.saveDocumentToTemp(file, FILE_NAME, FileFormatEnum.DOCX);
    }

    private Set<StudentDegree> getFilteredStudentDegrees(Set<StudentDegree> studentDegrees) {
        return studentDegrees.stream()
                .sorted(Comparator.comparing(degree -> (
                                degree.getStudent().getSurname() + " " +
                                        degree.getStudent().getName() + " " +
                                        degree.getStudent().getPatronimic()
                        )
                )).collect(Collectors.toCollection(LinkedHashSet::new));
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
