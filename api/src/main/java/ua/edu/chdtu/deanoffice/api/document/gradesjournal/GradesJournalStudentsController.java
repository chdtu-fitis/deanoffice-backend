package ua.edu.chdtu.deanoffice.api.document.gradesjournal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.service.document.report.journal.GradesJournalService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.io.File;

@RestController
@RequestMapping("/documents/grades-journal")
public class GradesJournalStudentsController extends DocumentResponseController {

    private GradesJournalService gradesJournalService;

    public GradesJournalStudentsController(GradesJournalService gradesJournalService){
        this.gradesJournalService = gradesJournalService;
    }

    @GetMapping("/students")
    public ResponseEntity getStudentGroupFile(@RequestParam int degreeId, @RequestParam int year, @CurrentUser ApplicationUser user){
        try {
            File file = gradesJournalService.createStudentsListsPdf(degreeId, year, user.getFaculty().getId());
            return buildDocumentResponseEntity(file, file.getName(), MEDIA_TYPE_PDF);
        } catch (Exception e){
            return handleException(e);
        }
    }

    @GetMapping("/courses/pdf")
    public ResponseEntity getSubjectsFile(@RequestParam int degreeId, @RequestParam int year, @CurrentUser ApplicationUser user){
        try{
            File file = gradesJournalService.createCoursesListsPdf(degreeId, year, user.getFaculty().getId());
            return buildDocumentResponseEntity(file, file.getName(), MEDIA_TYPE_PDF);
        } catch (Exception e){
            return handleException(e);
        }
    }

    @GetMapping("/courses/docx")
    public ResponseEntity getCoursesInDocxFile(@RequestParam int degreeId, @RequestParam int year, @CurrentUser ApplicationUser user){
        try{
            File file = gradesJournalService.createCoursesListsDocx(degreeId, year, user.getFaculty().getId());
            return buildDocumentResponseEntity(file, file.getName(), MEDIA_TYPE_DOCX);
        } catch (Exception e){
            return handleException(e);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, GradesJournalStudentsController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
