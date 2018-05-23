package ua.edu.chdtu.deanoffice.api.document.reportsjournal;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.service.FacultyService;
import ua.edu.chdtu.deanoffice.service.document.report.journal.ReportsCoursesService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.io.File;

@RestController
@RequestMapping("/documents/coursereport")
public class ReportsJournalController extends DocumentResponseController {

    private ReportsCoursesService reportsCoursesService;
    private FacultyService facultyService;

    public ReportsJournalController(ReportsCoursesService reportsCoursesService, FacultyService facultyService) {
        this.reportsCoursesService = reportsCoursesService;
        this.facultyService = facultyService;
    }

    //TODO Нужно поправить на /groups/{group_id}/semester/{semester} или лучше на /groups/{group_id}?semester={semester}

    @GetMapping("/groups/{group_id}/{semester}")
    public ResponseEntity<Resource> generateForGroup(
            @PathVariable("group_id") Integer groupId,
            @PathVariable Integer semester,
            @CurrentUser ApplicationUser user) {
        try {
            facultyService.checkGroup(groupId, user.getFaculty().getId());
            File groupDiplomaSupplements = reportsCoursesService.prepareReportForGroup(groupId, semester);
            return buildDocumentResponseEntity(groupDiplomaSupplements, groupDiplomaSupplements.getName(), MEDIA_TYPE_DOCX);
        } catch (Exception e) {
            return handleException(e);
        }

    }

    private static ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, ReportsJournalController.class);
    }
}
