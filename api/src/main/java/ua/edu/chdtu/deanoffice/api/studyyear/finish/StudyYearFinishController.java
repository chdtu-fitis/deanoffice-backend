package ua.edu.chdtu.deanoffice.api.studyyear.finish;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.api.general.dto.NamedDTO;
import ua.edu.chdtu.deanoffice.api.general.mapper.Mapper;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentDegreeShortBean;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.service.DataVerificationService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.security.FacultyAuthorizationService;
import ua.edu.chdtu.deanoffice.service.study.year.finish.StudyYearFinishService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.util.*;

@RestController
@RequestMapping("/study-year-finish")
public class StudyYearFinishController {
    private StudyYearFinishService studyYearFinishService;
    private FacultyAuthorizationService facultyAuthorizationService;
    private DataVerificationService dataVerificationService;
    private StudentDegreeService studentDegreeService;

    @Autowired
    public StudyYearFinishController(StudyYearFinishService studyYearFinishService,
                                     FacultyAuthorizationService facultyAuthorizationService,
                                     DataVerificationService dataVerificationService,
                                     StudentDegreeService studentDegreeService){
        this.studyYearFinishService = studyYearFinishService;
        this.facultyAuthorizationService = facultyAuthorizationService;
        this.dataVerificationService = dataVerificationService;
        this.studentDegreeService = studentDegreeService;
    }

    @Secured("ROLE_DEANOFFICER")
    @PostMapping
    public ResponseEntity finishStudyYear(@RequestBody StudyYearFinishDTO studyYearFinishDTO, @CurrentUser ApplicationUser user){
        try {
            if (studyYearFinishDTO == null)
                throw new OperationCannotBePerformedException("Не передана форма");
            if (studyYearFinishDTO.getIds().size() == 0)
                throw new OperationCannotBePerformedException("Не вказані ідентифікатори");
            if (studyYearFinishDTO.getExpelDate() == null)
                throw new OperationCannotBePerformedException("Не вказана дата відрахування");
            if (studyYearFinishDTO.getOrderDate() == null)
                throw new OperationCannotBePerformedException("Не вказана дата наказу");
            if (studyYearFinishDTO.getOrderNumber() == "")
                throw new OperationCannotBePerformedException("Номер наказу");
            facultyAuthorizationService.verifyAccessibilityOfStudentDegrees(studyYearFinishDTO.getIds(), user);
            dataVerificationService.isStudentDegreesActiveByIds(studyYearFinishDTO.getIds());
            List<StudentDegree> studentDegrees = studentDegreeService.getByIds(studyYearFinishDTO.getIds());
            if (studentDegrees.size() != studyYearFinishDTO.getIds().size()) {
                throw new OperationCannotBePerformedException("Серед даних ідентифікаторів є неіснуючі в базі даних");
            }
            dataVerificationService.existActiveStudentDegreesInInactiveStudentGroups(studentDegrees);
            Set<StudentGroup> deletedStudentGroups = studyYearFinishService.expelStudentsAndDisableGroups(studentDegrees, studyYearFinishDTO.getExpelDate(), studyYearFinishDTO.getOrderDate(), studyYearFinishDTO.getOrderNumber());
            Set<NamedDTO> deletedStudentGroupsDTO = Mapper.strictMap(deletedStudentGroups, NamedDTO.class);
            return ResponseEntity.ok().body(deletedStudentGroupsDTO);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @Secured("ROLE_DEANOFFICER")
    @GetMapping("/graduate-student-degrees")
    public ResponseEntity<Map<String, List<StudentDegreeShortDTO>>> getShortGraduateStudentDegree(@RequestParam List<Integer> groupIds) {
        try {
            dataVerificationService.areStudentGroupsActiveByIds(groupIds);
            dataVerificationService.areGroupsGraduate(groupIds);

            Map<String, List<StudentDegreeShortBean>> groupsWithStudents = studentDegreeService.getStudentsShortInfoGroupedByGroupNames(groupIds);
            Map<String, List<StudentDegreeShortDTO>> groupsWithStudentsDTO = new HashMap<>();

            for (Map.Entry<String, List<StudentDegreeShortBean>> groupWithStudents : groupsWithStudents.entrySet()) {
                List<StudentDegreeShortDTO> studentsDTO = Mapper.strictMap(groupWithStudents.getValue(), StudentDegreeShortDTO.class);
                groupsWithStudentsDTO.put(groupWithStudents.getKey(), studentsDTO);
            }

            return ResponseEntity.ok().body(groupsWithStudentsDTO);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, StudyYearFinishController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
