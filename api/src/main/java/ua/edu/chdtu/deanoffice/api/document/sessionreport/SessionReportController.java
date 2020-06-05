package ua.edu.chdtu.deanoffice.api.document.sessionreport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.api.document.examreport.ConsolidatedExamReportController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.service.document.sessionreport.SessionReportService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.io.File;

@RestController
@RequestMapping("/documents/session-report")
public class SessionReportController extends DocumentResponseController {

    private final SessionReportService sessionReportService;

    @Autowired
    public SessionReportController(SessionReportService sessionReportService) {
        this.sessionReportService = sessionReportService;
    }

    @GetMapping
    public ResponseEntity<Resource> getSessionReportInExcel(@CurrentUser ApplicationUser user) {
        try {
            File sessionReport = sessionReportService.createSessionReportInXLSX(user);

            return buildDocumentResponseEntity(sessionReport, sessionReport.getName(), MEDIA_TYPE_XLSX);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private static ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, SessionReportController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }

}
