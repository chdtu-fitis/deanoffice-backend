package ua.edu.chdtu.deanoffice.api.document.examreport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.service.document.report.exam.QualificationWorkReportService;

import java.io.File;

@RestController
@RequestMapping("/documents")
public class QualificationWorkReportController extends DocumentResponseController {
    private QualificationWorkReportService qualificationWorkReportService;

    @Autowired
    public QualificationWorkReportController(QualificationWorkReportService qualificationWorkReportService) {
        this.qualificationWorkReportService = qualificationWorkReportService;
    }

    @GetMapping("/qualification-work-report")
    public ResponseEntity getStatementOfQualificationWorkPdf(@RequestParam Integer groupId) {
        try {
            File file = qualificationWorkReportService.createQualificationWorkStatementForGroup(groupId);
            return buildDocumentResponseEntity(file, file.getName(), MEDIA_TYPE_PDF);
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, QualificationWorkReportController.class,
                ExceptionToHttpCodeMapUtil.map(exception));
    }
}

