package ua.edu.chdtu.deanoffice.api.document.graduates_group_report;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;

import java.io.File;

import static ua.edu.chdtu.deanoffice.api.document.DocumentResponseController.MEDIA_TYPE_PDF;
import static ua.edu.chdtu.deanoffice.api.document.DocumentResponseController.buildDocumentResponseEntity;

@RestController
@RequestMapping("/document/")
public class GraduatesGroupReportController {
    @GetMapping("/graduates/{groupId}/report")
    public ResponseEntity getGraduatesGroupReportPdf(@PathVariable int groupId) {
        try {

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
