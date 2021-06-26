package ua.edu.chdtu.deanoffice.api.student;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.api.general.mapper.Mapper;
import ua.edu.chdtu.deanoffice.api.student.dto.RenewedAcademicVacationStudentDTO;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentAcademicVacationDTO;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentView;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.OrderReason;
import ua.edu.chdtu.deanoffice.entity.RenewedAcademicVacationStudent;
import ua.edu.chdtu.deanoffice.entity.StudentAcademicVacation;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.service.OrderReasonService;
import ua.edu.chdtu.deanoffice.service.StudentAcademicVacationService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;
import ua.edu.chdtu.deanoffice.util.StudentUtil;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.net.URI;
import java.util.List;

import static ua.edu.chdtu.deanoffice.api.general.Util.getNewResourceLocation;

@RestController
@RequestMapping("/students/degrees/academic-vacations")
public class StudentAcademicVacationController {
    private final StudentAcademicVacationService studentAcademicVacationService;
    private final OrderReasonService orderReasonService;
    private final StudentDegreeService studentDegreeService;
    private final StudentGroupService studentGroupService;
    private final StudentUtil studentUtil;

    @Autowired
    public StudentAcademicVacationController(
            StudentAcademicVacationService studentAcademicVacationService,
            OrderReasonService orderReasonService,
            StudentDegreeService studentDegreeService,
            StudentGroupService studentGroupService,
            StudentUtil studentUtil
    ) {
        this.studentAcademicVacationService = studentAcademicVacationService;
        this.orderReasonService = orderReasonService;
        this.studentDegreeService = studentDegreeService;
        this.studentGroupService = studentGroupService;
        this.studentUtil = studentUtil;
    }

    @Secured("ROLE_DEANOFFICER")
    @PostMapping
    @JsonView(StudentView.AcademicVacation.class)
    public ResponseEntity giveAcademicVacationToStudent(@RequestBody StudentAcademicVacationDTO studentAcademicVacationDTO) {
        try {
            if (studentUtil.studentDegreeIsInactive(studentAcademicVacationDTO.getStudentDegreeId())) {
                return handleException(new OperationCannotBePerformedException("Студент не навчається в даний час"));
            }
            StudentAcademicVacation studentAcademicVacation = createStudentAcademicVacation(studentAcademicVacationDTO);
            studentAcademicVacation = studentAcademicVacationService.giveAcademicVacation(studentAcademicVacation);

            URI location = getNewResourceLocation(studentAcademicVacation.getId());
            return ResponseEntity.created(location).build();
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private StudentAcademicVacation createStudentAcademicVacation(StudentAcademicVacationDTO studentAcademicVacationDTO) {
        StudentAcademicVacation studentAcademicVacation =
                (StudentAcademicVacation) Mapper.strictMap(studentAcademicVacationDTO, StudentAcademicVacation.class);

        StudentDegree studentDegree = studentDegreeService.getById(studentAcademicVacationDTO.getStudentDegreeId());
        studentAcademicVacation.setStudentDegree(studentDegree);

        OrderReason orderReason = orderReasonService.getById(studentAcademicVacationDTO.getOrderReasonId());
        studentAcademicVacation.setOrderReason(orderReason);

        studentAcademicVacation.setStudentGroup(studentDegree.getStudentGroup());
        studentAcademicVacation.setStudyYear(studentUtil.getStudyYear(studentDegree));

        return studentAcademicVacation;
    }

    @Secured("ROLE_DEANOFFICER")
    @GetMapping
    @JsonView(StudentView.AcademicVacation.class)
    public ResponseEntity getAllAcademicVacations(@CurrentUser ApplicationUser user, @RequestParam(required = false) boolean onlyActive) {
        try {
            List<StudentAcademicVacation> academicVacations = studentAcademicVacationService.getAll(user.getFaculty().getId(), onlyActive);
            return ResponseEntity.ok(Mapper.map(academicVacations, StudentAcademicVacationDTO.class));
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @Secured("ROLE_DEANOFFICER")
    @PostMapping("/renewed")
    public ResponseEntity renewAcademicVacation(
            @RequestBody RenewedAcademicVacationStudentDTO renewedAcademicVacationStudentDTO
    ) {
        try {
            Integer studentDegreeId = this.studentAcademicVacationService
                    .getById(renewedAcademicVacationStudentDTO.getStudentAcademicVacationId())
                    .getStudentDegree().getId();
            if (studentUtil.studentDegreeIsActive(studentDegreeId)) {
                return handleException(new OperationCannotBePerformedException("Даний студент не брав академвідпустки"));
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
        RenewedAcademicVacationStudent renewedAcademicVacationStudent = (RenewedAcademicVacationStudent)
                Mapper.strictMap(renewedAcademicVacationStudentDTO, RenewedAcademicVacationStudent.class);
        StudentAcademicVacation studentAcademicVacation =
                studentAcademicVacationService.getById(renewedAcademicVacationStudentDTO.getStudentAcademicVacationId());
        renewedAcademicVacationStudent.setStudentAcademicVacation(studentAcademicVacation);
        StudentGroup studentGroup = studentGroupService.getById(renewedAcademicVacationStudentDTO.getStudentGroupId());
        renewedAcademicVacationStudent.setStudentGroup(studentGroup);
        renewedAcademicVacationStudent.setStudyYear(studentUtil.getStudyYear(studentGroup));
        return renewedAcademicVacationStudent;
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, StudentAcademicVacationController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
