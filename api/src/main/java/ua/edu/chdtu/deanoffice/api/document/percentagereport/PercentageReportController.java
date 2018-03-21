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

    @GetMapping(path = "/groups/{groupId}/{format}")
    public ResponseEntity<Resource> generateForGroup(@PathVariable Integer groupId,
                                                     @PathVariable String format)
            throws IOException, Docx4JException {
        File groupReport;
        switch (format) {
            case "pdf": {
                groupReport = gradePercentageReportService.prepareReportForGroup(groupId, format);
                return buildDocumentResponseEntity(groupReport, groupReport.getName(), MEDIA_TYPE_PDF);
            }
            default: {
                groupReport = gradePercentageReportService.prepareReportForGroup(groupId, format);
                return buildDocumentResponseEntity(groupReport, groupReport.getName(), MEDIA_TYPE_DOCX);
            }
        }
    }
}
