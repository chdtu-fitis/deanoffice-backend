package ua.edu.chdtu.deanoffice.api.document.personalstatement;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.api.document.groupgrade.GroupGradeReportController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.service.FacultyService;
import ua.edu.chdtu.deanoffice.service.document.report.personalstatement.PersonalStatementService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.io.File;
import java.util.Arrays;

@RestController
@RequestMapping("/documents/personal-file-grades-statement")
public class PersonalStatementController extends DocumentResponseController {

    private PersonalStatementService personalStatementService;
    private FacultyService facultyService;

    public PersonalStatementController(PersonalStatementService personalStatementService, FacultyService facultyService) {
        this.personalStatementService = personalStatementService;
        this.facultyService = facultyService;
    }

    @GetMapping(path = "/{year}/docx")
    public ResponseEntity<Resource> getPersonalStatement(@RequestParam Integer[] studentDegreeIds,
                                                         @PathVariable Integer year,
                                                         @CurrentUser ApplicationUser user) {
        try {
            for (Integer studentDegreeId : studentDegreeIds) {
                facultyService.checkStudentDegree(studentDegreeId, user.getFaculty().getId());
            }
            File result = personalStatementService.formDocument(year, Arrays.asList(studentDegreeIds));
            return buildDocumentResponseEntity(result, result.getName(), MEDIA_TYPE_DOCX);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/front/{studentDegreeId}/")
    public ResponseEntity<Resource> generatePersonalWrapperFront(@PathVariable Integer studentDegreeId,
                                                              @CurrentUser ApplicationUser user) {
        try {
            facultyService.checkStudentDegree(studentDegreeId, user.getFaculty().getId());
            File result  = personalStatementService.preparePersonalWrapperFront(studentDegreeId);
            return buildDocumentResponseEntity(result , result .getName(), MEDIA_TYPE_DOCX);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/back/{studentDegreeId}/")
    public ResponseEntity<Resource> generatePersonalWrapperBack(@PathVariable Integer studentDegreeId,
                                                              @CurrentUser ApplicationUser user) {
        try {
            facultyService.checkStudentDegree(studentDegreeId, user.getFaculty().getId());
            File result  = personalStatementService.preparePersonalWrapperBack(studentDegreeId);
            return buildDocumentResponseEntity(result , result .getName(), MEDIA_TYPE_DOCX);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, GroupGradeReportController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
