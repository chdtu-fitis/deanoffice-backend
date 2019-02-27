package ua.edu.chdtu.deanoffice.api.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

@RestController
@RequestMapping("/students/degrees/transfers")
public class StudentTransferController {
    private final StudentDegreeService studentDegreeService;

    @Autowired
    public StudentTransferController(
            StudentDegreeService studentDegreeService
    ){
        this.studentDegreeService = studentDegreeService;
    }

    @PostMapping
    public ResponseEntity studentTransfer (@RequestBody StudentTransferDTO studentTransferDTO,
                                           @CurrentUser ApplicationUser user){
        try{

            return null;
        }catch (Exception exception){
            return handleException(exception);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, StudentExpelController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
