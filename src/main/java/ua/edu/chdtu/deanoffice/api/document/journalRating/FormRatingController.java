package ua.edu.chdtu.deanoffice.api.document.journalRating;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;
import ua.edu.chdtu.deanoffice.service.document.report.journal.rating.FormRatingDocxService;
import ua.edu.chdtu.deanoffice.service.document.report.journal.rating.FormRatingPDFService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;
import java.io.File;

@Controller
@RequestMapping("/documents/form-rating")
public class FormRatingController extends DocumentResponseController {
    private FormRatingDocxService formRatingDocxService;
    private FormRatingPDFService formRatingPDFService;

    public FormRatingController(FormRatingDocxService formRatingDocxService, FormRatingPDFService formRatingPDFService) {
        this.formRatingDocxService = formRatingDocxService;
        this.formRatingPDFService = formRatingPDFService;
    }


    @GetMapping("/year/{year}/degree/{degreeId}/docx")
    public ResponseEntity<Resource> generateDocxDocumentForStudent(
            @PathVariable Integer year, @PathVariable Integer degreeId,
            @RequestParam("tuitionForm") String tuitionForm,
            @RequestParam("semester") int semester,
            @CurrentUser ApplicationUser user
    ) {
        try {
            File file = formRatingDocxService.formDocument(degreeId, year, user.getFaculty().getId(), tuitionForm, (year-1)*2+semester);
            return buildDocumentResponseEntity(file, file.getName(), MEDIA_TYPE_DOCX);
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @GetMapping("/year/{year}/degree/{degreeId}/pdf")
    public ResponseEntity<Resource> generatePdfDocumentForStudent(
            @PathVariable Integer year, @PathVariable Integer degreeId,
            @RequestParam("tuitionForm") String tuitionForm,
            @RequestParam("semester") int semester,
            @CurrentUser ApplicationUser user
    ) {
        try {
            File file = formRatingPDFService.formDocument(degreeId, year, user.getFaculty().getId(), tuitionForm, (year-1)*2+semester);
            return buildDocumentResponseEntity(file, file.getName(), MEDIA_TYPE_PDF);
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, FormRatingController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
