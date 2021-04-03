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
import ua.edu.chdtu.deanoffice.api.course.selective.dto.SelectiveCoursesSelectionParametersDTO;
import ua.edu.chdtu.deanoffice.api.course.selective.dto.SelectiveCoursesSelectionParametersWriteDTO;
import ua.edu.chdtu.deanoffice.entity.SelectiveCoursesSelectionParameters;
import ua.edu.chdtu.deanoffice.service.SelectiveCoursesSelectionParametersService;

import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;

@RestController
@RequestMapping("/selective-courses-selection-parameters")
public class SelectiveCoursesSelectionParametersController {
    private SelectiveCoursesSelectionParametersService selectiveCoursesSelectionParametersService;

    @Autowired
    public SelectiveCoursesSelectionParametersController(SelectiveCoursesSelectionParametersService selectiveCoursesSelectionParametersService) {
        this.selectiveCoursesSelectionParametersService = selectiveCoursesSelectionParametersService;
    }

    @Secured({"ROLE_NAVCH_METHOD"})
    @PostMapping
    public ResponseEntity createSelectiveCoursesSelectionParameters(@Validated @RequestBody SelectiveCoursesSelectionParametersWriteDTO selectiveCoursesSelectionParametersWriteDTO) {
        SelectiveCoursesSelectionParameters selectiveCoursesSelectionParameters = map(selectiveCoursesSelectionParametersWriteDTO, SelectiveCoursesSelectionParameters.class);
        SelectiveCoursesSelectionParameters selectiveCoursesSelectionParametersAfterSave = selectiveCoursesSelectionParametersService.create(selectiveCoursesSelectionParameters);
        SelectiveCoursesSelectionParametersDTO selectiveCoursesSelectionParametersAfterSaveDTO = map(selectiveCoursesSelectionParametersAfterSave, SelectiveCoursesSelectionParametersDTO.class);
        return new ResponseEntity(selectiveCoursesSelectionParametersAfterSaveDTO, HttpStatus.CREATED);
    }
}
