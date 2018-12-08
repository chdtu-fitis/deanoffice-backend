package ua.edu.chdtu.deanoffice.api.student.synchronization.diploma.number;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.api.student.synchronization.edebo.SyncronizationController;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

@RestController
@RequestMapping("/students")
public class DiplomaNumberController {

    public DiplomaNumberController(){

    }

    @PostMapping("/edebo-diploma-number-synchronization/process-file")
    public ResponseEntity diplomaNumberSynchronization(@RequestParam("file") MultipartFile uploadFile,
                                                       @CurrentUser ApplicationUser user){
        try{

            return ResponseEntity.ok().body("");
        } catch (Exception exception){
            return handleException(exception);
        }

    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, SyncronizationController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
