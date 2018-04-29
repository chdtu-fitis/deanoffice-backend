package ua.edu.chdtu.deanoffice.api.grade;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.course.dto.CourseForGroupView;

@Getter
@Setter
public class GradeDTO {
    private Integer id;
    private Integer points;
    @JsonView(CourseForGroupView.Course.class)
    private CourseIdDTO course;
    private StudentIdDTO studentDegree;
}
