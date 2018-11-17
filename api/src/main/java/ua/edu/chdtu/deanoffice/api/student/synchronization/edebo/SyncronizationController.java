package ua.edu.chdtu.deanoffice.api.student.synchronization.edebo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.api.student.synchronization.edebo.dto.*;
import ua.edu.chdtu.deanoffice.api.general.mapper.Mapper;
import ua.edu.chdtu.deanoffice.api.student.synchronization.edebo.dto.StudentDegreeFullEdeboDataDto;
import ua.edu.chdtu.deanoffice.api.student.synchronization.edebo.dto.UnmatchedSecondaryDataStudentDegreeBlueDTO;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.StudentService;
import ua.edu.chdtu.deanoffice.service.datasync.edebo.student.EdeboStudentDataSynchronizationReport;
import ua.edu.chdtu.deanoffice.service.datasync.edebo.student.EdeboStudentDataSyncronizationService;
import ua.edu.chdtu.deanoffice.api.student.synchronization.edebo.dto.StudentDegreePrimaryEdeboDataDTO;
import ua.edu.chdtu.deanoffice.api.student.synchronization.edebo.dto.MissingPrimaryDataRedDTO;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.*;

@RestController
@RequestMapping("/students")
public class SyncronizationController {
    private EdeboStudentDataSyncronizationService edeboDataSynchronizationService;
    private final StudentDegreeService studentDegreeService;
    private final StudentService studentService;

    @Autowired
    public SyncronizationController(EdeboStudentDataSyncronizationService edeboDataSynchronizationService, StudentDegreeService studentDegreeService,StudentService studentService) {
        this.edeboDataSynchronizationService = edeboDataSynchronizationService;
        this.studentDegreeService = studentDegreeService;
        this.studentService = studentService;
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
            edeboDataSynchronizationReport = edeboDataSynchronizationService.getEdeboDataSynchronizationReport(uploadfile.getInputStream(), user.getFaculty().getId(), selectionParams);
            List<UnmatchedSecondaryDataStudentDegreeBlueDTO> unmatchedSecondaryDataStudentDegreesBlueDTOs = strictMap(
                    edeboDataSynchronizationReport.getUnmatchedSecondaryDataStudentDegreesBlue(),
                    UnmatchedSecondaryDataStudentDegreeBlueDTO.class
            );
            List<StudentDegreeFullEdeboDataDto> noSuchStudentDegreeInDbOrangeDTOs = strictMap(
                    edeboDataSynchronizationReport.getNoSuchStudentOrSuchStudentDegreeInDbOrange(),
                    StudentDegreeFullEdeboDataDto.class
            );
            List<StudentDegreePrimaryEdeboDataDTO> synchronizedStudentDegreesGreen = strictMap(
                    edeboDataSynchronizationReport.getSynchronizedStudentDegreesGreen(),
                    StudentDegreePrimaryEdeboDataDTO.class
            );
            List<MissingPrimaryDataRedDTO> missingPrimaryDataRed = strictMap(
                    edeboDataSynchronizationReport.getMissingPrimaryDataRed(),
                    MissingPrimaryDataRedDTO.class
            );
            List<StudentDegreePrimaryEdeboDataDTO> absentInFileStudentDegreesYellow = strictMap(
                    edeboDataSynchronizationReport.getAbsentInFileStudentDegreesYellow(),
                    StudentDegreePrimaryEdeboDataDTO.class);
            allListsDTO.setNoSuchStudentOrSuchStudentDegreeInDbOrange(noSuchStudentDegreeInDbOrangeDTOs);
            allListsDTO.setUnmatchedSecondaryDataStudentDegreesBlue(unmatchedSecondaryDataStudentDegreesBlueDTOs);
            allListsDTO.setSynchronizedStudentDegreesGreen(synchronizedStudentDegreesGreen);
            allListsDTO.setMissingPrimaryDataRed(missingPrimaryDataRed);
            allListsDTO.setAbsentInFileStudentDegreesYellow(absentInFileStudentDegreesYellow);
            return ResponseEntity.ok(allListsDTO);
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @PostMapping("/edebo-synchronization/save")
    public ResponseEntity studentSaveChanges(@RequestBody NewAndUpdatedStudentDegreesDTO newAndUpdatedStudentDegreesDTO){
        try {
            Map <String,Object> updateInformation = updateSecondaryData(newAndUpdatedStudentDegreesDTO.getStudentDegreesForUpdate());
            Map <String,Object> saveInformation = createNewStudent(newAndUpdatedStudentDegreesDTO.getNewStudentDegrees());
            Map <String,Object> information = new HashMap<String, Object>();
            information.put("updated",updateInformation);
            information.put("created",saveInformation);
            return ResponseEntity.ok(information);
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private Map updateSecondaryData(StudentDegreeFullEdeboDataDto[] studentDegreesForUpdate){
        if (studentDegreesForUpdate.length == 0){
            return null;
        }
        int count = 0;
        List<String> notUpdatedStudentDegrees = new ArrayList();
        for(StudentDegreeFullEdeboDataDto studentDegree: studentDegreesForUpdate){
            if (studentDegree.getId() == 0 || studentDegree.getStudent().getId() == 0){
                continue;
            }
            try {
                if (studentDegree.isModified()) {
                    StudentDegree studentDegreeOfDb = studentDegreeService.getById(studentDegree.getId());
                    Mapper.map(studentDegree, studentDegreeOfDb);
                    studentDegreeService.save(studentDegreeOfDb);
                } else {
                    Student studentOfDb = studentService.findById(studentDegree.getStudent().getId());
                    Mapper.map(studentDegree.getStudent(), studentOfDb);
                    studentService.save(studentOfDb);
                }
                count++;
            } catch (Exception e){
                notUpdatedStudentDegrees.add(studentDegree.getStudent().getSurname()+ " "
                        + studentDegree.getStudent().getName()+ " "
                        + studentDegree.getStudent().getPatronimic()+ " "
                        + studentDegree.getSpecialization().getSpeciality().getCode()+ " "
                        + studentDegree.getSpecialization().getSpeciality().getName());
            }
        }
        Map <String,Object> result = new HashMap<>();
        result.put("updatedStudentDegrees",count);
        result.put("notUpdatedStudentDegrees",notUpdatedStudentDegrees);
        return result;
    }

    private Map createNewStudent(StudentDegreeFullEdeboDataDto[] newStudentDTO){
        if(newStudentDTO.length==0){
            return null;
        }
        int count = 0;
        List <String> notSavedStudents = new ArrayList<>();
        for(StudentDegreeFullEdeboDataDto studentDegreeDTO: newStudentDTO){
            try {
                StudentDegree studentDegree = (StudentDegree) strictMap(studentDegreeDTO, StudentDegree.class);
                studentDegree.setActive(true);
                if (studentDegree.getStudent().getId() == 0) {
                    Student student = studentService.save(studentDegree.getStudent());
                    studentDegree.setStudent(student);
                }
                studentDegreeService.save(studentDegree);
                count++;
            } catch (Exception e){
                notSavedStudents.add(studentDegreeDTO.getStudent().getSurname() + " "
                        +studentDegreeDTO.getStudent().getName()+ " "
                        +studentDegreeDTO.getStudent().getPatronimic()+ " "
                        +studentDegreeDTO.getSpecialization().getSpeciality().getCode()+ " "
                        +studentDegreeDTO.getSpecialization().getSpeciality().getName());
            }
        }
        Map <String,Object> result = new HashMap <String, Object>();
        result.put("createdStudentDegrees", count);
        result.put("notCreatedStudentDegrees",notSavedStudents);
        return result;
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, SyncronizationController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
