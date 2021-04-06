package ua.edu.chdtu.deanoffice.api.course.selective.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import java.time.LocalDate;

@Data
public class SelectiveCoursesYearParametersWriteDTO {
    private LocalDate firstRoundStartDate;
    private LocalDate firstRoundEndDate;
    private LocalDate secondRoundEndDate;
    @Min(1)
    private int minStudentsCount;
}
