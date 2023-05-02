package ua.edu.chdtu.deanoffice.api.student.synchronization.edebo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class StudentPreviousUniversityDTO {
    private int id;
    private String universityName;
    @JsonFormat(pattern="yyyy-MM-dd", locale = "uk_UA", timezone = "EET")
    private Date studyStartDate;
    @JsonFormat(pattern="yyyy-MM-dd", locale = "uk_UA", timezone = "EET")
    private Date studyEndDate;
}
