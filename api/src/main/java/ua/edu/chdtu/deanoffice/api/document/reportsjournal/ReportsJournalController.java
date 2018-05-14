package ua.edu.chdtu.deanoffice.api.document.reportsjournal;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.api.document.diplomasupplement.DiplomaSupplementController;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.service.document.report.journal.ReportsCoursesService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/documents/coursereport")
public class ReportsJournalController extends DocumentResponseController {

    private static Logger log = LoggerFactory.getLogger(DiplomaSupplementController.class);

    private ReportsCoursesService reportsCoursesService;

    public ReportsJournalController(ReportsCoursesService reportsCoursesService) {
        this.reportsCoursesService = reportsCoursesService;
    }

    @GetMapping("/groups/{groupId}")
    public ResponseEntity<Resource> generateForGroup(@PathVariable Integer groupId,
                                                     @RequestParam("semester") int semester) throws IOException, Docx4JException {
        File reportsJournal = reportsCoursesService.prepareReportForGroup(groupId, semester);
        return buildDocumentResponseEntity(reportsJournal, reportsJournal.getName(),MEDIA_TYPE_DOCX);
    }
    @GetMapping("/year/{yearId}/degree/{degreeId}")
    public ResponseEntity<Resource> generateForYear(@PathVariable Integer yearId,
                                                    @PathVariable Integer degreeId,
                                                    @RequestParam("semester") int semester,
                                                    @CurrentUser ApplicationUser user) throws IOException, Docx4JException {

        File reportsJournal = reportsCoursesService.prepareReportForYear(degreeId,yearId, semester,user.getFaculty().getId());
        return buildDocumentResponseEntity(reportsJournal, reportsJournal.getName(),MEDIA_TYPE_DOCX);
    }
}
