package ua.edu.chdtu.deanoffice.api.group.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.NamedDTO;

@Getter
@Setter
class CourseDTO {
    @JsonView(StudentGroupView.BasicCourse.class)
    private int id;
    @JsonView(StudentGroupView.BasicCourse.class)
    private NamedDTO courseName;
    @JsonView(StudentGroupView.Course.class)
    private Integer semester;
    @JsonView(StudentGroupView.BasicCourse.class)
    private NamedDTO knowledgeControl;
    @JsonView(StudentGroupView.BasicCourse.class)
    private Integer hours;
}
