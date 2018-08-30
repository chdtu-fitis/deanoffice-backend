package ua.edu.chdtu.deanoffice.api.student;

import com.fasterxml.jackson.annotation.JsonView;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.student.dto.ImportReportDTO;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentDegreeDTO;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentView;
import ua.edu.chdtu.deanoffice.service.document.importing.ImportDataService;
import ua.edu.chdtu.deanoffice.service.document.importing.ImportReport;

import java.util.List;

@RestController
@RequestMapping("/students")
public class ImportController {
    private final ImportDataService importDataService;

    @Autowired
    public ImportController(ImportDataService importDataService) {
        this.importDataService = importDataService;
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
            importReport = importDataService.getStudentsFromStream(uploadfile.getInputStream());
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

//    private ImportReportDTO parseToImportReportDTO(ImportReport importReport) {
//        ModelMapper modelMapper = new ModelMapper();
//        ImportReportDTO importReportDTO = new ImportReportDTO();
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
