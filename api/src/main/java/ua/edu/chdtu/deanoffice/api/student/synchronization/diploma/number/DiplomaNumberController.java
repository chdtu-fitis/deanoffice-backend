package ua.edu.chdtu.deanoffice.api.student.synchronization.diploma.number;

import org.springframework.beans.factory.annotation.Autowired;
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
import ua.edu.chdtu.deanoffice.service.datasync.edebo.diploma.number.EdeboDiplomaNumberSynchronizationReport;
import ua.edu.chdtu.deanoffice.service.datasync.edebo.diploma.number.EdeboDiplomaNumberSynchronizationService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

@RestController
@RequestMapping("/students")
public class DiplomaNumberController {
    private EdeboDiplomaNumberSynchronizationService edeboDiplomaNumberSynchronizationService;
    @Autowired
    public DiplomaNumberController(EdeboDiplomaNumberSynchronizationService edeboDiplomaNumberSynchronizationService){
        this.edeboDiplomaNumberSynchronizationService = edeboDiplomaNumberSynchronizationService;
    }

    @PostMapping("/edebo-diploma-number-synchronization/process-file")
    public ResponseEntity diplomaNumberSynchronization(@RequestParam("file") MultipartFile uploadFile,
                                                       @CurrentUser ApplicationUser user){
        try{
            EdeboDiplomaNumberSynchronizationReport edeboDiplomaNumberSynchronizationReport = edeboDiplomaNumberSynchronizationService.getEdeboDiplomaNumberSynchronizationReport(
                    uploadFile.getInputStream(),
                    user.getFaculty().getId(),
                    user.getFirstName()
            );
            return ResponseEntity.ok().body("");
        } catch (Exception exception){
            return handleException(exception);
        }

    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, SyncronizationController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
