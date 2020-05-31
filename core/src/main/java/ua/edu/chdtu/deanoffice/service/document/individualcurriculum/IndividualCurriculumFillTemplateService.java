package ua.edu.chdtu.deanoffice.service.document.individualcurriculum;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.TemplateUtil;

import java.util.Objects;
import java.util.Set;

import static ua.edu.chdtu.deanoffice.service.document.DocumentIOService.TEMPLATES_PATH;

@Service
public class IndividualCurriculumFillTemplateService {
    private static final Logger log = LoggerFactory.getLogger(IndividualCurriculumFillTemplateService.class);

    private final static String TEMPLATE_PATH = TEMPLATES_PATH + "IndividualCurriculum.dox";

    private final DocumentIOService documentIOService;
    private final CourseForGroupService courseForGroupService;

    public IndividualCurriculumFillTemplateService(DocumentIOService documentIOService,
                                                   CourseForGroupService courseForGroupService) {
        this.documentIOService = documentIOService;
        this.courseForGroupService = courseForGroupService;
    }

    public WordprocessingMLPackage fillTemplate(Set<StudentDegree> degrees) {
        WordprocessingMLPackage formedDocument = null;

        for (StudentDegree degree : degrees) {
            try {
                if (Objects.nonNull(formedDocument)) {
                    TemplateUtil.addPageBreak(formedDocument);

                    formedDocument.getMainDocumentPart().getContent().addAll(
                            fillTemplateForSingleDegree(degree).getMainDocumentPart().getContent()
                    );
                } else {
                    formedDocument = fillTemplateForSingleDegree(degree);
                }
            } catch (Docx4JException e) {
                log.error(e.getMessage());
            }
        }

        return formedDocument;
    }

    private WordprocessingMLPackage fillTemplateForSingleDegree(StudentDegree degree) throws Docx4JException {
        WordprocessingMLPackage template = documentIOService.loadTemplate(TEMPLATE_PATH);

        return null;
    }
}
