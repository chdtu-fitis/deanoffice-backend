package ua.edu.chdtu.deanoffice.api.course.selective.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SelectiveCoursesYearParametersDTO {
    private int id;
    @JsonFormat(pattern="dd-MM-yyyy")
    private LocalDate firstRoundStartDate;
    @JsonFormat(pattern="dd-MM-yyyy")
    private LocalDate firstRoundEndDate;
    @JsonFormat(pattern="dd-MM-yyyy")
    private LocalDate secondRoundEndDate;
    private int minStudentsCount;
}
