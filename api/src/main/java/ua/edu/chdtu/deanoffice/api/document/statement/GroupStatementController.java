package ua.edu.chdtu.deanoffice.api.document.statement;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.service.document.statement.GroupStatementService;

import java.io.File;

@RestController
@RequestMapping("/documents/statements")
public class GroupStatementController extends DocumentResponseController {

    private GroupStatementService groupStatementService;

    public GroupStatementController(GroupStatementService groupStatementService) {
        this.groupStatementService = groupStatementService;
    }

    @GetMapping(path = "/groups/{groupId}/courses/{courseId}")
    public ResponseEntity<Resource> generateForSingleCourse(
            @PathVariable Integer groupId,
            @PathVariable Integer courseId) {
        try {
            File groupStatement = groupStatementService.createGroupStatement(groupId, courseId);
            return buildDocumentResponseEntity(groupStatement, groupStatement.getName());
        } catch (Exception e) {
            return ExceptionHandlerAdvice.handleException(e);
        }
    }
}
