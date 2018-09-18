package ua.edu.chdtu.deanoffice.api.document.reportsjournal;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.service.FacultyService;
import ua.edu.chdtu.deanoffice.service.document.report.journal.ReportsCoursesService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
@RequestMapping("/documents/course-report")
public class ReportsJournalController {
    private ReportsCoursesService reportsCoursesService;
    private FacultyService facultyService;

    public ReportsJournalController(ReportsCoursesService reportsCoursesService, FacultyService facultyService) {
        this.reportsCoursesService = reportsCoursesService;
        this.facultyService = facultyService;
    }

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

    @GetMapping("/year/{yearId}/degree/{degreeId}")
    public ResponseEntity<Resource> generateForYear(@PathVariable Integer yearId,
                                                    @PathVariable Integer degreeId,
                                                    @RequestParam("semester") int semester,
                                                    @CurrentUser ApplicationUser user) throws IOException, Docx4JException {

        File reportsJournal = reportsCoursesService.prepareReportForYear(degreeId,yearId, semester,user.getFaculty().getId());
        return buildDocumentResponseEntity(reportsJournal, reportsJournal.getName(),MEDIA_TYPE_DOCX);
    }

    private static ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, ReportsJournalController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }

}
