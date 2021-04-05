package ua.edu.chdtu.deanoffice.api.course.selective.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class SelectiveCoursesSelectionParametersDTO {
    @JsonFormat(pattern="dd-MM-yyyy")
    private Date firstRoundStartDate;
    @JsonFormat(pattern="dd-MM-yyyy")
    private Date firstRoundEndSecondRoundStartDate;
    @JsonFormat(pattern="dd-MM-yyyy")
    private Date secondRoundEndDate;
    private int minimumCountOfStudents;
}
