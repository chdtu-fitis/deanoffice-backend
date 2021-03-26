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
import ua.edu.chdtu.deanoffice.service.document.report.academic.reference.AcademicCertificateService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.io.File;
import java.io.IOException;

@Controller
@RequestMapping("/documents/academic-certificate")
public class AcademicCertificateController extends DocumentResponseController {

    private AcademicCertificateService academicReferenceService;

    private FacultyService facultyService;

    public AcademicCertificateController(AcademicCertificateService academicReferenceService, FacultyService facultyService) {
        this.academicReferenceService = academicReferenceService;
        this.facultyService = facultyService;
    }

    @GetMapping("/{studentExpelId}")
    public ResponseEntity<Resource> generateDocumentForStudent(@PathVariable Integer studentExpelId,
                                                               @CurrentUser ApplicationUser user) throws IOException, Docx4JException {
        try {
            facultyService.checkStudentExpel(studentExpelId, user.getFaculty().getId());
            File academicReferenceReport = academicReferenceService.formDocument(studentExpelId);
            return buildDocumentResponseEntity(academicReferenceReport, academicReferenceReport.getName(), MEDIA_TYPE_DOCX);
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, AcademicCertificateController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
