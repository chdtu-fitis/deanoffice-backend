package ua.edu.chdtu.deanoffice.api.document.examreport.multigroup;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.service.FacultyService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;
import ua.edu.chdtu.deanoffice.service.document.report.exam.MultiGroupExamReportService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/documents/examreport/courses/")
public class MultiGroupExamReportController extends DocumentResponseController {

    private MultiGroupExamReportService multiGroupExamReportService;
    private FacultyService facultyService;

    public MultiGroupExamReportController(MultiGroupExamReportService multiGroupExamReportService, FacultyService facultyService) {
        this.multiGroupExamReportService = multiGroupExamReportService;
        this.facultyService = facultyService;
    }

    @GetMapping(path = "{courseId}/docx")
    public ResponseEntity<Resource> generateDocxForSingleCourse(
            @RequestParam List<Integer> groupIds,
            @PathVariable Integer courseId,
            @CurrentUser ApplicationUser user) {
        try {
            checkAllGroupIds(groupIds, user);
            File examReport = multiGroupExamReportService.prepareReport(groupIds, courseId, FileFormatEnum.DOCX);
            return buildDocumentResponseEntity(examReport, examReport.getName(), MEDIA_TYPE_DOCX);
        } catch (Exception e) {
            return ExceptionHandlerAdvice.handleException(e, MultiGroupExamReportController.class);
        }
    }

    @PostMapping(path = "{courseId}/pdf")
    public ResponseEntity<Resource> generateForSingleCourse(
            @RequestParam List<Integer> groupIds,
            @PathVariable Integer courseId,
            @CurrentUser ApplicationUser user) {
        try {
            checkAllGroupIds(groupIds, user);
            File examReport = multiGroupExamReportService.prepareReport(groupIds, courseId, FileFormatEnum.PDF);
            return buildDocumentResponseEntity(examReport, examReport.getName(), MEDIA_TYPE_PDF);
        } catch (Exception e) {
            return ExceptionHandlerAdvice.handleException(e, MultiGroupExamReportController.class);
        }

    }

    private void checkAllGroupIds(@RequestParam List<Integer> groupIds,
                                  @CurrentUser ApplicationUser user)
            throws Exception {
        for (Integer groupId : groupIds) {
            facultyService.checkGroup(groupId, user.getFaculty().getId());
        }
    }
}
