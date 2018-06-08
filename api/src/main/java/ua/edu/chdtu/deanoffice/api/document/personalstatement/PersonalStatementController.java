package ua.edu.chdtu.deanoffice.api.document.personalstatement;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.api.document.groupgrade.GroupGradeReportController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.service.FacultyService;
import ua.edu.chdtu.deanoffice.service.document.report.personalstatement.PersonalStatementService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.io.File;
import java.util.Arrays;

@RestController
@RequestMapping("/documents/personalstatement")
public class PersonalStatementController extends DocumentResponseController {

    private PersonalStatementService personalStatementService;
    private FacultyService facultyService;

    public PersonalStatementController(PersonalStatementService personalStatementService, FacultyService facultyService) {
        this.personalStatementService = personalStatementService;
        this.facultyService = facultyService;
    }

    @GetMapping(path = "/{year}/docx")
    public ResponseEntity<Resource> getPersonalStatement(@RequestParam Integer[] groupIds,
                                                         @PathVariable Integer year,
                                                         @CurrentUser ApplicationUser user) {
        try {
            for (Integer groupId : groupIds) {
                facultyService.checkGroup(groupId, user.getFaculty().getId());
            }
            File result = personalStatementService.formDocument(year, Arrays.asList(groupIds));
            return buildDocumentResponseEntity(result, result.getName(), MEDIA_TYPE_DOCX);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, GroupGradeReportController.class);
    }


}
