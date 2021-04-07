package ua.edu.chdtu.deanoffice.api.course.selective;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.course.selective.dto.SelectiveCoursesYearParametersDTO;
import ua.edu.chdtu.deanoffice.api.course.selective.dto.SelectiveCoursesYearParametersWriteDTO;
import ua.edu.chdtu.deanoffice.entity.SelectiveCoursesYearParameters;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.service.SelectiveCoursesYearParametersService;

import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;
import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.strictMap;

@RestController
@RequestMapping("/selective-courses-selection-parameters")
public class SelectiveCoursesYearParametersController {
    private SelectiveCoursesYearParametersService selectiveCoursesYearParametersService;

    @Autowired
    public SelectiveCoursesYearParametersController(SelectiveCoursesYearParametersService selectiveCoursesYearParametersService) {
        this.selectiveCoursesYearParametersService = selectiveCoursesYearParametersService;
    }

    @GetMapping
    public ResponseEntity getSelectiveCoursesYearParameters(@RequestParam Integer year) {
        return new ResponseEntity(map(selectiveCoursesYearParametersService.getSelectiveCoursesYearParametersByYear(year), SelectiveCoursesYearParametersDTO.class), HttpStatus.OK);
    }

    @Secured({"ROLE_NAVCH_METHOD"})
    @PostMapping
    public ResponseEntity createSelectiveCoursesYearParameters(@Validated @RequestBody SelectiveCoursesYearParametersWriteDTO selectiveCoursesYearParametersWriteDTO)
        throws OperationCannotBePerformedException {

        SelectiveCoursesYearParameters selectiveCoursesYearParameters = strictMap(selectiveCoursesYearParametersWriteDTO, SelectiveCoursesYearParameters.class);
        SelectiveCoursesYearParameters selectiveCoursesYearParametersAfterSave = selectiveCoursesYearParametersService.create(selectiveCoursesYearParameters);
        SelectiveCoursesYearParametersDTO selectiveCoursesSelectionParametersAfterSaveDTO = map(selectiveCoursesYearParametersAfterSave, SelectiveCoursesYearParametersDTO.class);
        return new ResponseEntity(selectiveCoursesSelectionParametersAfterSaveDTO, HttpStatus.CREATED);
    }
}
