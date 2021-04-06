package ua.edu.chdtu.deanoffice.api.course.selective;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.course.selective.dto.SelectiveCoursesYearParametersDTO;
import ua.edu.chdtu.deanoffice.api.course.selective.dto.SelectiveCoursesYearParametersWriteDTO;
import ua.edu.chdtu.deanoffice.entity.SelectiveCoursesYearParameters;
import ua.edu.chdtu.deanoffice.service.SelectiveCoursesYearParametersService;

import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;

@RestController
@RequestMapping("/selective-courses-selection-parameters")
public class SelectiveCoursesSelectionParametersController {
    private SelectiveCoursesYearParametersService selectiveCoursesYearParametersService;

    @Autowired
    public SelectiveCoursesSelectionParametersController(SelectiveCoursesYearParametersService selectiveCoursesYearParametersService) {
        this.selectiveCoursesYearParametersService = selectiveCoursesYearParametersService;
    }

    @Secured({"ROLE_NAVCH_METHOD"})
    @PostMapping
    public ResponseEntity createSelectiveCoursesSelectionParameters(@RequestBody SelectiveCoursesYearParametersWriteDTO selectiveCoursesYearParametersWriteDTO) {
        SelectiveCoursesYearParameters selectiveCoursesYearParameters = map(selectiveCoursesYearParametersWriteDTO, SelectiveCoursesYearParameters.class);
        SelectiveCoursesYearParameters selectiveCoursesYearParametersAfterSave = selectiveCoursesYearParametersService.create(selectiveCoursesYearParameters);
        SelectiveCoursesYearParametersDTO selectiveCoursesSelectionParametersAfterSaveDTO = map(selectiveCoursesYearParametersAfterSave, SelectiveCoursesYearParametersDTO.class);
        return new ResponseEntity(selectiveCoursesSelectionParametersAfterSaveDTO, HttpStatus.CREATED);
    }
}
