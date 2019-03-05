package ua.edu.chdtu.deanoffice.api.student.study.year.finish;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.api.student.synchronization.diploma.number.DiplomaNumberController;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.service.DataVerificationService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.StudentExpelService;
import ua.edu.chdtu.deanoffice.service.security.FacultyAuthorizationService;
import ua.edu.chdtu.deanoffice.service.study.year.finish.StudyYearFinishService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/student/study-year-finish")
public class StudyYearFinishController {
    private StudyYearFinishService studyYearFinishService;
    private FacultyAuthorizationService facultyAuthorizationService;
    private DataVerificationService dataVerificationService;
    private StudentDegreeService studentDegreeService;

    @Autowired
    public StudyYearFinishController(StudyYearFinishService studyYearFinishService,
                                     FacultyAuthorizationService facultyAuthorizationService,
                                     DataVerificationService dataVerificationService,
                                     StudentDegreeService studentDegreeService){
        this.studyYearFinishService = studyYearFinishService;
        this.facultyAuthorizationService = facultyAuthorizationService;
        this.dataVerificationService = dataVerificationService;
        this.studentDegreeService = studentDegreeService;
    }

    @PostMapping("/expel-students")
    public ResponseEntity expelStudents(@RequestBody StudyYearFinishDTO studyYearFinishDTO, @CurrentUser ApplicationUser user){
        try {
            facultyAuthorizationService.verifyAccessibilityOfStudentDegrees(studyYearFinishDTO.getIds(), user);
            dataVerificationService.isStudentDegreesActiveByIds(studyYearFinishDTO.getIds());

            List<StudentDegree> studentDegrees = studentDegreeService.getByIds(studyYearFinishDTO.getIds());

            dataVerificationService.existActiveStudentDegreesInInactiveStudentGroups(studentDegrees);

            studyYearFinishService.expelStudents(studentDegrees, studyYearFinishDTO.getExpelDate(), studyYearFinishDTO.getOrderDate(), studyYearFinishDTO.getOrderNumber());

        } catch (Exception e) {
            handleException(e);
        }

        return ResponseEntity.ok().build();
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, StudyYearFinishController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
