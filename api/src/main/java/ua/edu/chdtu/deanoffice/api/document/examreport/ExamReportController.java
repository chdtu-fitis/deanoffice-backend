package ua.edu.chdtu.deanoffice.api.document.examreport;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.service.FacultyService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;
import ua.edu.chdtu.deanoffice.service.document.report.exam.ExamReportService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/documents/exam-report")
public class ExamReportController extends DocumentResponseController {

    private ExamReportService examReportService;
    private FacultyService facultyService;

    public ExamReportController(ExamReportService examReportService, FacultyService facultyService) {
        this.examReportService = examReportService;
        this.facultyService = facultyService;
    }

    @GetMapping("/groups/{groupId}/docx")
    public ResponseEntity<Resource> generateDocxForSingleCourse(
            @PathVariable Integer groupId,
            @RequestParam List<Integer> courseIds,
            @CurrentUser ApplicationUser user) {
        try {
            facultyService.checkGroup(groupId, user.getFaculty().getId());
            File examReport = examReportService.createGroupStatement(groupId, courseIds, FileFormatEnum.DOCX);
            return buildDocumentResponseEntity(examReport, examReport.getName(), MEDIA_TYPE_DOCX);
        } catch (Exception e) {
            return handleException(e);
        }

    }

    @GetMapping("/groups/{groupId}/pdf")
    public ResponseEntity<Resource> generateForSingleCourse(
            @PathVariable Integer groupId,
            @PathVariable List<Integer> courseIds,
            @CurrentUser ApplicationUser user) {
        try {
            facultyService.checkGroup(groupId, user.getFaculty().getId());
            File examReport = examReportService.createGroupStatement(groupId, courseIds, FileFormatEnum.PDF);
            return buildDocumentResponseEntity(examReport, examReport.getName(), MEDIA_TYPE_PDF);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, ExamReportController.class);
    }
}
