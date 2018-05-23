package ua.edu.chdtu.deanoffice.api.grade.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GradeDTO {
    private Integer id;
    private Integer points;
    private Boolean onTime;
    private Integer courseId;
    private Integer studentDegreeId;
}
