package ua.edu.chdtu.deanoffice.api.course.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.NamedDTO;

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
}
