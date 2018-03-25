package ua.edu.chdtu.deanoffice.api.document.percentagereport;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.service.document.report.gradepercentage.GradePercentageReportService;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/documents/percentagereport")
public class PercentageReportController extends DocumentResponseController {

    private GradePercentageReportService gradePercentageReportService;

    public PercentageReportController(GradePercentageReportService gradePercentageReportService) {
        this.gradePercentageReportService = gradePercentageReportService;
    }

    @GetMapping(path = "/groups/{groupId}/docx")
    public ResponseEntity<Resource> generateDocxForGroup(@PathVariable Integer groupId)
            throws IOException, Docx4JException {
        File groupReport = gradePercentageReportService.prepareReportForGroup(groupId, "docx");
        return buildDocumentResponseEntity(groupReport, groupReport.getName(), MEDIA_TYPE_DOCX);
    }

    @GetMapping(path = "/groups/{groupId}/pdf")
    public ResponseEntity<Resource> generatePdfForGroup(@PathVariable Integer groupId)
            throws IOException, Docx4JException {
        File groupReport = gradePercentageReportService.prepareReportForGroup(groupId, "pdf");
        return buildDocumentResponseEntity(groupReport, groupReport.getName(), MEDIA_TYPE_PDF);
    }
}
