package ua.edu.chdtu.deanoffice.api.student.syncronization.edebo;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.student.StudentController;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentView;
import ua.edu.chdtu.deanoffice.service.document.importing.EdeboStudentDataSyncronizationReport;
import ua.edu.chdtu.deanoffice.service.document.importing.EdeboStudentDataSyncronizationService;

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
//    public ResponseEntity studentsEdeboSynchronization(@RequestParam("file") MultipartFile uploadfile) {
    public @ResponseBody String studentsEdeboSynchronization(@RequestParam("file") MultipartFile uploadfile) {
        if (uploadfile.isEmpty()) {
            ResponseEntity.ok().body("No file selected");
        }

        EdeboStudentDataSyncronizationReport edeboDataSyncronizationReport = null;

        try {
            edeboDataSyncronizationReport = edeboDataSynchronizationService.getEdeboDataSynchronizationReport(uploadfile.getInputStream());
//            importDataService.saveImport(edeboDataSyncronizationReport);
        } catch (Exception exception) {
            exception.printStackTrace();
            //return handleException(exception);
        }
        if (edeboDataSyncronizationReport == null)
            return "import report is not created";
//        else
//            return "insert: "+edeboDataSyncronizationReport.getInsertData().size()+" update: "+edeboDataSyncronizationReport.getUpdateData().size()+" fail: "+edeboDataSyncronizationReport.getFailData().size();
//        return ResponseEntity.ok(parseToImportReportDTO(edeboDataSyncronizationReport));
        return null;
    }

//    private SyncronizationReportDTO parseToImportReportDTO(EdeboStudentDataSyncronizationReport importReport) {
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
        return ExceptionHandlerAdvice.handleException(exception, StudentController.class);
    }
}
