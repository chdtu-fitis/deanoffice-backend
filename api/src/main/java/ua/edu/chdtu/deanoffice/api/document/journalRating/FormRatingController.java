package ua.edu.chdtu.deanoffice.api.document.journalRating;


import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.api.document.academicreference.AcademicCertificateController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.TuitionForm;
import ua.edu.chdtu.deanoffice.service.FacultyService;
import ua.edu.chdtu.deanoffice.service.document.report.academicdifference.AcademicDifferenceService;
import ua.edu.chdtu.deanoffice.service.document.report.journal.FormRatingService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.io.File;
import java.io.IOException;

@Controller
@RequestMapping("/documents/form-rating")
public class FormRatingController extends DocumentResponseController {
    private FormRatingService formRatingService;
    private FacultyService facultyService;
    public FormRatingController(FormRatingService formRatingService, FacultyService facultyService) {
        this.formRatingService = formRatingService;
        this.facultyService = facultyService;
    }

    @GetMapping("/year/{year}/degree/{degreeId}")
    public ResponseEntity<Resource> generateDocumentForStudent(
            @PathVariable Integer year, @PathVariable Integer degreeId,
            @RequestParam("tuitionForm") String tuitionForm,
            @RequestParam("semester") int semester,
            @CurrentUser ApplicationUser user
        ) throws IOException, Docx4JException {
        try {
            File academicDifferenceReport = formRatingService.formDocument(degreeId,year,user.getFaculty().getId(),tuitionForm,semester);
            return buildDocumentResponseEntity(academicDifferenceReport, academicDifferenceReport.getName(), MEDIA_TYPE_DOCX);
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, AcademicCertificateController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
