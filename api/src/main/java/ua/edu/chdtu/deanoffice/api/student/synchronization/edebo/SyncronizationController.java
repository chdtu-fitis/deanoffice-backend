package ua.edu.chdtu.deanoffice.api.student.synchronization.edebo;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentDTO;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentView;
import ua.edu.chdtu.deanoffice.service.datasync.edebo.student.EdeboStudentDataSynchronizationReport;
import ua.edu.chdtu.deanoffice.service.datasync.edebo.student.EdeboStudentDataSyncronizationService;

import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;

@RestController
@RequestMapping("/students")
public class SyncronizationController {
    private EdeboStudentDataSyncronizationService edeboDataSynchronizationService;

    @Autowired
    public SyncronizationController(EdeboStudentDataSyncronizationService edeboDataSynchronizationService) {
        this.edeboDataSynchronizationService = edeboDataSynchronizationService;
    }

    @JsonView(StudentView.Degree.class)
    @PostMapping("/edebo-synchronization")
    public ResponseEntity studentsEdeboSynchronization(@RequestParam("file") MultipartFile uploadfile) {
        if (uploadfile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Файл не було надіслано");
        }

        EdeboStudentDataSynchronizationReport edeboDataSynchronizationReport = null;

        try {
            edeboDataSynchronizationReport = edeboDataSynchronizationService.getEdeboDataSynchronizationReport(uploadfile.getInputStream());
            return ResponseEntity.ok().body(edeboDataSynchronizationReport);
//            importDataService.saveImport(edeboDataSyncronizationReport);
        } catch (Exception exception) {
            return handleException(exception);
        }
//        else
//            return "insert: "+edeboDataSyncronizationReport.getInsertData().size()+" update: "+edeboDataSyncronizationReport.getUpdateData().size()+" fail: "+edeboDataSyncronizationReport.getFailData().size();
//        return ResponseEntity.ok(parseToImportReportDTO(edeboDataSyncronizationReport));
    }

//    private SyncronizationReportDTO parseToImportReportDTO(EdeboStudentDataSynchronizationReport importReport) {
//        ModelMapper modelMapper = new ModelMapper();
//        SyncronizationReportDTO importReportDTO = new SyncronizationReportDTO();
//        importReportDTO.setInsertData(modelMapper.map(importReport.getInsertData(), new TypeToken<List<StudentDegreeDTO>>() {
//        }.getType()));
//        importReportDTO.setUpdateData(modelMapper.map(importReport.getUpdateData(), new TypeToken<List<StudentDegreeDTO>>() {
//        }.getType()));
//        importReportDTO.setFailData(modelMapper.map(importReport.getFailData(), new TypeToken<List<StudentDegreeDTO>>() {
//        }.getType()));
//
//        return importReportDTO;
//    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, SyncronizationController.class);
    }
}
