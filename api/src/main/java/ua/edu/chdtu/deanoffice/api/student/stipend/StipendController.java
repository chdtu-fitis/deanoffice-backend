package ua.edu.chdtu.deanoffice.api.student.stipend;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.api.general.mapper.Mapper;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.service.stipend.DebtorStudentDegreesBean;
import ua.edu.chdtu.deanoffice.service.stipend.StipendService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/student-degree/stipend")
public class StipendController {
    private StipendService stipendService;

    public StipendController(StipendService stipendService) {
        this.stipendService = stipendService;
    }

    @GetMapping
    public ResponseEntity<List<StudentInfoForStipendDto>> getPersonalStatement(
            @CurrentUser ApplicationUser user) {
        try {
            List<DebtorStudentDegreesBean> debtorStudentDegrees = stipendService
                    .getDebtorStudentDegrees(user.getFaculty().getId(), 1);
            LinkedHashMap<Integer, StudentInfoForStipendDto> debtorStudentDegreesDtosMap = new LinkedHashMap<>();
            debtorStudentDegrees.forEach(dsd -> {
                StudentInfoForStipendDto studentInfoForStipendDto = debtorStudentDegreesDtosMap.get(dsd.getId());
                if (studentInfoForStipendDto == null) {
                    studentInfoForStipendDto = (StudentInfoForStipendDto)Mapper.strictMap(dsd, StudentInfoForStipendDto.class);
                }
                CourseForStipendDto courseForStipendDto = new CourseForStipendDto(
                        dsd.getCourseName(), dsd.getKnowledgeControlName(), dsd.getSemester()
                );
                studentInfoForStipendDto.getDebtCourses().add(courseForStipendDto);
                debtorStudentDegreesDtosMap.put(studentInfoForStipendDto.getId(), studentInfoForStipendDto);
            });
            return ResponseEntity.ok(debtorStudentDegreesDtosMap.values().stream().collect(Collectors.toList()));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, StipendController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
