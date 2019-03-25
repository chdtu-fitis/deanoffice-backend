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
import ua.edu.chdtu.deanoffice.entity.StudentTransfer;
import ua.edu.chdtu.deanoffice.service.DataVerificationService;
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
    private final DataVerificationService dataVerificationService;

    @Autowired
    public StudentTransferController(
            StudentDegreeService studentDegreeService,
            SpecializationService specializationService,
            StudentTransferService studentTransferService,
            DataVerificationService dataVerificationService
    ){
        this.studentDegreeService = studentDegreeService;
        this.specializationService = specializationService;
        this.studentTransferService = studentTransferService;
        this.dataVerificationService = dataVerificationService;
    }

    @PostMapping
    public ResponseEntity createStudentTransfer(@RequestBody StudentTransferDTO studentTransferDTO,
                                           @CurrentUser ApplicationUser user){
        try{
            StudentTransfer studentTransfer = create(studentTransferDTO);
            dataVerificationService.validateNewGroupExistsAndMatchesSpecialization(studentTransfer.getNewSpecializationId(),studentTransfer.getNewStudentGroupId());
            StudentTransfer studentTransferAfterSaving = studentTransferService.save(studentTransfer);
            dataVerificationService.validateTransferAfterSave(studentTransferAfterSaving);
            StudentTransferDTO studentTransferSavedDTO = Mapper.strictMap(studentTransferAfterSaving, StudentTransferDTO.class);
            studentTransferService.updateSpecializationAndStudentGroupAndPayment(studentTransferAfterSaving.getNewSpecializationId(), studentTransferAfterSaving.getNewStudentGroupId(), studentTransferAfterSaving.getNewPayment(), studentTransferAfterSaving.getStudentDegreeId());
            return new ResponseEntity(studentTransferSavedDTO, HttpStatus.CREATED);
        }catch (Exception exception){
            return handleException(exception);
        }
    }

    private StudentTransfer create(StudentTransferDTO studentTransferDTO){
        StudentTransfer studentTransfer = (StudentTransfer) Mapper.strictMap(studentTransferDTO, StudentTransfer.class);
        studentTransfer.setOldSpecializationId(studentTransferService.getSpecializationIdByStudentDegreeId(studentTransfer.getStudentDegreeId()));
        studentTransfer.setOldStudentGroupId(studentTransferService.getStudentGroupIdByStudentDegreeId(studentTransfer.getStudentDegreeId()));
        studentTransfer.setOldPayment(studentTransferService.getPaymentByStudentDegreeId(studentTransfer.getStudentDegreeId()));
        studentTransfer.setOldStudyYear(studentTransferService.getStudyYear(studentTransfer.getOldStudentGroupId()));
        return studentTransfer;
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, StudentExpelController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
