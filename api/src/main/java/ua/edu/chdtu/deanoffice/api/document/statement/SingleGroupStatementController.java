package ua.edu.chdtu.deanoffice.api.document.statement;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.service.document.statement.SingleGroupStatementService;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/documents/statements")
public class SingleGroupStatementController extends DocumentResponseController {

    private SingleGroupStatementService singleGroupStatementService;

    public SingleGroupStatementController(SingleGroupStatementService singleGroupStatementService) {
        this.singleGroupStatementService = singleGroupStatementService;
    }

    @GetMapping(path = "/groups/{groupId}/courses/{courseId}/{format}")
    public ResponseEntity<Resource> generateForSingleCourse(
            @PathVariable Integer groupId,
            @PathVariable Integer courseId,
            @PathVariable String format
    ) throws IOException, Docx4JException {
        File groupStatement;
        switch (format) {
            case "pdf": {
                groupStatement = singleGroupStatementService.createGroupStatement(groupId, courseId, format);
                return buildDocumentResponseEntity(groupStatement, groupStatement.getName(), MEDIA_TYPE_PDF);
            }
            default: {
                groupStatement = singleGroupStatementService.createGroupStatement(groupId, courseId, format);
                return buildDocumentResponseEntity(groupStatement, groupStatement.getName(), MEDIA_TYPE_DOCX);
            }
        }
    }
}
