package ua.edu.chdtu.deanoffice.api.document.informal.recordbooks;

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
public class ExamReportsRecordBookController extends DocumentResponseController {
    private ReportsCoursesService reportsCoursesService;
    private FacultyService facultyService;

    public ExamReportsRecordBookController(ReportsCoursesService reportsCoursesService, FacultyService facultyService) {
        this.reportsCoursesService = reportsCoursesService;
        this.facultyService = facultyService;
    }

    @GetMapping("/groups/{groupId}")
    public ResponseEntity<Resource> generateForGroup(@PathVariable Integer groupId,
                                                     @RequestParam("semester") Integer semester,
                                                     @RequestParam(required = false, defaultValue = "1") int initialNumber,
                                                     @CurrentUser ApplicationUser user) {
        try {
            facultyService.checkGroup(groupId, user.getFaculty().getId());
            File groupDiplomaSupplements = reportsCoursesService.prepareReportForGroup(groupId, semester, initialNumber);
            return buildDocumentResponseEntity(groupDiplomaSupplements, groupDiplomaSupplements.getName(), MEDIA_TYPE_DOCX);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/year/{year}/degree/{degreeId}")
    public ResponseEntity<Resource> generateForYear(@PathVariable int year, @PathVariable int degreeId,
                                                    @RequestParam("semester") int semester,
                                                    @RequestParam(required = false) TuitionForm tuitionForm,
                                                    @RequestParam(required = false, defaultValue = "0") int groupId,
                                                    @RequestParam(required = false, defaultValue = "1") int initialNumber)  {
        try {
            File reportsJournal = reportsCoursesService.prepareReportForYear(degreeId, year, semester, tuitionForm, groupId, initialNumber);
            return buildDocumentResponseEntity(reportsJournal, reportsJournal.getName(), MEDIA_TYPE_DOCX);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private static ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, ExamReportsRecordBookController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
