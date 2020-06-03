package ua.edu.chdtu.deanoffice.service.document.individualcurriculum;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class IndividualCurriculumService {
    private static final String FILE_NAME = "Individual_curriculum";

    private final StudentDegreeRepository studentDegreeRepository;
    private final IndividualCurriculumFillTemplateService fillTemplateService;
    private final DocumentIOService documentIOService;

    public IndividualCurriculumService(StudentDegreeRepository studentDegreeRepository,
                                       IndividualCurriculumFillTemplateService fillTemplateService,
                                       DocumentIOService documentIOService) {
        this.studentDegreeRepository = studentDegreeRepository;
        this.fillTemplateService = fillTemplateService;
        this.documentIOService = documentIOService;
    }

    public File createIndividualCurriculumDocx(List<Integer> studentDegreeIds,
                                               String studyYear) throws FileNotFoundException, Docx4JException {
        List<StudentDegree> studentDegrees = studentDegreeRepository.getAllByIds(studentDegreeIds);
        Set<StudentDegree> collect = sortByFullName(studentDegrees);
        WordprocessingMLPackage file = fillTemplateService.fillTemplate(collect, studyYear);
        return documentIOService.saveDocumentToTemp(file, FILE_NAME, FileFormatEnum.DOCX);
    }

    private Set<StudentDegree> sortByFullName(List<StudentDegree> studentDegrees) {
        return studentDegrees.stream()
                .sorted(Comparator.comparing(degree -> (
                                degree.getStudent().getSurname() + " " +
                                degree.getStudent().getName() + " " +
                                degree.getStudent().getPatronimic()
                        )
                )).collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
