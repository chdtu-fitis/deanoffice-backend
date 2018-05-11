package ua.edu.chdtu.deanoffice.api.document.groupgrade;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.service.document.report.groupgrade.SummaryForGroupService;

import java.io.File;
import java.io.IOException;


@RestController
@RequestMapping("/documents/grouptablereport")
public class GroupGradeReportController extends DocumentResponseController {

    @Autowired
    private SummaryForGroupService summaryForGroupService;

    @GetMapping("/groups/{group_id}")
    public ResponseEntity<Resource> generateForGroup(
            @PathVariable("group_id") Integer groupId
    ) throws IOException, Docx4JException {
        File groupDiplomaSupplements = summaryForGroupService.formDocument(groupId);
        return buildDocumentResponseEntity(groupDiplomaSupplements, groupDiplomaSupplements.getName(), MEDIA_TYPE_DOCX);
    }
}
