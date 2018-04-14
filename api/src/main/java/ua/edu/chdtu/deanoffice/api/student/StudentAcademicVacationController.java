package ua.edu.chdtu.deanoffice.api.student;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.parser.Parser;
import ua.edu.chdtu.deanoffice.api.student.dto.RenewedAcademicVacationStudentDTO;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentAcademicVacationDTO;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentView;
import ua.edu.chdtu.deanoffice.entity.OrderReason;
import ua.edu.chdtu.deanoffice.entity.RenewedAcademicVacationStudent;
import ua.edu.chdtu.deanoffice.entity.StudentAcademicVacation;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.service.OrderReasonService;
import ua.edu.chdtu.deanoffice.service.StudentAcademicVacationService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;

import java.net.URI;
import java.util.List;

import static ua.edu.chdtu.deanoffice.Constants.FACULTY_ID;
import static ua.edu.chdtu.deanoffice.api.general.Util.getNewResourceLocation;

@RestController
@RequestMapping("/students/degrees/academic-vacations")
public class StudentAcademicVacationController {
    private final StudentAcademicVacationService studentAcademicVacationService;
    private final OrderReasonService orderReasonService;
    private final StudentDegreeService studentDegreeService;
    private final StudentGroupService studentGroupService;

    @Autowired
    public StudentAcademicVacationController(
            StudentAcademicVacationService studentAcademicVacationService,
            OrderReasonService orderReasonService,
            StudentDegreeService studentDegreeService,
            StudentGroupService studentGroupService
    ) {
        this.studentAcademicVacationService = studentAcademicVacationService;
        this.orderReasonService = orderReasonService;
        this.studentDegreeService = studentDegreeService;
        this.studentGroupService = studentGroupService;
    }

    @JsonView(StudentView.AcademicVacation.class)
    @PostMapping("")
    public ResponseEntity giveAcademicVacationToStudent(@RequestBody StudentAcademicVacationDTO studentAcademicVacationDTO) {
        try {
            StudentAcademicVacation studentAcademicVacation = studentAcademicVacationService
                    .giveAcademicVacation(createStudentAcademicVacation(studentAcademicVacationDTO));

            URI location = getNewResourceLocation(studentAcademicVacation.getId());
            return ResponseEntity.created(location).build();
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private StudentAcademicVacation createStudentAcademicVacation(StudentAcademicVacationDTO studentAcademicVacationDTO) {
        StudentAcademicVacation studentAcademicVacation =
                (StudentAcademicVacation) Parser.strictParse(studentAcademicVacationDTO, StudentAcademicVacation.class);

        StudentDegree studentDegree = studentDegreeService.getById(studentAcademicVacationDTO.getStudentDegreeId());
        studentAcademicVacation.setStudentDegree(studentDegree);

        OrderReason orderReason = orderReasonService.getById(studentAcademicVacationDTO.getReasonId());
        studentAcademicVacation.setOrderReason(orderReason);

        return studentAcademicVacation;
    }

    @JsonView(StudentView.AcademicVacation.class)
    @GetMapping("")
    public ResponseEntity getAllAcademicVacations() {
        List<StudentAcademicVacation> academicVacations = studentAcademicVacationService.getAll(FACULTY_ID);
        return ResponseEntity.ok(Parser.parse(academicVacations, StudentAcademicVacationDTO.class));
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, StudentAcademicVacationController.class);
    }

    @PostMapping("/renewed")
    public ResponseEntity renewAcademicVacation(
            @RequestBody RenewedAcademicVacationStudentDTO renewedAcademicVacationStudentDTO
    ) {
        try {
            if (studentAcademicVacationService.inAcademicVacation(renewedAcademicVacationStudentDTO.getStudentAcademicVacationId())) {
                return ExceptionHandlerAdvice.handleException("", StudentAcademicVacation.class);
            }
            Integer id = studentAcademicVacationService
                    .renew(createRenewedAcademicVacationStudent(renewedAcademicVacationStudentDTO))
                    .getId();
            URI location = getNewResourceLocation(id);
            return ResponseEntity.created(location).build();
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private RenewedAcademicVacationStudent createRenewedAcademicVacationStudent(
            RenewedAcademicVacationStudentDTO renewedAcademicVacationStudentDTO
    ) {
        RenewedAcademicVacationStudent renewedAcademicVacationStudent =
                (RenewedAcademicVacationStudent) Parser.strictParse(renewedAcademicVacationStudentDTO, RenewedAcademicVacationStudent.class);

        StudentAcademicVacation studentAcademicVacation =
                studentAcademicVacationService.getById(renewedAcademicVacationStudentDTO.getStudentAcademicVacationId());
        renewedAcademicVacationStudent.setStudentAcademicVacation(studentAcademicVacation);

        StudentGroup studentGroup = studentGroupService.getById(renewedAcademicVacationStudentDTO.getStudentGroupId());
        renewedAcademicVacationStudent.setStudentGroup(studentGroup);

        return renewedAcademicVacationStudent;
    }
}
