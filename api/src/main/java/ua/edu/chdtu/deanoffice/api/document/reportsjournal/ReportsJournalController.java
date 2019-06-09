package ua.edu.chdtu.deanoffice.api.document.reportsjournal;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.TuitionForm;
import ua.edu.chdtu.deanoffice.service.FacultyService;
import ua.edu.chdtu.deanoffice.service.document.report.journal.ReportsCoursesService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.io.File;

@RestController
@RequestMapping("/documents/exam-reports-journal-courses")
public class ReportsJournalController extends DocumentResponseController {
    private ReportsCoursesService reportsCoursesService;
    private FacultyService facultyService;

    public ReportsJournalController(ReportsCoursesService reportsCoursesService, FacultyService facultyService) {
        this.reportsCoursesService = reportsCoursesService;
        this.facultyService = facultyService;
    }

    @GetMapping("/groups/{groupId}")
    public ResponseEntity<Resource> generateForGroup(@PathVariable Integer groupId,
                                                     @RequestParam("semester") Integer semester,
                                                     @CurrentUser ApplicationUser user) {
        try {
            facultyService.checkGroup(groupId, user.getFaculty().getId());
            File groupDiplomaSupplements = reportsCoursesService.prepareReportForGroup(groupId, semester);
            return buildDocumentResponseEntity(groupDiplomaSupplements, groupDiplomaSupplements.getName(), MEDIA_TYPE_DOCX);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/year/{year}/degree/{degreeId}")
    public ResponseEntity<Resource> generateForYear(@PathVariable Integer year, @PathVariable Integer degreeId,
                                                    @RequestParam("semester") int semester,
                                                    @RequestParam(required = false) TuitionForm tuitionForm,
                                                    @RequestParam(required = false, defaultValue = "0") int groupId,
                                                    @CurrentUser ApplicationUser user)  {
        try {
            File reportsJournal = reportsCoursesService.prepareReportForYear(degreeId, year, semester, tuitionForm, groupId, user.getFaculty().getId());
            return buildDocumentResponseEntity(reportsJournal, reportsJournal.getName(), MEDIA_TYPE_DOCX);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private static ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, ReportsJournalController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
