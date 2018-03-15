package ua.edu.chdtu.deanoffice.api.document.statement;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

//TODO Ось тут вже більше похоже на те, які мають бути адреси ресурсів, але я небагато сумніваюсь на рахунок, чи
// правильна воженість. Якщо файл може видаватись для різних груп, то все ок, в іншому випадку краще переробити
@RestController
@RequestMapping("/documents/statements")
public class SingleGroupStatementController extends DocumentResponseController {

    //TODO потрібно видалити
    private static Logger log = LoggerFactory.getLogger(SingleGroupStatementController.class);

    private SingleGroupStatementService singleGroupStatementService;

    public SingleGroupStatementController(SingleGroupStatementService singleGroupStatementService) {
        this.singleGroupStatementService = singleGroupStatementService;
    }

    //TODO краще якось більш зручно структурувати аргументи в методах, щоб вони не займали дуже багато місця, наприклад так
    @GetMapping(path = "/groups/{groupId}/courses/{courseId}")
    public ResponseEntity<Resource> generateForSingleCourse(
            @PathVariable Integer groupId,
            @PathVariable Integer courseId
    ) throws IOException, Docx4JException {
        File groupStatement = singleGroupStatementService.formGroupStatement(groupId, courseId);
        return buildDocumentResponseEntity(groupStatement, groupStatement.getName());
    }
}
