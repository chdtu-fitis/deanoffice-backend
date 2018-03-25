package ua.edu.chdtu.deanoffice.api.group.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.KnowledgeControlDTO;

import java.math.BigDecimal;

@Getter
@Setter
public class CourseDTO {
    @JsonView({GroupViews.Name.class, StudentGroupView.Course.class})
    private int id;
    @JsonView({GroupViews.Name.class, StudentGroupView.Course.class})
    private CourseNameDTO courseName;
    @JsonView({GroupViews.Course.class, StudentGroupView.Course.class})
    private Integer semester;
    @JsonView({GroupViews.Name.class, StudentGroupView.Course.class})
    private KnowledgeControlDTO knowledgeControl;
    @JsonView({GroupViews.Name.class, StudentGroupView.Course.class})
    private Integer hours;
    private BigDecimal credits;
}
