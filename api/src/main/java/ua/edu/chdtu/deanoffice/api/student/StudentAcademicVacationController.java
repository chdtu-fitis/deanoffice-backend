package ua.edu.chdtu.deanoffice.api.student;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.parser.Parser;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentAcademicVacationDTO;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentView;
import ua.edu.chdtu.deanoffice.entity.OrderReason;
import ua.edu.chdtu.deanoffice.entity.StudentAcademicVacation;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.service.OrderReasonService;
import ua.edu.chdtu.deanoffice.service.StudentAcademicVacationService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;

import java.net.URI;
import java.util.List;

import static ua.edu.chdtu.deanoffice.Constants.FACULTY_ID;
import static ua.edu.chdtu.deanoffice.api.general.Util.getNewResourceLocation;

@RestController
@RequestMapping("/students/degrees")
public class StudentAcademicVacationController {
    private final StudentAcademicVacationService studentAcademicVacationService;
    private final OrderReasonService orderReasonService;
    private final StudentDegreeService studentDegreeService;

    @Autowired
    public StudentAcademicVacationController(
            StudentAcademicVacationService studentAcademicVacationService,
            OrderReasonService orderReasonService,
            StudentDegreeService studentDegreeService
    ) {
        this.studentAcademicVacationService = studentAcademicVacationService;
        this.orderReasonService = orderReasonService;
        this.studentDegreeService = studentDegreeService;
    }

    @JsonView(StudentView.AcademicVacation.class)
    @PostMapping("/academic-vacations")
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
    @GetMapping("/academic-vacations")
    public ResponseEntity getAllAcademicVacations() {
        List<StudentAcademicVacation> academicVacations = studentAcademicVacationService.getAll(FACULTY_ID);
        return ResponseEntity.ok(Parser.parse(academicVacations, StudentAcademicVacationDTO.class));
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, StudentAcademicVacationController.class);
    }

    @PostMapping("{student_degree_id}/academic-vacations")
    public ResponseEntity renewAcademicVacation(@PathVariable("student_degree_id") int studentDegreeId) {
        try {
            if (studentAcademicVacationService.inAcademicVacation(studentDegreeId)) {
                return ExceptionHandlerAdvice.handleException("", StudentAcademicVacation.class);
            }
            studentAcademicVacationService.renew(studentDegreeId);
            URI location = getNewResourceLocation(0);
            return ResponseEntity.created(location).build();
        } catch (Exception exception) {
            return handleException(exception);
        }
    }
}
