package ua.edu.chdtu.deanoffice.api.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.api.order.dto.StudentSurnameChangeInputDTO;
import ua.edu.chdtu.deanoffice.api.order.dto.StudentSurnameChangeOutputDTO;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.StudentSurnameChange;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.service.FacultyService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.order.StudentSurnameChangeService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.util.List;

import static java.util.Objects.isNull;
import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.strictMap;

@RestController
@RequestMapping("/order/student-surname-change")
public class StudentSurnameChangeController {

    private final StudentSurnameChangeService studentSurnameChangeService;
    private final FacultyService facultyService;
    private final StudentDegreeService studentDegree;


    @Autowired
    public StudentSurnameChangeController(StudentSurnameChangeService studentSurnameChangeService,
                                          FacultyService facultyService,
                                          StudentDegreeService studentDegree) {
        this.studentSurnameChangeService = studentSurnameChangeService;
        this.facultyService = facultyService;
        this.studentDegree = studentDegree;
    }

    @GetMapping
    public ResponseEntity getAllForUserWhichHasEqualsSurname( @CurrentUser ApplicationUser user) {
        List<StudentSurnameChange> all = studentSurnameChangeService.getAll();
        return ResponseEntity.ok(strictMap(all, StudentSurnameChangeOutputDTO.class));
    }

    @PostMapping
    public ResponseEntity addStudentSurnameChange(@RequestBody StudentSurnameChangeInputDTO studentSurnameChangeInputDTO,
                                                  @CurrentUser ApplicationUser user) {

        try {
            if (isNull(studentSurnameChangeInputDTO))
                throw new OperationCannotBePerformedException("");
            StudentSurnameChange studentSurnameChange = strictMap(studentSurnameChangeInputDTO, StudentSurnameChange.class);


            studentSurnameChange.setFaculty(facultyService.getById(verifyByNull(studentSurnameChangeInputDTO.getFacultyId())));
            studentSurnameChange.setStudentDegree(studentDegree.getById(verifyByNull(studentSurnameChangeInputDTO.getStudentDegreeId())));
            studentSurnameChangeService.saveStudentSurnameChange(user, studentSurnameChange);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return handleException(e);
        }
    }


    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception,
                StudentSurnameChangeController.class,
                ExceptionToHttpCodeMapUtil.map(exception));
    }

    private Integer verifyByNull(Integer id) throws OperationCannotBePerformedException {
        if (isNull(id)) {
            throw new OperationCannotBePerformedException("");
        }
        return id;
    }
}
