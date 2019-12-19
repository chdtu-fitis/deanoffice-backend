package ua.edu.chdtu.deanoffice.api.document.examreport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.service.FacultyService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;
import ua.edu.chdtu.deanoffice.service.document.report.exam.ExamReportForForeignStudentService;
import ua.edu.chdtu.deanoffice.service.document.report.exam.ExamReportService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/documents/exam-report")
public class ExamReportController extends DocumentResponseController {

    private ExamReportService examReportService;
    private FacultyService facultyService;
    private ExamReportForForeignStudentService examReportForForeignStudentService;


    @Autowired
    public ExamReportController(ExamReportService examReportService,
                                FacultyService facultyService,
                                ExamReportForForeignStudentService examReportForForeignStudentService) {
        this.examReportService = examReportService;
        this.facultyService = facultyService;
        this.examReportForForeignStudentService = examReportForForeignStudentService;
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

    @PostMapping("/student/foreign/examReport/docx")
    public ResponseEntity<Resource> generateDocxForSingleStudent(
            @RequestParam Integer semesterId,
            @RequestBody List<Integer> studentId,
            @CurrentUser ApplicationUser user) {
        try {

            File examReport = examReportForForeignStudentService.createGroupStatementForeign(studentId, semesterId, FileFormatEnum.DOCX);
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
        return ExceptionHandlerAdvice.handleException(exception, ExamReportController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
