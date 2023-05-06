package ua.edu.chdtu.deanoffice.service.course;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentCourseBean {
    private String name;
    private int hours;
    private BigDecimal credits;
    private int semester;
    private String teacher;
    private String knowledgeControl;
    private boolean selective;
}
