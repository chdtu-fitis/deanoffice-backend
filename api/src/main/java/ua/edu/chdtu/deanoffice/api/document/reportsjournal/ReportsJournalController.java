package ua.edu.chdtu.deanoffice.api.document.reportsjournal;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.document.diplomasupplement.DiplomaSupplementController;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.service.document.report.journal.ReportsCoursesService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
@RequestMapping("/documents/coursereport")
public class ReportsJournalController {

    private static Logger log = LoggerFactory.getLogger(DiplomaSupplementController.class);

    private ReportsCoursesService reportsCoursesService;

    public ReportsJournalController(ReportsCoursesService reportsCoursesService) {
        this.reportsCoursesService = reportsCoursesService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public void start() {

    }

    @RequestMapping(method = RequestMethod.GET, path = "/groups/{groupId}/{semesterId}")
    public ResponseEntity<Resource> generateForGroup(@PathVariable Integer groupId,@PathVariable Integer semesterId) throws IOException, Docx4JException {
        File groupDiplomaSupplements = reportsCoursesService.prepareReportForGroup(groupId, semesterId);
        return buildDocumentResponseEntity(groupDiplomaSupplements, groupDiplomaSupplements.getName());
    }
    @RequestMapping(method = RequestMethod.GET, path = "/year/{degreeId}/{yearId}/{semesterId}")
    public ResponseEntity<Resource> generateForYear(@PathVariable Integer degreeId, @PathVariable Integer yearId,@PathVariable Integer semesterId,@CurrentUser ApplicationUser user) throws IOException, Docx4JException {

        File groupDiplomaSupplements = reportsCoursesService.prepareReportForYear(degreeId,yearId, semesterId,user.getFaculty().getId());
        return buildDocumentResponseEntity(groupDiplomaSupplements, groupDiplomaSupplements.getName());
    }

    private static ResponseEntity<Resource> buildDocumentResponseEntity(File result, String asciiName) {
        try {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(result));
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + asciiName)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                    .contentLength(result.length())
                    .body(resource);
        } catch (FileNotFoundException e) {
            log.error("Created file not found!", e);
            return ResponseEntity.notFound().build();
        }
    }
}
