package ua.edu.chdtu.deanoffice.api.student.stipend;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.api.general.mapper.Mapper;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.security.FacultyAuthorizationService;
import ua.edu.chdtu.deanoffice.service.stipend.DebtorStudentDegreesBean;
import ua.edu.chdtu.deanoffice.service.stipend.StipendService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

import static ua.edu.chdtu.deanoffice.api.general.Util.getNewResourceLocation;

@RestController
@RequestMapping("/student-degree/stipend")
public class StipendController {
    private StipendService stipendService;
    private StudentDegreeService studentDegreeService;
    private FacultyAuthorizationService facultyAuthorizationService;

    public StipendController(StipendService stipendService, StudentDegreeService studentDegreeService, FacultyAuthorizationService facultyAuthorizationService) {
        this.stipendService = stipendService;
        this.studentDegreeService = studentDegreeService;
        this.facultyAuthorizationService = facultyAuthorizationService;
    }

    @GetMapping
    public ResponseEntity<List<StudentInfoForStipendDTO>> getPersonalStatement(
            @CurrentUser ApplicationUser user) {
        try {
            List<DebtorStudentDegreesBean> debtorStudentDegrees = stipendService
                    .getDebtorStudentDegrees(user.getFaculty().getId());
            LinkedHashMap<Integer, StudentInfoForStipendDTO> debtorStudentDegreesDTOsMap = new LinkedHashMap<>();
            debtorStudentDegrees.forEach(dsd -> {
                StudentInfoForStipendDTO studentInfoForStipendDTO = debtorStudentDegreesDTOsMap.get(dsd.getId());
                if (studentInfoForStipendDTO == null) {
                    studentInfoForStipendDTO = Mapper.strictMap(dsd, StudentInfoForStipendDTO.class);
                }
                CourseForStipendDTO courseForStipendDto = new CourseForStipendDTO(
                        dsd.getCourseName(), dsd.getKnowledgeControlName(), dsd.getSemester()
                );
                studentInfoForStipendDTO.getDebtCourses().add(courseForStipendDto);
                debtorStudentDegreesDTOsMap.put(studentInfoForStipendDTO.getId(), studentInfoForStipendDTO);
            });
            List<DebtorStudentDegreesBean> noDebtsStudentDegrees = stipendService
                    .getNoDebtStudentDegrees(user.getFaculty().getId(), debtorStudentDegreesDTOsMap.keySet());
            List<StudentInfoForStipendDTO> noDebtsStudentDegreesDTOs = Mapper.map(noDebtsStudentDegrees, StudentInfoForStipendDTO.class);
            noDebtsStudentDegreesDTOs.addAll(new ArrayList<>(debtorStudentDegreesDTOsMap.values()));
            noDebtsStudentDegreesDTOs.sort(Comparator
                    .comparing(StudentInfoForStipendDTO::getDegreeName)
                    .thenComparing(StudentInfoForStipendDTO::getYear)
                    .thenComparing(StudentInfoForStipendDTO::getSpecialityCode)
                    .thenComparing(StudentInfoForStipendDTO::getSpecializationName)
                    .thenComparing(StudentInfoForStipendDTO::getGroupName)
                    //.thenComparing(StudentInfoForStipendDTO::getExtraPoints)
                    .thenComparing(Collections.reverseOrder(Comparator.comparing(StudentInfoForStipendDTO::getFinalGrade)))
                    .thenComparing(StudentInfoForStipendDTO::getSurname)
                    .thenComparing(StudentInfoForStipendDTO::getName)
                    .thenComparing(StudentInfoForStipendDTO::getPatronimic)
            );
            return ResponseEntity.ok(noDebtsStudentDegreesDTOs);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PostMapping("/extra-points-update")
    public ResponseEntity updateExtraPoints(@RequestBody List<ExtraPointsDTO> extraPointsDTO,
                                            @CurrentUser ApplicationUser user) {
        try {
            List<Integer> degreesIds = new ArrayList<>();
            for (ExtraPointsDTO extraPoints : extraPointsDTO) {
                degreesIds.add(extraPoints.getStudentDegreeId());
            }
            facultyAuthorizationService.verifyAccessibilityOfStudentDegrees(degreesIds, user);
            for (ExtraPointsDTO extraPoints : extraPointsDTO) {
                Integer semester = stipendService.getStudentSemester(extraPoints.getStudentDegreeId());
                stipendService.putExtraPoints(extraPoints.getStudentDegreeId(), semester, extraPoints.getPoints());
            }
            URI location = getNewResourceLocation(extraPointsDTO.get(0).getStudentDegreeId());
            return ResponseEntity.created(location).build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, StipendController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
