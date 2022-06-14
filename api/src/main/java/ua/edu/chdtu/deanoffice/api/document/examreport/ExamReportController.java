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
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.service.FacultyService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;
import ua.edu.chdtu.deanoffice.service.document.report.exam.ExamReportForForeignStudentService;
import ua.edu.chdtu.deanoffice.service.document.report.exam.ExamReportService;
import ua.edu.chdtu.deanoffice.service.document.report.exam.SelectiveCourseExamReportDataService;
import ua.edu.chdtu.deanoffice.service.document.report.exam.beans.ExamReportDataBean;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/documents/exam-report")
public class ExamReportController extends DocumentResponseController {

    private ExamReportService examReportService;
    private FacultyService facultyService;
    private ExamReportForForeignStudentService examReportForForeignStudentService;
    private SelectiveCourseExamReportDataService selectiveCourseExamReportDataService;

    @Autowired
    public ExamReportController(ExamReportService examReportService,
                                FacultyService facultyService,
                                ExamReportForForeignStudentService examReportForForeignStudentService,
                                SelectiveCourseExamReportDataService selectiveCourseExamReportDataService) {
        this.examReportService = examReportService;
        this.facultyService = facultyService;
        this.examReportForForeignStudentService = examReportForForeignStudentService;
        this.selectiveCourseExamReportDataService = selectiveCourseExamReportDataService;
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

    @GetMapping("/selective-courses/docx")
    public ResponseEntity<Resource> generateDocxForSelectiveCourses(
            @RequestParam List<Integer> selectiveCourseIds) throws Exception {
        List<ExamReportDataBean> examReportDataBeans = selectiveCourseExamReportDataService.getExamReportData(selectiveCourseIds);
        File examReport = examReportService.createExamReport(examReportDataBeans, FileFormatEnum.DOCX);
        return buildDocumentResponseEntity(examReport, examReport.getName(), MEDIA_TYPE_DOCX);
    }

    @GetMapping("/foreign/docx")
    public ResponseEntity<Resource> generateDocxForSingleStudent(
            @RequestParam Integer semester,
            @RequestParam List<Integer> studentIds) throws Exception {
        File examReport = examReportForForeignStudentService.createGroupStatementForeign(studentIds, semester, FileFormatEnum.DOCX);
        return buildDocumentResponseEntity(examReport, examReport.getName(), MEDIA_TYPE_DOCX);
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
