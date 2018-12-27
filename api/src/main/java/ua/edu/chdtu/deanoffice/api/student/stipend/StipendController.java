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

@RestController
@RequestMapping("/student-degree/stipend")
public class StipendController {
    private StipendService stipendService;

    public StipendController(StipendService stipendService) {
        this.stipendService = stipendService;
    }

    @GetMapping
    public ResponseEntity<List<DebtorStudentDegreesBean>> getPersonalStatement(@CurrentUser ApplicationUser user) {
        try {
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, StipendController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
