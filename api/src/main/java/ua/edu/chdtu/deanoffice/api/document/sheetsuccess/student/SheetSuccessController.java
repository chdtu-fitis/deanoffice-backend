package ua.edu.chdtu.deanoffice.api.document.sheetsuccess.student;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.document.reportsjournal.ReportsJournalController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.service.FacultyService;
import ua.edu.chdtu.deanoffice.service.document.report.sheetsuccess.student.SheetSuccessService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.io.File;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;

@RestController
@RequestMapping("/documents/sheetsuccess")
public class SheetSuccessController extends DocumentResponseController{
    private FacultyService facultyService;
    private SheetSuccessService sheetSuccessService;

    public SheetSuccessController(FacultyService facultyService, SheetSuccessService sheetSuccessService) {
        this.facultyService = facultyService;
        this.sheetSuccessService = sheetSuccessService;
    }
    @GetMapping("/groups")
    public ResponseEntity<Resource> generateForGroup(
                                                     @CurrentUser ApplicationUser user) {
        try {
            //facultyService.checkGroup(groupId, user.getFaculty().getId());
            File groupDiplomaSupplements = sheetSuccessService.formDocument();
            return buildDocumentResponseEntity(groupDiplomaSupplements, groupDiplomaSupplements.getName(), MEDIA_TYPE_PDF);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private static ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, ReportsJournalController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
