package ua.edu.chdtu.deanoffice.api.document.reportsjournal;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.service.document.report.journal.ReportsCoursesService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
@RequestMapping("/documents/coursereport")
public class ReportsJournalController {

    private ReportsCoursesService reportsCoursesService;

    public ReportsJournalController(ReportsCoursesService reportsCoursesService) {
        this.reportsCoursesService = reportsCoursesService;
    }

    private static ResponseEntity<Resource> buildDocumentResponseEntity(File result, String asciiName) {
        try {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(result));
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + asciiName)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                    .contentLength(result.length())
                    .body(resource);
        } catch (FileNotFoundException exception) {
            return handleException(exception);
        }
    } //TODO Чем это отличается от метода в файле DocumentResponseController?

    private static ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, ReportsJournalController.class);
    }

    //TODO Нужно поправить на /groups/{group_id}/semester/{semester} или лучше на /groups/{group_id}?semester={semester}
    @GetMapping("/groups/{group_id}/{semester}")
    public ResponseEntity<Resource> generateForGroup(
            @PathVariable("group_id") Integer groupId,
            @PathVariable Integer semester
    ) throws IOException, Docx4JException {
        File groupDiplomaSupplements = reportsCoursesService.prepareReportForGroup(groupId, semester);
        return buildDocumentResponseEntity(groupDiplomaSupplements, groupDiplomaSupplements.getName());
    }
}
