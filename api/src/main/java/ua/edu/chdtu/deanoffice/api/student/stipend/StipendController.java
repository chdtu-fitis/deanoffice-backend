package ua.edu.chdtu.deanoffice.api.student.stipend;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.api.general.mapper.Mapper;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.ExtraPoints;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.stipend.DebtorStudentDegreesBean;
import ua.edu.chdtu.deanoffice.service.stipend.StipendService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/student-degree/stipend")
public class StipendController {
    private StipendService stipendService;
    private StudentDegreeService studentDegreeService;

    public StipendController(StipendService stipendService, StudentDegreeService studentDegreeService) {
        this.stipendService = stipendService;
        this.studentDegreeService = studentDegreeService;
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
                    .thenComparing(Collections.reverseOrder(Comparator.comparing(StudentInfoForStipendDTO::getAverageGrade)))
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
    public void updateExtraPointsByStudentDegreeId(Integer studentDegreeId, Integer points){
        List <ExtraPoints> extraPoints = stipendService.getExtraPoints(studentDegreeId);
        if (extraPoints.size()==0){
            ExtraPoints newExtraPoints = create(studentDegreeId, points);
            stipendService.saveExtraPoints(newExtraPoints);
        } else
            stipendService.updateExtraPoints(studentDegreeId, points);
    }



    private ExtraPoints create(Integer studentDegreeId, Integer points){
        ExtraPoints extraPoints = new ExtraPoints();
        StudentDegree studentDegree = studentDegreeService.getById(studentDegreeId);
        Integer semester = (2018 - studentDegree.getStudentGroup().getCreationYear() + studentDegree.getStudentGroup().getBeginYears())* 2 - 1;
        extraPoints.setStudentDegree(studentDegree);
        extraPoints.setSemester(semester);
        extraPoints.setPoints(points);
        return extraPoints;
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, StipendController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
