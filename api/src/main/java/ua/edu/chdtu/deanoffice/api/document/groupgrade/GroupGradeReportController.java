package ua.edu.chdtu.deanoffice.api.document.groupgrade;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.service.FacultyService;
import ua.edu.chdtu.deanoffice.service.document.report.groupgrade.SummaryForGroupService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.io.File;


@RestController
@RequestMapping("/documents/grouptablereport")
public class GroupGradeReportController extends DocumentResponseController {

    private SummaryForGroupService summaryForGroupService;
    private FacultyService facultyService;

    public GroupGradeReportController(SummaryForGroupService summaryForGroupService, FacultyService facultyService) {
        this.summaryForGroupService = summaryForGroupService;
        this.facultyService = facultyService;
    }

    @GetMapping("/groups/{group_id}")
    public ResponseEntity<Resource> generateForGroup(
            @PathVariable("group_id") Integer groupId,
            @CurrentUser ApplicationUser user) {
        try {
            facultyService.checkGroup(groupId, user.getFaculty().getId());
            File groupDiplomaSupplements = summaryForGroupService.formDocument(groupId);
            return buildDocumentResponseEntity(groupDiplomaSupplements, groupDiplomaSupplements.getName(), MEDIA_TYPE_DOCX);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, GroupGradeReportController.class);
    }
}
