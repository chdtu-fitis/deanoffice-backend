package ua.edu.chdtu.deanoffice.api.student.syncronization.edebo;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.student.StudentController;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentView;
import ua.edu.chdtu.deanoffice.service.document.importing.EdeboStudentDataSynchronizationServiceImpl;
import ua.edu.chdtu.deanoffice.service.document.importing.ImportReport;

@RestController
@RequestMapping("/students")
public class SyncronizationController {
    private final EdeboStudentDataSynchronizationServiceImpl edeboDataSynchronizationServiceImpl;

    @Autowired
    public SyncronizationController(EdeboStudentDataSynchronizationServiceImpl edeboDataSynchronizationServiceImpl) {
        this.edeboDataSynchronizationServiceImpl = edeboDataSynchronizationServiceImpl;
    }

    @JsonView(StudentView.Degree.class)
    @PostMapping("/import")
//    public ResponseEntity importStudents(@RequestParam("file") MultipartFile uploadfile) {
    public @ResponseBody String importStudents(@RequestParam("file") MultipartFile uploadfile) {
        if (uploadfile.isEmpty()) {
            ResponseEntity.ok().body("No file selected");
        }

        ImportReport importReport = null;

        try {
            importReport = edeboDataSynchronizationServiceImpl.getStudentDegreesFromStream(uploadfile.getInputStream());
//            importDataService.saveImport(importReport);
        } catch (Exception exception) {
            exception.printStackTrace();
            //return handleException(exception);
        }
        if (importReport == null)
            return "import report is not created";
//        else
//            return "insert: "+importReport.getInsertData().size()+" update: "+importReport.getUpdateData().size()+" fail: "+importReport.getFailData().size();
//        return ResponseEntity.ok(parseToImportReportDTO(importReport));
        return null;
    }

//    private SyncronizationReportDTO parseToImportReportDTO(ImportReport importReport) {
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
