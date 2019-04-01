package ua.edu.chdtu.deanoffice.api.document.graduates_group_report;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.service.document.report.graduates.group.GraduatesGroupReportService;

import java.io.File;

@RestController
@RequestMapping("/document/")
public class GraduatesGroupReportController {
    private GraduatesGroupReportService graduatesGroupReportService;

    public GraduatesGroupReportController(GraduatesGroupReportService graduatesGroupReportService) {
        this.graduatesGroupReportService = graduatesGroupReportService;
    }

    @GetMapping("/graduates/{groupId}/report")
    public ResponseEntity getGraduatesReportForGroupPdf(@PathVariable int groupId) {
        try {
            File file = graduatesGroupReportService.getGraduatesReportForGroupPdf(groupId);
            return null;
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, GraduatesGroupReportController.class,
                ExceptionToHttpCodeMapUtil.map(exception));
    }
}
