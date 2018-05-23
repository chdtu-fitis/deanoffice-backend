package ua.edu.chdtu.deanoffice.api.document.percentagereport;

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
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;
import ua.edu.chdtu.deanoffice.service.document.report.gradepercentage.GradePercentageReportService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.io.File;

@RestController
@RequestMapping("/documents/percentagereport")
public class PercentageReportController extends DocumentResponseController {

    private GradePercentageReportService gradePercentageReportService;
    private FacultyService facultyService;

    public PercentageReportController(GradePercentageReportService gradePercentageReportService, FacultyService facultyService) {
        this.gradePercentageReportService = gradePercentageReportService;
        this.facultyService = facultyService;
    }

    @GetMapping("/groups/{groupId}/docx")
    public ResponseEntity<Resource> generateDocxForGroup(@PathVariable Integer groupId,
                                                         @CurrentUser ApplicationUser user) {
        try {
            facultyService.checkGroup(groupId, user.getFaculty().getId());
            File groupReport = gradePercentageReportService.prepareReportForGroup(groupId, FileFormatEnum.DOCX);
            return buildDocumentResponseEntity(groupReport, groupReport.getName(), MEDIA_TYPE_DOCX);
        } catch (Exception e) {
            e.printStackTrace();
            return handleException(e);
        }

    }

    @GetMapping("/groups/{groupId}/pdf")
    public ResponseEntity<Resource> generatePdfForGroup(@PathVariable Integer groupId,
                                                        @CurrentUser ApplicationUser user) {
        try {
            facultyService.checkGroup(groupId, user.getFaculty().getId());
            File groupReport = gradePercentageReportService.prepareReportForGroup(groupId, FileFormatEnum.PDF);
            return buildDocumentResponseEntity(groupReport, groupReport.getName(), MEDIA_TYPE_PDF);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, PercentageReportController.class);
    }
}
