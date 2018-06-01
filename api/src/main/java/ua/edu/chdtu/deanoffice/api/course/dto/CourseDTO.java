package ua.edu.chdtu.deanoffice.api.course.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.dto.NamedDTO;

import java.math.BigDecimal;

@Getter
@Setter
public class CourseDTO {
    @JsonView(CourseForGroupView.Basic.class)
    private int id;
    @JsonView(CourseForGroupView.Basic.class)
    private NamedDTO courseName;
    @JsonView(CourseForGroupView.Course.class)
    private Integer semester;
    @JsonView(CourseForGroupView.Basic.class)
    private NamedDTO knowledgeControl;
    @JsonView(CourseForGroupView.Basic.class)
    private Integer hours;
    @JsonView(CourseForGroupView.Basic.class)
    private BigDecimal credits;
    @JsonView(CourseForGroupView.Basic.class)
    private Integer hoursPerCredit;
}
