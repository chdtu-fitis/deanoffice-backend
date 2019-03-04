package ua.edu.chdtu.deanoffice.api.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.api.general.mapper.Mapper;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.Specialization;
import ua.edu.chdtu.deanoffice.entity.StudentTransfer;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.service.SpecializationService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.StudentTransferService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

@RestController
@RequestMapping("/students/degrees/transfers")
public class StudentTransferController {
    private final StudentDegreeService studentDegreeService;
    private final SpecializationService specializationService;
    private final StudentTransferService studentTransferService;

    @Autowired
    public StudentTransferController(
            StudentDegreeService studentDegreeService,
            SpecializationService specializationService,
            StudentTransferService studentTransferService
    ){
        this.studentDegreeService = studentDegreeService;
        this.specializationService = specializationService;
        this.studentTransferService = studentTransferService;
    }

    @PostMapping
    public ResponseEntity createStudentTransfer(@RequestBody StudentTransferDTO studentTransferDTO,
                                           @CurrentUser ApplicationUser user){
        try{
            StudentTransfer studentTransfer = create(studentTransferDTO);

            StudentTransfer studentTransferAfterSaving = studentTransferService.save(studentTransfer);
            validateTransferAfterSave(studentTransferAfterSaving);
            StudentTransferDTO studentTransferSavedDTO = Mapper.strictMap(studentTransferAfterSaving, StudentTransferDTO.class);
            studentTransferService.updateSpecialization(studentTransferAfterSaving.getNewSpecializationId(), studentTransferAfterSaving.getStudentDegreeId());
            return new ResponseEntity(studentTransferSavedDTO, HttpStatus.CREATED);
        }catch (Exception exception){
            return handleException(exception);
        }
    }

    private StudentTransfer create(StudentTransferDTO studentTransferDTO){
        StudentTransfer studentTransfer = (StudentTransfer) Mapper.strictMap(studentTransferDTO, StudentTransfer.class);
        Specialization specialization = specializationService.getById(studentTransferDTO.getOldSpecializationId());
        studentTransfer.setNewSpecializationId(studentTransferDTO.getNewSpecializationId());
        return studentTransfer;
    }

    private void validateTransferAfterSave(StudentTransfer studentTransferSaving) throws OperationCannotBePerformedException {
        if (studentTransferSaving == null) {
            throw new OperationCannotBePerformedException("Дані про переведення студента не вдалося зберегти");
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, StudentExpelController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
