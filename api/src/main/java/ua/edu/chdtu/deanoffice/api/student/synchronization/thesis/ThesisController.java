package ua.edu.chdtu.deanoffice.api.student.synchronization.thesis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.api.student.synchronization.edebo.SyncronizationController;
import ua.edu.chdtu.deanoffice.api.student.synchronization.thesis.dto.*;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
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
        ThesisReport thesisReport;
        try {
            validateInputDataForFileWithTheses(uploadFile);
            AllThesisListsDTO allThesisListsDTO = new AllThesisListsDTO();
            thesisReport = thesisImportService.getThesisImportReport(uploadFile.getInputStream(), user.getFaculty().getId());
            List<ListThesisDataForGroupDTO> importedThesisDataDTOs = map(
                    thesisReport.getThesisGreen(),
                    ListThesisDataForGroupDTO.class);
            List<MissingThesisDataRedDTO> missingThesisDataRedDTOs = map(
                    thesisReport.getThesisRedMessage(),
                    MissingThesisDataRedDTO.class);
            allThesisListsDTO.setListThesisDataForGroupDTOs(importedThesisDataDTOs);
            allThesisListsDTO.setMissingThesisDataRedDTOs(missingThesisDataRedDTOs);
            return ResponseEntity.ok(allThesisListsDTO);
        } catch (Exception exception){
            return handleException(exception);
        }
    }

    @PostMapping("/thesis-import/update")
    public ResponseEntity thesisSaveChanges(@RequestBody ThesisDataForSaveDTO[] thesisDataForSaveDTOs){
        try {
            Map<String, Object> savedThesis = saveThesisData(thesisDataForSaveDTOs);
            return ResponseEntity.ok(savedThesis);
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private Map saveThesisData(ThesisDataForSaveDTO[] thesisDataForSaveDTOs){
        if(thesisDataForSaveDTOs.length == 0){
            return null;
        }
        int count = 0;
        List<StudentDegree> studentDegreesWithNewThesisData = new ArrayList<>();
        List<String> notSavedStudentsThesises = new ArrayList();
        for(ThesisDataForSaveDTO thesisData: thesisDataForSaveDTOs){
            if (thesisData.getIdStudentDegree() == 0){
                continue;
            }
//              StudentDegree studentDegreeOfDb = studentDegreeService.updateThesisName(thesisData.getIdStudentDegree(),thesisData.getThesisName(),thesisData.getThesisNameEng());;
//            //Mapper.map(thesisData, studentDegreeOfDb);
            try {
                  //studentDegreesWithNewThesisData.add(studentDegreeOfDb);
                  studentDegreeService.updateThesisName(thesisData.getIdStudentDegree(),thesisData.getThesisName(),thesisData.getThesisNameEng());
                  //studentDegreeService.update(studentDegreesWithNewThesisData);
                count++;
            }catch (Exception e){
                StudentDegree studentDegreeOfDb = studentDegreeService.getById(thesisData.getIdStudentDegree());
                notSavedStudentsThesises.add(studentDegreeOfDb.getStudent().getSurname() +" "+studentDegreeOfDb.getStudent().getName()+" "+studentDegreeOfDb.getStudent().getPatronimic());
            }
        }
        Map <String,Object> result = new HashMap<>();
        result.put("updatedStudentDegrees",count);
        result.put("notUpdatedStudentDegrees",notSavedStudentsThesises);
        return result;
    }

    private void validateInputDataForFileWithTheses(MultipartFile uploadFile) throws OperationCannotBePerformedException {
        if (uploadFile.isEmpty()) {
            String message = "Файл не було надіслано";
            throw new OperationCannotBePerformedException(message);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, SyncronizationController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}