package ua.edu.chdtu.deanoffice.api.document.sessionreport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.service.document.sessionreport.SessionReportService;

@RestController
@RequestMapping("/documents/session-report")
public class SessionReportController {

    private final SessionReportService sessionReportService;
    
    @Autowired
    public SessionReportController(SessionReportService sessionReportService) {
        this.sessionReportService = sessionReportService;
    }

}
