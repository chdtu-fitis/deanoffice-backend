package ua.edu.chdtu.deanoffice.api.document.coursereport;

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
import ua.edu.chdtu.deanoffice.service.document.report.course.CourseReportService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
@RequestMapping("/documents/coursereport")
public class CourseReportContoller {

    private static Logger log = LoggerFactory.getLogger(DiplomaSupplementController.class);

    private CourseReportService courseReportService;

    public CourseReportContoller(CourseReportService courseReportService) {
        this.courseReportService = courseReportService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public void start() {

    }

    @RequestMapping(method = RequestMethod.GET, path = "/groups/{groupId}/{semestrId}")
    public ResponseEntity<Resource> generateForGroup(@PathVariable Integer groupId,@PathVariable Integer semestrId) throws IOException, Docx4JException {
        File groupDiplomaSupplements = courseReportService.prepareReportForGroup(groupId, semestrId);
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
