package ua.edu.chdtu.deanoffice.api.document.academicdifference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;
import ua.edu.chdtu.deanoffice.service.document.report.exam.CourseForStudentExamReportDataService;
import ua.edu.chdtu.deanoffice.service.document.report.exam.beans.ExamReportDataBean;
import ua.edu.chdtu.deanoffice.service.document.report.exam.ExamReportService;

import java.io.File;
import java.util.List;


@Controller
@RequestMapping("/documents/academic-difference")
public class GroupAcademicDifferenceController extends DocumentResponseController {
    private final CourseForStudentExamReportDataService courseForStudentExamReportDataService;
    private final ExamReportService examReportService;

    @Autowired
    public GroupAcademicDifferenceController(CourseForStudentExamReportDataService courseForStudentExamReportDataService,
                                             ExamReportService examReportService) {
        this.courseForStudentExamReportDataService = courseForStudentExamReportDataService;
        this.examReportService = examReportService;
    }

    @GetMapping("/group/docx")
    public ResponseEntity<Resource> generateDocxForSelectiveCourses(
            @RequestParam List<Integer> coursesForStudentIds) throws Exception {
        courseForStudentExamReportDataService.setCourseIds(coursesForStudentIds);
        List<ExamReportDataBean> examReportDataBeans = courseForStudentExamReportDataService.getExamReportData();
        File examReport = examReportService.createExamReport(examReportDataBeans, FileFormatEnum.DOCX);
        return buildDocumentResponseEntity(examReport, examReport.getName(), MEDIA_TYPE_DOCX);
    }
}
