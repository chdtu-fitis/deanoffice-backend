package ua.edu.chdtu.deanoffice.api.document.examreport;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.service.document.statement.ExamReportService;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/documents/statements")
public class ExamReportController extends DocumentResponseController {

    private ExamReportService examReportService;

    public ExamReportController(ExamReportService examReportService) {
        this.examReportService = examReportService;
    }

    @GetMapping(path = "/groups/{groupId}/courses/{courseId}/docx")
    public ResponseEntity<Resource> generateDocxForSingleCourse(
            @PathVariable Integer groupId,
            @PathVariable Integer courseId
    ) throws IOException, Docx4JException {
        File examReport = examReportService.createGroupStatement(groupId, courseId, "docx");
        return buildDocumentResponseEntity(examReport, examReport.getName(), MEDIA_TYPE_DOCX);
    }

    @GetMapping(path = "/groups/{groupId}/courses/{courseId}/pdf")
    public ResponseEntity<Resource> generateForSingleCourse(
            @PathVariable Integer groupId,
            @PathVariable Integer courseId
    ) throws IOException, Docx4JException {
        File examReport = examReportService.createGroupStatement(groupId, courseId, "pdf");
        return buildDocumentResponseEntity(examReport, examReport.getName(), MEDIA_TYPE_PDF);
    }
}
