package ua.edu.chdtu.deanoffice.api.document.academicdifference;


import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.api.document.academicreference.AcademicCertificateController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.service.FacultyService;
import ua.edu.chdtu.deanoffice.service.document.report.academicdifference.AcademicDifferenceService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.io.File;
import java.io.IOException;

@Controller
@RequestMapping("/documents/academic-difference")
public class AcademicDifferenceController extends DocumentResponseController {
    private AcademicDifferenceService academicDifferenceService;
    private FacultyService facultyService;
    public AcademicDifferenceController(AcademicDifferenceService academicDifferenceService, FacultyService facultyService) {
        this.academicDifferenceService = academicDifferenceService;
        this.facultyService = facultyService;
    }

    @GetMapping("/{studentDegreeId}")
    public ResponseEntity<Resource> generateDocumentForStudent(
        @PathVariable Integer studentDegreeId,
        @CurrentUser ApplicationUser user
        ) throws IOException, Docx4JException {
        try {
            facultyService.checkStudentExpel(studentDegreeId, user.getFaculty().getId());
            File academicDifferenceReport = academicDifferenceService.formDocument(studentDegreeId);
            return buildDocumentResponseEntity(academicDifferenceReport, academicDifferenceReport.getName(), MEDIA_TYPE_DOCX);
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, AcademicCertificateController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
