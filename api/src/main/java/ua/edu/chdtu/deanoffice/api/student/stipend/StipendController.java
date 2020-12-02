package ua.edu.chdtu.deanoffice.api.student.stipend;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.api.general.mapper.Mapper;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.security.FacultyAuthorizationService;
import ua.edu.chdtu.deanoffice.service.stipend.SingleSpecialityStipendDataBean;
import ua.edu.chdtu.deanoffice.service.stipend.StipendService;
import ua.edu.chdtu.deanoffice.service.stipend.StudentInfoForStipend;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ua.edu.chdtu.deanoffice.api.general.Util.getNewResourceLocation;

@RestController
@RequestMapping("/student-degree/stipend")
public class StipendController extends DocumentResponseController {
    private StipendService stipendService;
    private StudentDegreeService studentDegreeService;
    private FacultyAuthorizationService facultyAuthorizationService;

    public StipendController(StipendService stipendService, StudentDegreeService studentDegreeService, FacultyAuthorizationService facultyAuthorizationService) {
        this.stipendService = stipendService;
        this.studentDegreeService = studentDegreeService;
        this.facultyAuthorizationService = facultyAuthorizationService;
    }

    @Secured("ROLE_DEANOFFICER")
    @GetMapping
    public ResponseEntity<List<SpecialityStudentsInfoForStipendDTO>> getStipendInfo() {
        try {
            List<StudentInfoForStipend> stipendData = stipendService.getStipendData();
            Map<SingleSpecialityStipendDataBean, List<StudentInfoForStipend>> studInfoGroupedBySpeciality = stipendService.getStudentInfoGroupedBySpeciality(stipendData);
            List<SpecialityStudentsInfoForStipendDTO> studInfoGroupedBySpecialityDTOs = mapStipendInfoBeansToDtos(studInfoGroupedBySpeciality);

            return ResponseEntity.ok(studInfoGroupedBySpecialityDTOs);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @Secured("ROLE_DEANOFFICER")
    @GetMapping("/docx")
    public ResponseEntity<File> generateStipendDocument() {
        try {
            File result = stipendService.formDocument();
            return buildDocumentResponseEntity(result, result.getName(), MEDIA_TYPE_DOCX);
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @Secured("ROLE_DEANOFFICER")
    @PostMapping("/extra-points-update")
    public ResponseEntity updateExtraPoints(@RequestBody List <ExtraPointsDTO> extraPointsDTO,
                                            @CurrentUser ApplicationUser user){
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

    private List<SpecialityStudentsInfoForStipendDTO> mapStipendInfoBeansToDtos(Map<SingleSpecialityStipendDataBean, List<StudentInfoForStipend>> studInfoGroupedBySpeciality) {
        List<SpecialityStudentsInfoForStipendDTO> specialityStudentsInfoForStipendDtos = new ArrayList<>();
        studInfoGroupedBySpeciality.forEach((singleSpecialityStipendDataBean, studentInfoForStipend) -> {
            SpecialityStudentsInfoForStipendDTO specialityStudentsInfoForStipendDTO = Mapper.strictMap(singleSpecialityStipendDataBean, SpecialityStudentsInfoForStipendDTO.class);
            List<StudentInfoForStipendDTO> studentsInfoForStipendDTO = Mapper.strictMap(studentInfoForStipend, StudentInfoForStipendDTO.class);
            specialityStudentsInfoForStipendDTO.setStudentsInfoForStipend(studentsInfoForStipendDTO);
            specialityStudentsInfoForStipendDtos.add(specialityStudentsInfoForStipendDTO);
        });
        return specialityStudentsInfoForStipendDtos;
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, StipendController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }

}
