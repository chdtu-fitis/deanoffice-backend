package ua.edu.chdtu.deanoffice.api.student.synchronization.edebo;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.student.synchronization.edebo.dto.AllListsDTO;
import ua.edu.chdtu.deanoffice.api.general.mapper.Mapper;
import ua.edu.chdtu.deanoffice.api.student.synchronization.edebo.dto.StudentDegreeFullEdeboDataDto;
import ua.edu.chdtu.deanoffice.api.student.synchronization.edebo.dto.UnmatchedSecondaryDataStudentDegreeBlueDTO;
import ua.edu.chdtu.deanoffice.service.datasync.edebo.student.EdeboStudentDataSynchronizationReport;
import ua.edu.chdtu.deanoffice.service.datasync.edebo.student.EdeboStudentDataSyncronizationService;
import ua.edu.chdtu.deanoffice.api.student.synchronization.edebo.dto.StudentDegreePrimaryEdeboDataDTO;
import ua.edu.chdtu.deanoffice.api.student.synchronization.edebo.dto.MissingPrimaryDataRedDTO;
import java.util.List;

import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;

@RestController
@RequestMapping("/students")
public class SyncronizationController {
    private EdeboStudentDataSyncronizationService edeboDataSynchronizationService;

    @Autowired
    public SyncronizationController(EdeboStudentDataSyncronizationService edeboDataSynchronizationService) {
        this.edeboDataSynchronizationService = edeboDataSynchronizationService;
    }

    @PostMapping("/edebo-synchronization")
    public ResponseEntity studentsEdeboSynchronization(@RequestParam("file") MultipartFile uploadfile) {
        if (uploadfile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Файл не було надіслано");
        }

        EdeboStudentDataSynchronizationReport edeboDataSynchronizationReport = null;
        try {
            AllListsDTO allListsDTO = new AllListsDTO();
            edeboDataSynchronizationReport = edeboDataSynchronizationService.getEdeboDataSynchronizationReport(uploadfile.getInputStream());
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

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, SyncronizationController.class);
    }
}
