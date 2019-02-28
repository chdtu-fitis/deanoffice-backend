package ua.edu.chdtu.deanoffice.api.student.study.year.finish;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
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

    @Autowired
    public StudyYearFinishController(StudyYearFinishService studyYearFinishService,
                                     FacultyAuthorizationService facultyAuthorizationService,
                                     DataVerificationService dataVerificationService){
        this.studyYearFinishService = studyYearFinishService;
        this.facultyAuthorizationService = facultyAuthorizationService;
        this.dataVerificationService = dataVerificationService;
    }

    @PostMapping
    public ResponseEntity expelStudents(List<Integer> ids, Date expelDate, Date orderDate, int orderNumber, @CurrentUser ApplicationUser user){
        try {
            facultyAuthorizationService.verifyAccessibilityOfStudentDegrees(ids, user);
            if (dataVerificationService.isStudentDegreesActiveByIds(ids)) {
                throw new Exception();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
