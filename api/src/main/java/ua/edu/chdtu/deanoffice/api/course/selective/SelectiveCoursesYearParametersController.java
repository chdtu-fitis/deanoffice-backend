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
import ua.edu.chdtu.deanoffice.entity.SelectiveCoursesYearParameters;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.service.SelectiveCoursesYearParametersService;

import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;
import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.strictMap;

@RestController
@RequestMapping("/selective-courses-year-parameters")
public class SelectiveCoursesYearParametersController {
    private SelectiveCoursesYearParametersService selectiveCoursesYearParametersService;

    @Autowired
    public SelectiveCoursesYearParametersController(SelectiveCoursesYearParametersService selectiveCoursesYearParametersService) {
        this.selectiveCoursesYearParametersService = selectiveCoursesYearParametersService;
    }

    @GetMapping
    public ResponseEntity getSelectiveCoursesYearParameters(@RequestParam Integer year,
                                                            @RequestParam(required = false) Integer degreeId) {
        if (degreeId == null) {
            SelectiveCoursesYearParameters selectiveCoursesYearParameters = selectiveCoursesYearParametersService.getSelectiveCoursesYearParametersByYear(year);
            if (selectiveCoursesYearParameters == null)
                return ResponseEntity.ok().build();

            return new ResponseEntity(map(selectiveCoursesYearParameters, FullSelectiveCoursesYearParametersDTO.class), HttpStatus.OK);
        }
        else {
            SelectiveCoursesYearParametersDTO selectiveCoursesYearParametersDTO = new SelectiveCoursesYearParametersDTO();
            SelectiveCoursesYearParameters selectiveCoursesYearParameters = selectiveCoursesYearParametersService.getSelectiveCoursesYearParametersByYear(year);
            strictMap(selectiveCoursesYearParameters, selectiveCoursesYearParametersDTO);
            if (degreeId == 1) {
                setMinStudentsCount(selectiveCoursesYearParametersDTO,
                        selectiveCoursesYearParameters.getBachelorGeneralMinStudentsCount(),
                        selectiveCoursesYearParameters.getBachelorProfessionalMinStudentsCount()
                );
            }
            else if (degreeId == 3) {
                setMinStudentsCount(selectiveCoursesYearParametersDTO,
                        selectiveCoursesYearParameters.getMasterGeneralMinStudentsCount(),
                        selectiveCoursesYearParameters.getMasterProfessionalMinStudentsCount()
                );
            }
            else if (degreeId == 4) {
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
    public ResponseEntity createSelectiveCoursesYearParameters(@Validated @RequestBody SelectiveCoursesYearParametersWriteDTO selectiveCoursesYearParametersWriteDTO)
        throws OperationCannotBePerformedException {

        SelectiveCoursesYearParameters selectiveCoursesYearParameters = strictMap(selectiveCoursesYearParametersWriteDTO, SelectiveCoursesYearParameters.class);
        SelectiveCoursesYearParameters selectiveCoursesYearParametersAfterSave = selectiveCoursesYearParametersService.create(selectiveCoursesYearParameters);
        FullSelectiveCoursesYearParametersDTO selectiveCoursesSelectionParametersAfterSaveDTO = map(selectiveCoursesYearParametersAfterSave, FullSelectiveCoursesYearParametersDTO.class);
        return new ResponseEntity(selectiveCoursesSelectionParametersAfterSaveDTO, HttpStatus.CREATED);
    }
}
