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
import ua.edu.chdtu.deanoffice.api.general.mapper.Mapper;
import ua.edu.chdtu.deanoffice.api.student.dto.RenewedExpelledStudentDTO;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentExpelDTO;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentView;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.OrderReason;
import ua.edu.chdtu.deanoffice.entity.RenewedExpelledStudent;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentExpel;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.service.OrderReasonService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.StudentExpelService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;
import ua.edu.chdtu.deanoffice.util.StudentUtil;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static ua.edu.chdtu.deanoffice.api.general.Util.getNewResourceLocation;

@RestController
@RequestMapping("/students/degrees/expels")
public class StudentExpelController {
    private final StudentDegreeService studentDegreeService;
    private final OrderReasonService orderReasonService;
    private final StudentExpelService studentExpelService;
    private final StudentGroupService studentGroupService;
    private final StudentUtil studentUtil;

    @Autowired
    public StudentExpelController(
            StudentDegreeService studentDegreeService,
            OrderReasonService orderReasonService,
            StudentExpelService studentExpelService,
            StudentGroupService studentGroupService,
            StudentUtil studentUtil
    ) {
        this.studentDegreeService = studentDegreeService;
        this.orderReasonService = orderReasonService;
        this.studentExpelService = studentExpelService;
        this.studentGroupService = studentGroupService;
        this.studentUtil = studentUtil;
    }


    @JsonView(StudentView.Expel.class)
    @PostMapping
    public ResponseEntity expelStudent(@RequestBody StudentExpelDTO studentExpelDTO) {
        try {
            List<Integer> inactiveStudents = studentExpelService.isExpelled(studentExpelDTO.getStudentDegreeIds());
            boolean hasInactive = !inactiveStudents.isEmpty();
            if (hasInactive) {
                String studentIds = inactiveStudents.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));
                return handleException("Student [" + studentIds + "] is not active");
            }

            List<StudentExpel> studentExpelList = studentExpelService.expelStudents(createStudentExpels(studentExpelDTO));
            List<StudentExpelDTO> studentExpelDTOs = Mapper.map(studentExpelList, StudentExpelDTO.class);

            Object[] ids = studentExpelDTOs.stream().map(StudentExpelDTO::getId).toArray();
            URI location = getNewResourceLocation(ids);
            return ResponseEntity.created(location).build();
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private List<StudentExpel> createStudentExpels(StudentExpelDTO studentExpelDTO) {
        OrderReason orderReason = orderReasonService.getById(studentExpelDTO.getOrderReasonId());
        studentExpelDTO.setEntityOrderReason(orderReason);

        List<Integer> studentDegreeIds = asList(studentExpelDTO.getStudentDegreeIds());
        return studentDegreeIds.stream()
                .map(id -> createStudentExpel(studentExpelDTO, id))
                .collect(Collectors.toList());
    }

    private StudentExpel createStudentExpel(StudentExpelDTO studentExpelDTO, int studentDegreeId) {
        StudentExpel studentExpel = (StudentExpel) Mapper.strictMap(studentExpelDTO, StudentExpel.class);

        StudentDegree studentDegree = studentDegreeService.getById(studentDegreeId);
        studentExpel.setStudentDegree(studentDegree);

        studentExpel.setPayment(studentDegree.getPayment());
        studentExpel.setStudentGroup(studentDegree.getStudentGroup());

        studentExpel.setOrderReason(studentExpelDTO.getEntityOrderReason());
        studentExpel.setStudyYear(studentUtil.getStudyYear(studentDegree));

        return studentExpel;
    }

    @GetMapping
    @JsonView(StudentView.Expel.class)
    public ResponseEntity getAllExpelledStudents(@CurrentUser ApplicationUser user) {
        List<StudentExpel> studentExpels = studentExpelService.getAllExpelledStudents(user.getFaculty().getId());
        return ResponseEntity.ok(Mapper.map(studentExpels, StudentExpelDTO.class));
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, StudentExpelController.class);
    }

    private ResponseEntity handleException(String message) {
        return ExceptionHandlerAdvice.handleException(message, StudentExpelController.class);
    }

    @PostMapping("/renewed")
    public ResponseEntity renewExpelledStudent(@RequestBody RenewedExpelledStudentDTO renewedExpelledStudentDTO) {
        try {
            if (studentUtil.studentDegreeIsActive(renewedExpelledStudentDTO.getStudentExpelId())) {
                return handleException("Student is not expelled");
            }
            Integer id = studentExpelService
                    .renew(createRenewedExpelledStudent(renewedExpelledStudentDTO))
                    .getId();
            URI location = getNewResourceLocation(id);
            return ResponseEntity.created(location).build();
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private RenewedExpelledStudent createRenewedExpelledStudent(RenewedExpelledStudentDTO renewedExpelledStudentDTO) {
        RenewedExpelledStudent renewedExpelledStudent =
                (RenewedExpelledStudent) Mapper.strictMap(renewedExpelledStudentDTO, RenewedExpelledStudent.class);

        StudentExpel studentExpel = studentExpelService.getById(renewedExpelledStudentDTO.getStudentExpelId());
        renewedExpelledStudent.setStudentExpel(studentExpel);

        StudentGroup studentGroup = studentGroupService.getById(renewedExpelledStudentDTO.getStudentGroupId());
        renewedExpelledStudent.setStudentGroup(studentGroup);

        renewedExpelledStudent.setStudyYear(studentUtil.getStudyYear(studentGroup));

        return renewedExpelledStudent;
    }
}
