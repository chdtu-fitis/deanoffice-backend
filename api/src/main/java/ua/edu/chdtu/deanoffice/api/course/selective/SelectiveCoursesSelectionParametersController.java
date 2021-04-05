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
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.service.SelectiveCoursesSelectionParametersService;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

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
    public ResponseEntity createSelectiveCoursesSelectionParameters(@Validated @RequestBody SelectiveCoursesSelectionParametersWriteDTO selectiveCoursesSelectionParametersWriteDTO)
            throws OperationCannotBePerformedException {
        SelectiveCoursesSelectionParameters selectiveCoursesSelectionParameters = map(selectiveCoursesSelectionParametersWriteDTO, SelectiveCoursesSelectionParameters.class);

        int yearOfFirstRoundStartDate = getYearOfDate(selectiveCoursesSelectionParameters.getFirstRoundStartDate());
        int yearOfFirstRoundEndSecondRoundStartDate = getYearOfDate(selectiveCoursesSelectionParameters.getFirstRoundEndSecondRoundStartDate());
        int yearOfSecondRoundEndDate = getYearOfDate(selectiveCoursesSelectionParameters.getSecondRoundEndDate());

        if (yearOfFirstRoundStartDate == yearOfFirstRoundEndSecondRoundStartDate && yearOfFirstRoundStartDate == yearOfSecondRoundEndDate) {
            SelectiveCoursesSelectionParameters selectiveCoursesSelectionParametersAfterSave = selectiveCoursesSelectionParametersService.create(selectiveCoursesSelectionParameters);
            SelectiveCoursesSelectionParametersDTO selectiveCoursesSelectionParametersAfterSaveDTO = map(selectiveCoursesSelectionParametersAfterSave, SelectiveCoursesSelectionParametersDTO.class);
            return new ResponseEntity(selectiveCoursesSelectionParametersAfterSaveDTO, HttpStatus.CREATED);
        } else
            throw new OperationCannotBePerformedException("Роки в датах повинні бути однакові");
    }

    private int getYearOfDate(Date targetDate) {
        Date date = targetDate;
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Kiev"));
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        return year;
    }
}
