package ua.edu.chdtu.deanoffice.api.student;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.Constants;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentExpelDTO;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentView;
import ua.edu.chdtu.deanoffice.entity.OrderReason;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentExpel;
import ua.edu.chdtu.deanoffice.service.OrderReasonService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.StudentExpelService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.modelmapper.convention.MatchingStrategies.STRICT;
import static ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice.handleException;
import static ua.edu.chdtu.deanoffice.api.general.Util.getNewResourceLocation;
import static ua.edu.chdtu.deanoffice.api.general.parser.Parser.parse;

@RestController
@RequestMapping("/students/degrees/")
public class StudentExpelController {
    private final StudentDegreeService studentDegreeService;
    private final OrderReasonService orderReasonService;
    private final StudentExpelService studentExpelService;

    @Autowired
    public StudentExpelController(
            StudentDegreeService studentDegreeService,
            OrderReasonService orderReasonService,
            StudentExpelService studentExpelService
    ) {
        this.studentDegreeService = studentDegreeService;
        this.orderReasonService = orderReasonService;
        this.studentExpelService = studentExpelService;
    }


    @JsonView(StudentView.Expel.class)
    @PostMapping("/expels")
    public ResponseEntity expelStudent(@RequestBody StudentExpelDTO studentExpelDTO) {
        try {
            OrderReason orderReason = orderReasonService.getById(studentExpelDTO.getReasonId());
            List<StudentExpel> studentExpelList = studentDegreeService.expelStudents(createStudentExpels(studentExpelDTO, orderReason));
            List<StudentExpelDTO> studentExpelDTOList = parse(studentExpelList, StudentExpelDTO.class);

            Object[] ids = studentExpelDTOList.stream().map(StudentExpelDTO::getId).toArray();
            URI location = getNewResourceLocation(ids);
            return ResponseEntity.created(location).body(studentExpelDTOList);
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private List<StudentExpel> createStudentExpels(StudentExpelDTO studentExpelDTO, OrderReason orderReason) {
        List<Integer> studentDegreeIds = asList(studentExpelDTO.getStudentDegreeIds());
        return studentDegreeIds.stream()
                .map(id -> createStudentExpel(studentExpelDTO, orderReason, id))
                .collect(Collectors.toList());
    }

    private StudentExpel createStudentExpel(StudentExpelDTO studentExpelDTO, OrderReason orderReason, int studentDegreeId) {
        StudentExpel studentExpel = (StudentExpel) parse(studentExpelDTO, StudentExpel.class, STRICT);
        StudentDegree studentDegree = studentDegreeService.getById(studentDegreeId);
        studentExpel.setStudentDegree(studentDegree);
        studentExpel.setReason(orderReason);
        return studentExpel;
    }

    @GetMapping("/expels")
    @JsonView(StudentView.Expel.class)
    public ResponseEntity getAllStudentExpels() {
        List<StudentExpel> studentExpels = studentDegreeService.getAllExpelStudents(Constants.FACULTY_ID);
        return ResponseEntity.ok(parse(studentExpels, StudentExpelDTO.class));
    }

    @PutMapping("/{student_degree_id}")
    public ResponseEntity returnStudent(@PathVariable("student_degree_id") int studentDegreeId) {
        try {
            if (studentExpelService.studentIsNotExpelled(studentDegreeId)) {
                return ResponseEntity.unprocessableEntity().body("Student (id = " + studentDegreeId + ") is not expelled");
            }
            studentExpelService.returnStudent(studentDegreeId);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            return handleException(exception);
        }
    }
}
