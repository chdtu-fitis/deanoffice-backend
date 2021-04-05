package ua.edu.chdtu.deanoffice.api.course.selective.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class SelectiveCoursesSelectionParametersWriteDTO {
    @NotNull
    private Date firstRoundStartDate;
    @NotNull
    private Date firstRoundEndSecondRoundStartDate;
    @NotNull
    private Date secondRoundEndDate;
    @NotNull
    @Min(1)
    private int minimumCountOfStudents;
}
