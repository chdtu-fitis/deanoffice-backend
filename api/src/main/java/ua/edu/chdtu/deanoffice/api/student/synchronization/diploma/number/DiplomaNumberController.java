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
import ua.edu.chdtu.deanoffice.api.student.synchronization.diploma.number.dto.DiplomaAndStudentSynchronizedDataDTO;
import ua.edu.chdtu.deanoffice.api.student.synchronization.diploma.number.dto.FormedListsWithDiplomaDataDTO;
import ua.edu.chdtu.deanoffice.api.student.synchronization.diploma.number.dto.MissingDataRedDTO;
import ua.edu.chdtu.deanoffice.api.student.synchronization.edebo.SyncronizationController;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.service.datasync.edebo.diploma.number.EdeboDiplomaNumberSynchronizationReport;
import ua.edu.chdtu.deanoffice.service.datasync.edebo.diploma.number.EdeboDiplomaNumberSynchronizationService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.util.List;

import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.*;

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
            FormedListsWithDiplomaDataDTO listsWithDiplomaData = new FormedListsWithDiplomaDataDTO();
            EdeboDiplomaNumberSynchronizationReport edeboDiplomaNumberSynchronizationReport = edeboDiplomaNumberSynchronizationService.getEdeboDiplomaNumberSynchronizationReport(
                    uploadFile.getInputStream(),
                    user.getFaculty().getName()
            );

            List<DiplomaAndStudentSynchronizedDataDTO> diplomaAndStudentSynchronizedDataDTOs = map(
                    edeboDiplomaNumberSynchronizationReport.getDiplomaAndStudentSynchronizedDataBeans(),
                    DiplomaAndStudentSynchronizedDataDTO.class
            );
            List<MissingDataRedDTO> missingDataRedDTOs = map(
                    edeboDiplomaNumberSynchronizationReport.getMissingDataBeans(),
                    MissingDataRedDTO.class
            );
            listsWithDiplomaData.setDiplomaAndStudentSynchronizedDataDTOs(diplomaAndStudentSynchronizedDataDTOs);
            listsWithDiplomaData.setMissingDataRedDTOs(missingDataRedDTOs);

            return ResponseEntity.ok(listsWithDiplomaData);
        } catch (Exception exception){
            return handleException(exception);
        }

    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, SyncronizationController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
