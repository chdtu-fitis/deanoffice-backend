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
import ua.edu.chdtu.deanoffice.service.document.report.journal.FormRatingService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;
import java.io.File;

@Controller
@RequestMapping("/documents/form-rating")
public class FormRatingController extends DocumentResponseController {
    private FormRatingService formRatingService;

    public FormRatingController(FormRatingService formRatingService) {
        this.formRatingService = formRatingService;
    }

    @GetMapping("/year/{year}/degree/{degreeId}")
    public ResponseEntity<Resource> generateDocumentForStudent(
            @PathVariable Integer year, @PathVariable Integer degreeId,
            @RequestParam("tuitionForm") String tuitionForm,
            @RequestParam("semester") int semester,
            @CurrentUser ApplicationUser user
        ) {
        try {
            File gradeJournalForm = formRatingService.formDocument(degreeId, year, user.getFaculty().getId(), tuitionForm, (year-1)*2+semester);
            return buildDocumentResponseEntity(gradeJournalForm, gradeJournalForm.getName(), MEDIA_TYPE_DOCX);
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, FormRatingController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
