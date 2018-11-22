package ua.edu.chdtu.deanoffice.api.student.synchronization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.api.general.mapper.Mapper;
import ua.edu.chdtu.deanoffice.api.student.synchronization.edebo.SyncronizationController;
import ua.edu.chdtu.deanoffice.api.student.synchronization.edebo.dto.StudentDegreeFullEdeboDataDto;
import ua.edu.chdtu.deanoffice.api.student.synchronization.thesis.AllThesisListsDTO;
import ua.edu.chdtu.deanoffice.api.student.synchronization.thesis.ImportedThesisDataDTO;
import ua.edu.chdtu.deanoffice.api.student.synchronization.thesis.MissingThesisDataRedDTO;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.datasync.thesis.ThesisImportService;
import ua.edu.chdtu.deanoffice.service.datasync.thesis.ThesisReport;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;

@RestController
@RequestMapping("/thesis")
public class ThesisController {
    private final ThesisImportService thesisImportService;
    private final StudentDegreeService studentDegreeService;

    @Autowired
    public ThesisController(ThesisImportService thesisImportService,
                            StudentDegreeService studentDegreeService){
        this.thesisImportService = thesisImportService;
        this.studentDegreeService = studentDegreeService;
    }

    @PostMapping("/thesis-import")
    public ResponseEntity importThesis(@RequestParam("file") MultipartFile uploadFile,
                                       @CurrentUser ApplicationUser user){
        if (uploadFile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Файл не було надіслано");
        }

        ThesisReport thesisReport;
        try {
            AllThesisListsDTO allThesisListsDTO = new AllThesisListsDTO();
            thesisReport = thesisImportService.getThesisImportReport(uploadFile.getInputStream(), user.getFaculty().getId());
            List<ImportedThesisDataDTO> importedThesisDataDTOs = map(
                    thesisReport.getThesisGreen(),
                    ImportedThesisDataDTO.class);
            List<MissingThesisDataRedDTO> missingThesisDataRedDTOs = map(
                    thesisReport.getThesisRedMessage(),
                    MissingThesisDataRedDTO.class);
            allThesisListsDTO.setImportedThesisDataDTOs(importedThesisDataDTOs);
            allThesisListsDTO.setMissingThesisDataRedDTOs(missingThesisDataRedDTOs);
            return ResponseEntity.ok(allThesisListsDTO);
        } catch (Exception exception){
            return handleException(exception);
        }
    }

    @PostMapping("/thesis-import/update")
    public ResponseEntity thesisSaveChanges(@RequestBody ImportedThesisDataDTO[] importedThesisDataDTOs){
        try {
            Map<String, Object> savedThesis = saveThesisData(importedThesisDataDTOs);
            Map<String, Object> information = new HashMap();
            information.put("saved", savedThesis);
            return ResponseEntity.ok(information);
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private Map saveThesisData(ImportedThesisDataDTO[] importedThesisDataDTOs){
        if(importedThesisDataDTOs.length == 0){
            return null;
        }
        int count = 0;
        List<StudentDegree> studentDegreesWithNewThesisData = new ArrayList<>();
        List<String> notSavedStudentsThesises = new ArrayList();
        for(ImportedThesisDataDTO thesisData: importedThesisDataDTOs){
            if (thesisData.getId() == 0){
                continue;
            }
            StudentDegree studentDegreeOfDb = studentDegreeService.getById(thesisData.getId());
            Mapper.map(thesisData, studentDegreeOfDb);
            try {
                studentDegreesWithNewThesisData.add(studentDegreeOfDb);
                studentDegreeService.update(studentDegreesWithNewThesisData);
                count++;
            }catch (Exception e){
                notSavedStudentsThesises.add(studentDegreeOfDb.getStudent().getSurname()+ " "
                        + studentDegreeOfDb.getStudent().getName()+ " "
                        + studentDegreeOfDb.getStudent().getPatronimic());
            }
        }
        Map <String,Object> result = new HashMap<>();
        result.put("updatedStudentDegrees",count);
        result.put("notUpdatedStudentDegrees",notSavedStudentsThesises);
        return result;
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, SyncronizationController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}