package ua.edu.chdtu.deanoffice.api.course.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class StudentCourseDTO {
    private String name;
    private int hours;
    private BigDecimal credits;
    private int semester;
    private String teacher;
    private String knowledgeControl;
    private boolean selective;
}
