package ua.edu.chdtu.deanoffice.api.course.selective.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class BaseSelectiveCoursesYearParametersDTO {
    private int id;
    @JsonFormat(pattern="dd-MM-yyyy")
    private Date firstRoundStartDate;
    @JsonFormat(pattern="dd-MM-yyyy")
    private Date firstRoundEndDate;
    @JsonFormat(pattern="dd-MM-yyyy")
    private Date secondRoundStartDate;
    @JsonFormat(pattern="dd-MM-yyyy")
    private Date secondRoundEndDate;
    private int studyYear;
}