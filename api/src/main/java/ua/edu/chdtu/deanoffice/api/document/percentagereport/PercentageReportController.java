package ua.edu.chdtu.deanoffice.api.document.percentagereport;

//TODO Дуже великий список імпору. Якщо імпортується більше 2х класів з одного package, то краще імпортувати весь
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.api.document.diplomasupplement.DiplomaSupplementController;
import ua.edu.chdtu.deanoffice.service.document.report.gradepercentage.GradePercentageReportService;

import java.io.File;
import java.io.IOException;

//TODO Такі адреси ресурсу краще не використовувати. Потрібно давати таку адресу, яка буде більш
// зручна та логічна для користувача, а не для того щоб якось назвати. Адреси ресурсів в REST повинні являти собою
// дерево, а одже мати певну вложеність
@RestController
@RequestMapping("/documents/percentagereport")
public class PercentageReportController extends DocumentResponseController {

    //TODO те ж саме. Потрібно прибирати код, який не використувається
    private static Logger log = LoggerFactory.getLogger(DiplomaSupplementController.class);

    private GradePercentageReportService gradePercentageReportService;

    public PercentageReportController(GradePercentageReportService gradePercentageReportService) {
        this.gradePercentageReportService = gradePercentageReportService;
    }

    //TODO Таке краще замінити на @GetMapping("/groups/{groupId}")
    @RequestMapping(method = RequestMethod.GET, path = "/groups/{groupId}")
    public ResponseEntity<Resource> generateForGroup(@PathVariable Integer groupId) throws IOException, Docx4JException {
        File groupDiplomaSupplements = gradePercentageReportService.prepareReportForGroup(groupId);
        return buildDocumentResponseEntity(groupDiplomaSupplements, groupDiplomaSupplements.getName());
    }
}
