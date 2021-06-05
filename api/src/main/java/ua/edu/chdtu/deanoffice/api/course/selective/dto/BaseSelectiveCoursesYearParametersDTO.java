package ua.edu.chdtu.deanoffice.api.course.selective.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class BaseSelectiveCoursesYearParametersDTO {
    private int id;
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private Date firstRoundStartDate;
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private Date firstRoundEndDate;
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private Date secondRoundStartDate;
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private Date secondRoundEndDate;
    private int studyYear;
}
