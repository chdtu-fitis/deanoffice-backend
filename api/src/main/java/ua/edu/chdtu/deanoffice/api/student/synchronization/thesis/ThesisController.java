package ua.edu.chdtu.deanoffice.api.student.synchronization.thesis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.api.student.synchronization.edebo.SyncronizationController;
import ua.edu.chdtu.deanoffice.api.student.synchronization.thesis.dto.AllThesisListsDTO;
import ua.edu.chdtu.deanoffice.api.student.synchronization.thesis.dto.ListThesisDataForGroupDTO;
import ua.edu.chdtu.deanoffice.api.student.synchronization.thesis.dto.MissingThesisDataRedDTO;
import ua.edu.chdtu.deanoffice.api.student.synchronization.thesis.dto.ThesisDataForSaveDTO;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.datasync.thesis.ThesisImportService;
import ua.edu.chdtu.deanoffice.service.datasync.thesis.ThesisReport;
import ua.edu.chdtu.deanoffice.service.security.FacultyAuthorizationService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;

@RestController
@RequestMapping("/students")
public class ThesisController {
    private final ThesisImportService thesisImportService;
    private final StudentDegreeService studentDegreeService;
    private final FacultyAuthorizationService facultyAuthorizationService;

    @Autowired
    public ThesisController(ThesisImportService thesisImportService,
                            StudentDegreeService studentDegreeService,
                            FacultyAuthorizationService facultyAuthorizationService) {
        this.thesisImportService = thesisImportService;
        this.studentDegreeService = studentDegreeService;
        this.facultyAuthorizationService = facultyAuthorizationService;
    }

    @PostMapping("/thesis-import")
    public ResponseEntity importThesis(@RequestParam("file") MultipartFile uploadFile,
                                       @CurrentUser ApplicationUser user) {
        ThesisReport thesisReport;
        try {
            validateInputDataForFileWithTheses(uploadFile);
            AllThesisListsDTO allThesisListsDTO = new AllThesisListsDTO();
            thesisReport = thesisImportService.getThesisImportReport(uploadFile.getInputStream(), user.getFaculty().getId());
            List<ListThesisDataForGroupDTO> importedThesisDataDTOs = map(
                    thesisReport.getThesisDataForImportGreen(),
                    ListThesisDataForGroupDTO.class);
            List<MissingThesisDataRedDTO> missingThesisDataRedDTOs = map(
                    thesisReport.getThesisDataWithMessageRed(),
                    MissingThesisDataRedDTO.class);
            allThesisListsDTO.setListThesisDataForGroupDTOs(importedThesisDataDTOs);
            allThesisListsDTO.setMissingThesisDataRedDTOs(missingThesisDataRedDTOs);
            return ResponseEntity.ok(allThesisListsDTO);
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @PutMapping("/thesis-import")
    public ResponseEntity thesisSaveChanges(@RequestBody ThesisDataForSaveDTO[] thesisDataForSaveDTOs,
                                            @CurrentUser ApplicationUser user) {
        try {
            validateDataAboutThesesForSave(thesisDataForSaveDTOs.length);
            validateStudentDegreeIdWithFacultyId(user, thesisDataForSaveDTOs);
            Map<String, Object> savedThesis = saveThesisData(thesisDataForSaveDTOs);
            return ResponseEntity.ok(savedThesis);
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private Map<String, Object> saveThesisData(ThesisDataForSaveDTO[] thesisDataForSaveDTOs) {
        int count = 0;
        List<String> notSavedStudentsThesises = new ArrayList();
        for (ThesisDataForSaveDTO thesisData : thesisDataForSaveDTOs) {
            if (thesisData.getStudentDegreeId() == 0) {
                continue;
            }
            try {
                studentDegreeService.updateThesisName(thesisData.getStudentDegreeId(), thesisData.getThesisName(),
                        thesisData.getThesisNameEng(), thesisData.getThesisSupervisor());
                count++;
            } catch (Exception e) {
                notSavedStudentsThesises.add(thesisData.getStudentFullName());
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("updatedStudentDegrees", count);
        result.put("notUpdatedStudentDegrees", notSavedStudentsThesises);
        return result;
    }

    private void validateStudentDegreeIdWithFacultyId(ApplicationUser user, ThesisDataForSaveDTO[] thesisDataForSaveDTOs) throws Exception {
        List<Integer> studentDegreeIds = new ArrayList<>();
        for (ThesisDataForSaveDTO thesisData : thesisDataForSaveDTOs) {
            studentDegreeIds.add(thesisData.getStudentDegreeId());
        }
        facultyAuthorizationService.verifyAccessibilityOfStudentDegrees(studentDegreeIds, user);
    }

    private void validateDataAboutThesesForSave(int thesisDataForSaveLenght) throws OperationCannotBePerformedException {
        if (thesisDataForSaveLenght == 0) {
            String message = "Для даної операції необхідно вибрати хоча б одного студента";
            throw new OperationCannotBePerformedException(message);
        }
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