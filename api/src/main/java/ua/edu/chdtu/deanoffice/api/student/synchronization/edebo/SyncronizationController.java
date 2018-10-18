package ua.edu.chdtu.deanoffice.api.student.synchronization.edebo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.student.synchronization.edebo.dto.*;
import ua.edu.chdtu.deanoffice.api.general.mapper.Mapper;
import ua.edu.chdtu.deanoffice.api.student.synchronization.edebo.dto.StudentDegreeFullEdeboDataDto;
import ua.edu.chdtu.deanoffice.api.student.synchronization.edebo.dto.UnmatchedSecondaryDataStudentDegreeBlueDTO;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.datasync.edebo.student.EdeboStudentDataSynchronizationReport;
import ua.edu.chdtu.deanoffice.service.datasync.edebo.student.EdeboStudentDataSyncronizationService;
import ua.edu.chdtu.deanoffice.api.student.synchronization.edebo.dto.StudentDegreePrimaryEdeboDataDTO;
import ua.edu.chdtu.deanoffice.api.student.synchronization.edebo.dto.MissingPrimaryDataRedDTO;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;

@RestController
@RequestMapping("/students")
public class SyncronizationController {
    private EdeboStudentDataSyncronizationService edeboDataSynchronizationService;
    private final StudentDegreeService studentDegreeService;

    @Autowired
    public SyncronizationController(EdeboStudentDataSyncronizationService edeboDataSynchronizationService, StudentDegreeService studentDegreeService) {
        this.edeboDataSynchronizationService = edeboDataSynchronizationService;
        this.studentDegreeService = studentDegreeService;
    }

    @PostMapping("/edebo-synchronization/process-file")
    public ResponseEntity studentsEdeboSynchronization(@RequestParam("file") MultipartFile uploadfile,
                                                       @CurrentUser ApplicationUser user,
                                                       @RequestParam(required=false) String degree, @RequestParam(required=false) String speciality) {
        if (uploadfile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Файл не було надіслано");
        }

        EdeboStudentDataSynchronizationReport edeboDataSynchronizationReport = null;
        try {
            AllListsDTO allListsDTO = new AllListsDTO();
            Map<String, String> selectionParams = new HashMap<>();
            selectionParams.put("faculty", user.getFaculty().getName());
            selectionParams.put("degree", degree);
            selectionParams.put("speciality", speciality);
            edeboDataSynchronizationReport = edeboDataSynchronizationService.getEdeboDataSynchronizationReport(uploadfile.getInputStream(), selectionParams);
            List<UnmatchedSecondaryDataStudentDegreeBlueDTO> unmatchedSecondaryDataStudentDegreesBlueDTOs = map(
                    edeboDataSynchronizationReport.getUnmatchedSecondaryDataStudentDegreesBlue(),
                    UnmatchedSecondaryDataStudentDegreeBlueDTO.class
            );
            List<StudentDegreeFullEdeboDataDto> noSuchStudentDegreeInDbOrangeDTOs = map(
                    edeboDataSynchronizationReport.getNoSuchStudentOrSuchStudentDegreeInDbOrange(),
                    StudentDegreeFullEdeboDataDto.class
            );
            List<StudentDegreePrimaryEdeboDataDTO> synchronizedStudentDegreesGreen = map(
                    edeboDataSynchronizationReport.getSynchronizedStudentDegreesGreen(),
                    StudentDegreePrimaryEdeboDataDTO.class
            );
            List<MissingPrimaryDataRedDTO> missingPrimaryDataRed = map(
                    edeboDataSynchronizationReport.getMissingPrimaryDataRed(),
                    MissingPrimaryDataRedDTO.class
            );
            allListsDTO.setNoSuchStudentOrSuchStudentDegreeInDbOrange(noSuchStudentDegreeInDbOrangeDTOs);
            allListsDTO.setUnmatchedSecondaryDataStudentDegreesBlue(unmatchedSecondaryDataStudentDegreesBlueDTOs);
            allListsDTO.setSynchronizedStudentDegreesGreen(synchronizedStudentDegreesGreen);
            allListsDTO.setMissingPrimaryDataRed(missingPrimaryDataRed);
            return ResponseEntity.ok(allListsDTO);
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @PostMapping("/edebo-synchronization/save")
    public ResponseEntity studentSaveChanges(@RequestBody NewAndUpdatedStudentDegreesDTO newAndUpdatedStudentDegreesDTO){
        updateSecondaryData(newAndUpdatedStudentDegreesDTO.getStudentDegreesForUpdate());
        return ResponseEntity.ok(200);
    }

    private void updateSecondaryData(StudentDegreeFullEdeboDataDto[] studentDegreesForUpdate){
        if (studentDegreesForUpdate.length == 0){
            return;
        }
        List<StudentDegree> studentDegreesWithNewData = new ArrayList<>();
        for(StudentDegreeFullEdeboDataDto studentDegree: studentDegreesForUpdate){
            if ((Integer) studentDegree.getId() == null){
                continue;
            }
            StudentDegree studentDegreeOfDb = studentDegreeService.getById(studentDegree.getId());
            Mapper.map(studentDegree, studentDegreeOfDb);
            studentDegreesWithNewData.add(studentDegreeOfDb);
        }
        studentDegreeService.update(studentDegreesWithNewData);
    }


    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, SyncronizationController.class);
    }
}
