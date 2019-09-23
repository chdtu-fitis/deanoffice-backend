package ua.edu.chdtu.deanoffice.api.document.examreport;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.document.reportsjournal.ReportsJournalController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.service.FacultyService;
import ua.edu.chdtu.deanoffice.service.document.report.exam.ssc.SingleStudentAndCourseExamReportService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.io.File;
import java.util.List;

import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;

@RestController
@RequestMapping("/documents/single-student-and-course-exam-report")
public class SingleStudentAndCourseExamReportController extends DocumentResponseController{
    private FacultyService facultyService;
//    private SingleStudentAndCourseExamReportService singleStudentAndCourseExamReportService;

    public SingleStudentAndCourseExamReportController(FacultyService facultyService) {//}, SingleStudentAndCourseExamReportService singleStudentAndCourseExamReportService) {
        this.facultyService = facultyService;
//        this.singleStudentAndCourseExamReportService = singleStudentAndCourseExamReportService;
    }
//    @GetMapping("/students/{student_ids}/courses/{course_ids}")
//    public ResponseEntity<Resource> generateForGroup(@PathVariable("student_ids") List<Integer> studentIds,
//                                                     @PathVariable("course_ids") List<Integer> courseIds,
//                                                     @CurrentUser ApplicationUser user) {
//        try {
//            //facultyService.checkGroup(groupId, user.getFaculty().getId());
//            File groupDiplomaSupplements = singleStudentAndCourseExamReportService.formDocument(studentIds,courseIds);
//            return buildDocumentResponseEntity(groupDiplomaSupplements, groupDiplomaSupplements.getName(), MEDIA_TYPE_PDF);
//        } catch (Exception e) {
//            return handleException(e);
//        }
//    }

    private static ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, ReportsJournalController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
