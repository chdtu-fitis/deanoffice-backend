package ua.edu.chdtu.deanoffice.api.student.stipend;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.document.groupgrade.GroupGradeReportController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.service.stipend.DebtorStudentDegreesBean;
import ua.edu.chdtu.deanoffice.service.stipend.StipendService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/student-degree/stipend")
public class StipendController {
    private StipendService stipendService;

    public StipendController(StipendService stipendService) {
        this.stipendService = stipendService;
    }

    @GetMapping
    public ResponseEntity<Set<StudentInfoForStipendDto>> getPersonalStatement(@CurrentUser ApplicationUser user) {
        try {
            List<DebtorStudentDegreesBean> debtorStudentDegrees = stipendService.getDebtorStudentDegrees(user.getFaculty().getId(), 1);
            Map<StudentInfoForStipendDto, List<DebtorStudentDegreesBean>> collect = debtorStudentDegrees.stream().collect(Collectors.groupingBy(post -> new StudentInfoForStipendDto(
                    post.getId(),
                    post.getSurname(),
                    post.getName(),
                    post.getPatronimic(),
                    post.getDegreeName(),
                    post.getGroupName(),
                    post.getYear(),
                    post.getTuitionTerm(),
                    post.getSpecialityCode(),
                    post.getSpecialityName(),
                    post.getSpecializationName(),
                    post.getDepartmentAbbreviation(),
                    post.getAverageGrade())));
            collect.forEach((key, value) -> {
                value.forEach(item -> key.getDebtCourses().add(
                        new CourseForStipendDto(item.getCourseName(), item.getKnowledgeControlName(), item.getSemester())
                ));
            });

            return ResponseEntity.ok(collect.keySet());
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, StipendController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
