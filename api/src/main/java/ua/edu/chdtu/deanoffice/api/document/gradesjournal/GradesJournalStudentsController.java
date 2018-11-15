package ua.edu.chdtu.deanoffice.api.document.gradesjournal;

import com.itextpdf.text.DocumentException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.api.document.academicreference.AcademicCertificateController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.service.document.report.journal.GradesJournalService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.io.File;
import java.io.IOException;


/**
 * Created by user on 01.11.2018.
 */
@RestController
@RequestMapping("/documents/grades-journal")
public class GradesJournalStudentsController extends DocumentResponseController {

    private GradesJournalService gradesJournalService;

    public GradesJournalStudentsController(GradesJournalService gradesJournalService){
        this.gradesJournalService = gradesJournalService;
    }

    @GetMapping("/students")
    public ResponseEntity getStudentGroupFile(@RequestParam int degreeId, @RequestParam int year, @CurrentUser ApplicationUser user){//(year, Degree)//int degreeId, int year, int facultyId
        try {
            File file = gradesJournalService.createStudentsListsPdf(degreeId, year, user.getFaculty().getId());
            return buildDocumentResponseEntity(file, file.getName(), MEDIA_TYPE_PDF);
        } catch (Exception e){
            return handleException(e);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, GradesJournalStudentsController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
