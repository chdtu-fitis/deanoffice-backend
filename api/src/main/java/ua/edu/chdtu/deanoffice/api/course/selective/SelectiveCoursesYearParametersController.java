package ua.edu.chdtu.deanoffice.api.course.selective;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.course.selective.dto.FullSelectiveCoursesYearParametersDTO;
import ua.edu.chdtu.deanoffice.api.course.selective.dto.SelectiveCoursesYearParametersDTO;
import ua.edu.chdtu.deanoffice.api.course.selective.dto.SelectiveCoursesYearParametersWriteDTO;
import ua.edu.chdtu.deanoffice.entity.DegreeEnum;
import ua.edu.chdtu.deanoffice.entity.PeriodCaseEnum;
import ua.edu.chdtu.deanoffice.entity.SelectiveCoursesYearParameters;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.service.SelectiveCoursesYearParametersService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.course.selective.SelectiveCourseService;

import java.util.List;

import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;
import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.strictMap;

@RestController
@RequestMapping("/selective-courses-year-parameters")
public class SelectiveCoursesYearParametersController {
    private SelectiveCoursesYearParametersService selectiveCoursesYearParametersService;
    private SelectiveCourseService selectiveCourseService;
    private StudentDegreeService studentDegreeService;

    @Autowired
    public SelectiveCoursesYearParametersController(SelectiveCoursesYearParametersService selectiveCoursesYearParametersService,
                                                    SelectiveCourseService selectiveCourseService,
                                                    StudentDegreeService studentDegreeService) {
        this.selectiveCoursesYearParametersService = selectiveCoursesYearParametersService;
        this.selectiveCourseService = selectiveCourseService;
        this.studentDegreeService = studentDegreeService;
    }

    @GetMapping
    public ResponseEntity getSelectiveCoursesYearParameters(@RequestParam Integer year,
                                                            @RequestParam(required = false) Integer studentDegreeId) {
        if (studentDegreeId == null) {
            List<SelectiveCoursesYearParameters> selectiveCoursesYearParameters = selectiveCoursesYearParametersService.getSelectiveCoursesYearParametersByYear(year);
            if (selectiveCoursesYearParameters == null)
                return ResponseEntity.ok().build();

            return new ResponseEntity(map(selectiveCoursesYearParameters, FullSelectiveCoursesYearParametersDTO.class), HttpStatus.OK);
        }
        else {
            StudentDegree studentDegree = studentDegreeService.getById(studentDegreeId);
            int degreeId = studentDegree.getSpecialization().getDegree().getId();

            SelectiveCoursesYearParametersDTO selectiveCoursesYearParametersDTO = new SelectiveCoursesYearParametersDTO();
            List<SelectiveCoursesYearParameters> selectiveCoursesYearParametersFromDB = selectiveCoursesYearParametersService.getSelectiveCoursesYearParametersByYear(year);

            if (selectiveCoursesYearParametersFromDB == null)
                return ResponseEntity.ok().build();

            PeriodCaseEnum periodCase = selectiveCourseService.getPeriodCaseByStudentDegree(studentDegree);
            if (periodCase == null)
                return new ResponseEntity("Для даного студента відсутній період вибору вибіркових дисциплін", HttpStatus.UNPROCESSABLE_ENTITY);

            SelectiveCoursesYearParameters selectiveCoursesYearParameters = selectiveCoursesYearParametersFromDB.stream()
                    .filter(elem -> elem.getPeriodCase() == periodCase)
                    .findFirst().orElse(null);

            if (selectiveCoursesYearParameters == null)
                return new ResponseEntity("Для даного студента відсутні параметри вибору вибіркових дисциплін", HttpStatus.UNPROCESSABLE_ENTITY);

            strictMap(selectiveCoursesYearParameters, selectiveCoursesYearParametersDTO);
            if (degreeId == DegreeEnum.BACHELOR.getId()) {
                setMinStudentsCount(selectiveCoursesYearParametersDTO,
                        selectiveCoursesYearParameters.getBachelorGeneralMinStudentsCount(),
                        selectiveCoursesYearParameters.getBachelorProfessionalMinStudentsCount()
                );
            }
            else if (degreeId == DegreeEnum.MASTER.getId()) {
                setMinStudentsCount(selectiveCoursesYearParametersDTO,
                        selectiveCoursesYearParameters.getMasterGeneralMinStudentsCount(),
                        selectiveCoursesYearParameters.getMasterProfessionalMinStudentsCount()
                );
            }
            else if (degreeId == DegreeEnum.PHD.getId()) {
                setMinStudentsCount(selectiveCoursesYearParametersDTO,
                        selectiveCoursesYearParameters.getPhdGeneralMinStudentsCount(),
                        selectiveCoursesYearParameters.getPhdProfessionalMinStudentsCount()
                );
            }
            else
                return new ResponseEntity("Не існує ступеня з таким id", HttpStatus.UNPROCESSABLE_ENTITY);
            return new ResponseEntity(selectiveCoursesYearParametersDTO, HttpStatus.OK);
        }
    }

    private void setMinStudentsCount(SelectiveCoursesYearParametersDTO selectiveCoursesYearParametersDTO, int generalMinStudentsCount, int professionalMinStudentsCount) {
        selectiveCoursesYearParametersDTO.setGeneralMinStudentsCount(generalMinStudentsCount);
        selectiveCoursesYearParametersDTO.setProfessionalMinStudentsCount(professionalMinStudentsCount);
    }

    @Secured({"ROLE_NAVCH_METHOD"})
    @PostMapping
    public ResponseEntity createSelectiveCoursesYearParameters(@Validated @RequestBody List<SelectiveCoursesYearParametersWriteDTO> selectiveCoursesYearParametersWriteDTO)
        throws OperationCannotBePerformedException {

        List<SelectiveCoursesYearParameters> selectiveCoursesYearParameters = strictMap(selectiveCoursesYearParametersWriteDTO, SelectiveCoursesYearParameters.class);
        selectiveCoursesYearParameters.get(0).setPeriodCase(PeriodCaseEnum.EARLY);
        selectiveCoursesYearParameters.get(1).setPeriodCase(PeriodCaseEnum.LATE);
        List<SelectiveCoursesYearParameters> selectiveCoursesYearParametersAfterSave = selectiveCoursesYearParametersService.create(selectiveCoursesYearParameters);
        List<FullSelectiveCoursesYearParametersDTO> selectiveCoursesSelectionParametersAfterSaveDTO = map(selectiveCoursesYearParametersAfterSave, FullSelectiveCoursesYearParametersDTO.class);
        return new ResponseEntity(selectiveCoursesSelectionParametersAfterSaveDTO, HttpStatus.CREATED);
    }
}
