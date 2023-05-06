package ua.edu.chdtu.deanoffice.api.document.academicreference;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.service.FacultyService;
import ua.edu.chdtu.deanoffice.service.document.report.academic.reference.AbstractScholasticRecordsService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.io.File;
import java.io.IOException;

@Controller
@RequestMapping("/documents/abstract-scholastic-records")
public class AbstractScholasticRecordsController extends DocumentResponseController {

    private AbstractScholasticRecordsService abstractScholasticRecordsService;
    private FacultyService facultyService;

    public AbstractScholasticRecordsController(AbstractScholasticRecordsService abstractScholasticRecordsService, FacultyService facultyService) {
        this.abstractScholasticRecordsService = abstractScholasticRecordsService;
        this.facultyService = facultyService;
    }

    @GetMapping("/{studentDegreeId}")
    public ResponseEntity<Resource> generateDocumentForStudent(@PathVariable Integer studentDegreeId,
                                                               @CurrentUser ApplicationUser user) throws IOException, Docx4JException {
        try {
            facultyService.checkStudentDegree(studentDegreeId, user.getFaculty().getId());
            File transcriptOfRecordsReport = abstractScholasticRecordsService.formTranscriptOfRecordsDocument(studentDegreeId);
            return buildDocumentResponseEntity(transcriptOfRecordsReport, transcriptOfRecordsReport.getName(), MEDIA_TYPE_DOCX);
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, AcademicCertificateController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}

