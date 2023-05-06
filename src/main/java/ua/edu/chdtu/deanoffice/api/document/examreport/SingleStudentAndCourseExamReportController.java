package ua.edu.chdtu.deanoffice.api.document.examreport;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.document.informal.recordbooks.ExamReportsRecordBookController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;
import ua.edu.chdtu.deanoffice.service.FacultyService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.course.CourseService;
import ua.edu.chdtu.deanoffice.service.document.report.exam.ssc.SingleStudentAndCourseExamReportService;
import ua.edu.chdtu.deanoffice.service.document.report.exam.ssc.StudentCourse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;

@RestController
@RequestMapping("/documents/single-student-and-course-exam-report")
public class SingleStudentAndCourseExamReportController extends DocumentResponseController{
    private FacultyService facultyService;
    private SingleStudentAndCourseExamReportService singleStudentAndCourseExamReportService;
    private StudentDegreeService studentDegreeService;
    private CourseService courseService;
    private CourseForGroupService courseForGroupService;

    public SingleStudentAndCourseExamReportController(FacultyService facultyService, SingleStudentAndCourseExamReportService singleStudentAndCourseExamReportService,
                                                      StudentDegreeService studentDegreeService, CourseService courseService, CourseForGroupService courseForGroupService) {
        this.facultyService = facultyService;
        this.singleStudentAndCourseExamReportService = singleStudentAndCourseExamReportService;
        this.studentDegreeService = studentDegreeService;
        this.courseService = courseService;
        this.courseForGroupService = courseForGroupService;
    }

    @GetMapping
    //[{"studentDegreeId":4546,"courseId":9687},{"studentDegreeId":6671,"courseId":9767}]
    public ResponseEntity<Resource> generateForGroup(@RequestParam String studentsCoursesJson) {
        String ret = null;
        ObjectMapper mapper = new ObjectMapper();
        List<StudentCourse> studentsCourse = new ArrayList<>();
        try {
            StudentCourseDTO[] studentCoursesDTOs = mapper.readValue(studentsCoursesJson, StudentCourseDTO[].class);
            for(StudentCourseDTO studentCourseDTO : studentCoursesDTOs) {
                StudentDegree studentDegree = studentDegreeService.getById(studentCourseDTO.getStudentDegreeId());
                for(Integer courseId : studentCourseDTO.getCourses()) {
                    Course course = courseService.getById(courseId);
                    CourseForGroup courseForGroup = courseForGroupService.getCourseForGroup(studentDegree.getStudentGroup().getId(), course.getId());
                    studentsCourse.add(new StudentCourse(studentDegree, course, courseForGroup));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }try {
            File groupDiplomaSupplements = singleStudentAndCourseExamReportService.formDocument( studentsCourse );
            return buildDocumentResponseEntity(groupDiplomaSupplements, groupDiplomaSupplements.getName(), MEDIA_TYPE_PDF);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private static ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, ExamReportsRecordBookController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
