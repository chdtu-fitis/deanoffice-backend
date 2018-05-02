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
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;
import ua.edu.chdtu.deanoffice.service.document.report.exam.MultiGroupExamReportService;

import java.io.File;
import java.util.List;

import static ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice.handleException;

@RestController
@RequestMapping("/documents/examreport/courses/")
public class MultiGroupExamReportController extends DocumentResponseController {
    private MultiGroupExamReportService multiGroupExamReportService;

    public MultiGroupExamReportController(MultiGroupExamReportService multiGroupExamReportService) {
        this.multiGroupExamReportService = multiGroupExamReportService;
    }

    @GetMapping("{courseId}/docx")
    public ResponseEntity<Resource> generateDocxForSingleCourse(
            @RequestParam List<Integer> groupIds,
            @PathVariable Integer courseId) {
        try {
            File examReport = multiGroupExamReportService.prepareReport(groupIds, courseId, FileFormatEnum.DOCX);
            return buildDocumentResponseEntity(examReport, examReport.getName(), MEDIA_TYPE_DOCX);
        } catch (Exception e) {
            return handleException(e, MultiGroupExamReportController.class);
        }
    }

    @PostMapping("{courseId}/pdf")
    public ResponseEntity<Resource> generateForSingleCourse(
            @RequestParam List<Integer> groupIds,
            @PathVariable Integer courseId
    ) {
        try {
            File examReport = multiGroupExamReportService.prepareReport(groupIds, courseId, FileFormatEnum.PDF);
            return buildDocumentResponseEntity(examReport, examReport.getName(), MEDIA_TYPE_PDF);
        } catch (Exception e) {
            return handleException(e, MultiGroupExamReportController.class);
        }

    }
}
