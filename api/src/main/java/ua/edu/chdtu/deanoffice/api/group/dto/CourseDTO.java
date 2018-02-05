package ua.edu.chdtu.deanoffice.api.group.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CourseDTO {
    @JsonView(GroupViews.Name.class)
    private int id;
    @JsonView(GroupViews.Name.class)
    private CourseNameDTO courseName;
    @JsonView(GroupViews.Course.class)
    private Integer semester;
    @JsonView(GroupViews.Name.class)
    private KnowledgeControlDTO knowledgeControl;
    @JsonView(GroupViews.Name.class)
    private Integer hours;
    private BigDecimal credits;
}
