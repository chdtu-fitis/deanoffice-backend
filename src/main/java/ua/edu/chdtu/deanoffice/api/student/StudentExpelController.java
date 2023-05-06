package ua.edu.chdtu.deanoffice.api.student;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.api.general.mapper.Mapper;
import ua.edu.chdtu.deanoffice.api.student.dto.ExpelledOrRenewedStudentDTO;
import ua.edu.chdtu.deanoffice.api.student.dto.RenewedExpelledStudentDTO;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentExpelDTO;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentView;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.OrderReason;
import ua.edu.chdtu.deanoffice.entity.RenewedExpelledStudent;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentExpel;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.exception.UnauthorizedFacultyDataException;
import ua.edu.chdtu.deanoffice.service.OrderReasonService;
import ua.edu.chdtu.deanoffice.service.RenewedExpelledStudentService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.StudentExpelService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;
import ua.edu.chdtu.deanoffice.util.StudentUtil;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static ua.edu.chdtu.deanoffice.api.general.Util.getNewResourceLocation;

@RestController
@RequestMapping("/students/degrees/expels")
public class StudentExpelController {
    private final RenewedExpelledStudentService renewedExpelledStudentService;
    private final StudentDegreeService studentDegreeService;
    private final OrderReasonService orderReasonService;
    private final StudentExpelService studentExpelService;
    private final StudentGroupService studentGroupService;
    private final StudentUtil studentUtil;

    @Autowired
    public StudentExpelController(
            RenewedExpelledStudentService renewedExpelledStudentService,
            StudentDegreeService studentDegreeService,
            OrderReasonService orderReasonService,
            StudentExpelService studentExpelService,
            StudentGroupService studentGroupService,
            StudentUtil studentUtil
    ) {
        this.renewedExpelledStudentService = renewedExpelledStudentService;
        this.studentDegreeService = studentDegreeService;
        this.orderReasonService = orderReasonService;
        this.studentExpelService = studentExpelService;
        this.studentGroupService = studentGroupService;
        this.studentUtil = studentUtil;
    }

    @Secured("ROLE_DEANOFFICER")
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
                return handleException(new OperationCannotBePerformedException("Студент [" + studentIds + "] не навчається в даний час"));
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

    @Secured("ROLE_DEANOFFICER")
    @GetMapping("/search")
    @JsonView(StudentView.Expel.class)
    public ResponseEntity searchByShortNameAndDate(
            @RequestParam(value = "surname", defaultValue = "", required = false) String surname,
            @RequestParam (value = "name", defaultValue = "", required = false) String name,
            @RequestParam(required = false) String startDate,
            @RequestParam (required = false) String endDate,
            @CurrentUser ApplicationUser user){
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedStartDate = format.parse(startDate);
            Date parsedEndDate;
            if(endDate != null && !endDate.isEmpty()){
                parsedEndDate = format.parse(endDate);
            } else{
                parsedEndDate = new Date();
            }
            List<StudentExpel> foundExcludedStudents = studentExpelService.getSpecificationName(parsedStartDate, parsedEndDate, surname, name, user.getFaculty().getId());
            List<StudentExpelDTO> studentExpelDTOs = Mapper.map(foundExcludedStudents, StudentExpelDTO.class);
            return ResponseEntity.ok(studentExpelDTOs);
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @Secured({"ROLE_DEANOFFICER", "ROLE_STUDENT"})
    @GetMapping("/last")
    @JsonView(StudentView.Expel.class)
    public ResponseEntity<StudentExpelDTO> getLastExpelForStudentDegree(@RequestParam int studentDegreeId) throws UnauthorizedFacultyDataException {
        StudentExpel studentExpel = studentExpelService.getLastByStudentDegreeId(studentDegreeId);
        if (studentExpel != null)
            return ResponseEntity.ok(Mapper.map(studentExpel, StudentExpelDTO.class));
        else
            return ResponseEntity.noContent().build();
    }

    @Secured("ROLE_DEANOFFICER")
    @GetMapping("/{id}/expels-and-renews")
    @JsonView(StudentView.Expel.class)
    public ResponseEntity searchExpelsAndRenewsByStudentDegreeId(
            @PathVariable("id") Integer studentDegreeId,
            @CurrentUser ApplicationUser user){
        try {
           List <StudentExpel> expelledInformation = studentExpelService.getByStudentDegreeId(studentDegreeId);
           List <ExpelledOrRenewedStudentBean> expelledOrRenewedStudentBeans = new ArrayList();
           for (StudentExpel studentExpel : expelledInformation) {
               expelledOrRenewedStudentBeans.add(new ExpelledOrRenewedStudentBean(studentExpel));
               RenewedExpelledStudent renewedInformation = renewedExpelledStudentService.getRenewedStudentByExpelledId(studentExpel.getId());
               if (renewedInformation != null) {
                   expelledOrRenewedStudentBeans.add(new ExpelledOrRenewedStudentBean(renewedInformation));
               }
           }
           List <ExpelledOrRenewedStudentDTO> expelledOrRenewedStudentDTOS = Mapper.map(expelledOrRenewedStudentBeans, ExpelledOrRenewedStudentDTO.class);
           return ResponseEntity.ok(expelledOrRenewedStudentDTOS);
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
        StudentExpel studentExpel = Mapper.strictMap(studentExpelDTO, StudentExpel.class);

        StudentDegree studentDegree = studentDegreeService.getById(studentDegreeId);
        studentExpel.setStudentDegree(studentDegree);

        studentExpel.setPayment(studentDegree.getPayment());
        studentExpel.setStudentGroup(studentDegree.getStudentGroup());

        studentExpel.setOrderReason(studentExpelDTO.getEntityOrderReason());
        studentExpel.setStudyYear(studentUtil.getStudyYear(studentDegree));

        return studentExpel;
    }

    @Secured("ROLE_DEANOFFICER")
    @GetMapping
    @JsonView(StudentView.Expel.class)
    public ResponseEntity getAllExpelledStudents(@CurrentUser ApplicationUser user) {
        try {
            List<StudentExpel> studentExpels = studentExpelService.getAllExpelledStudents(user.getFaculty().getId());
            return ResponseEntity.ok(Mapper.map(studentExpels, StudentExpelDTO.class));
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @Secured("ROLE_DEANOFFICER")
    @PostMapping("/renewed")
    public ResponseEntity renewExpelledStudent(@RequestBody RenewedExpelledStudentDTO renewedExpelledStudentDTO) {
        try {
            StudentExpel studentExpel = studentExpelService.getById(renewedExpelledStudentDTO.getStudentExpelId());
            if (studentUtil.studentDegreeIsActive(studentExpel.getStudentDegree().getId())) {
                return handleException(new OperationCannotBePerformedException("Даний студент не може бути поновлений, тому що він не відрахований"));
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
        RenewedExpelledStudent renewedExpelledStudent = Mapper.strictMap(renewedExpelledStudentDTO, RenewedExpelledStudent.class);
        StudentExpel studentExpel = studentExpelService.getById(renewedExpelledStudentDTO.getStudentExpelId());
        renewedExpelledStudent.setStudentExpel(studentExpel);
        StudentGroup studentGroup = studentGroupService.getById(renewedExpelledStudentDTO.getStudentGroupId());
        renewedExpelledStudent.setStudentGroup(studentGroup);
        renewedExpelledStudent.setStudyYear(studentUtil.getStudyYear(studentGroup));
        return renewedExpelledStudent;
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, StudentExpelController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
