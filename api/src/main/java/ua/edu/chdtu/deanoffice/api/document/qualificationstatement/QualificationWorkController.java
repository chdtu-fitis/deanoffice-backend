package ua.edu.chdtu.deanoffice.api.document.qualificationstatement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.service.document.report.qualificationstatement.QualificationWorkService;

import java.io.File;

@RestController
@RequestMapping("/documents/statement")
public class QualificationWorkController  extends DocumentResponseController {
    private QualificationWorkService qualificationWorkService;

    @Autowired
    public QualificationWorkController(QualificationWorkService qualificationWorkService) {
        this.qualificationWorkService = qualificationWorkService;
    }

    @GetMapping("/{groupId}/qualification/work")
    public ResponseEntity getStatementOfQualificationWorkPdf(@PathVariable int groupId) {
        try {
            File file = qualificationWorkService.createQualificationWorkStatementForGroup(groupId);
            return buildDocumentResponseEntity(file, file.getName(), MEDIA_TYPE_PDF);
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, QualificationWorkController.class,
                ExceptionToHttpCodeMapUtil.map(exception));
    }
}

