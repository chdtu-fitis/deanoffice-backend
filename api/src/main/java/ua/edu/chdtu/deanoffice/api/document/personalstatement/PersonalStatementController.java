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
import java.util.List;

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

    @GetMapping("/front/docx")
    public ResponseEntity<Resource> generatePersonalWrapperFront(@RequestParam List<Integer> studentDegreeIds,
                                                              @CurrentUser ApplicationUser user) {
        try {
            for (Integer studentDegreeId:studentDegreeIds)
                facultyService.checkStudentDegree(studentDegreeId, user.getFaculty().getId());
            File result  = personalStatementService.preparePersonalWrapperFront(studentDegreeIds);
            return buildDocumentResponseEntity(result , result.getName(), MEDIA_TYPE_DOCX);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/back/docx")
    public ResponseEntity<Resource> generatePersonalWrapperBack(@RequestParam List<Integer> studentDegreeIds,
                                                              @CurrentUser ApplicationUser user) {
        try {
            for (Integer studentDegreeId:studentDegreeIds)
                facultyService.checkStudentDegree(studentDegreeId, user.getFaculty().getId());
            File result  = personalStatementService.preparePersonalWrapperBack(studentDegreeIds);
            return buildDocumentResponseEntity(result , result.getName(), MEDIA_TYPE_DOCX);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, GroupGradeReportController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
