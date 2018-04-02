package ua.edu.chdtu.deanoffice.api.document.examreport.multigroup;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.api.group.dto.StudentGroupDTO;
import ua.edu.chdtu.deanoffice.service.document.report.exam.MultiGroupExamReportService;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/documents/examreport/courses/")
public class MultiGroupExamReportController extends DocumentResponseController {
    private MultiGroupExamReportService multiGroupExamReportService;

    public MultiGroupExamReportController(MultiGroupExamReportService multiGroupExamReportService) {
        this.multiGroupExamReportService = multiGroupExamReportService;
    }

    @PostMapping(path = "{courseId}/docx")
    public ResponseEntity<Resource> generateDocxForSingleCourse(
            @RequestBody List<StudentGroupDTO> groups,
            @PathVariable Integer courseId
    ) throws IOException, Docx4JException {
        List<Integer> groupIds = groups.stream().map(StudentGroupDTO::getId).collect(Collectors.toList());
        File examReport = multiGroupExamReportService.prepareReport(groupIds, courseId, "docx");
        return buildDocumentResponseEntity(examReport, examReport.getName(), MEDIA_TYPE_DOCX);
    }

    @PostMapping(path = "{courseId}/pdf")
    public ResponseEntity<Resource> generateForSingleCourse(
            @RequestBody List<StudentGroupDTO> groups,
            @PathVariable Integer courseId
    ) throws IOException, Docx4JException {
        List<Integer> groupIds = groups.stream().map(StudentGroupDTO::getId).collect(Collectors.toList());
        File examReport = multiGroupExamReportService.prepareReport(groupIds, courseId, "pdf");
        return buildDocumentResponseEntity(examReport, examReport.getName(), MEDIA_TYPE_PDF);
    }
}
