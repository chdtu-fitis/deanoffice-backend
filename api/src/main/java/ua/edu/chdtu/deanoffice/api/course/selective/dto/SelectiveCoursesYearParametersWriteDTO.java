package ua.edu.chdtu.deanoffice.api.course.selective.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class SelectiveCoursesYearParametersWriteDTO {
    @NotNull
    private Date firstRoundStartDate;
    @NotNull
    private Date firstRoundEndDate;
    @NotNull
    private Date secondRoundEndDate;
    @Min(1)
    private int minStudentsCount;
}
