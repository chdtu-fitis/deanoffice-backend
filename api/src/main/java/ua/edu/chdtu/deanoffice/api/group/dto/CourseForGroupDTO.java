package ua.edu.chdtu.deanoffice.api.group.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CourseForGroupDTO {
    @JsonView({GroupViews.Name.class, StudentGroupView.Course.class})
    private int id;
    @JsonView({GroupViews.Name.class, StudentGroupView.Course.class})
    private CourseDTO course;
    private GroupDTO studentGroup;
    @JsonView({GroupViews.Course.class, StudentGroupView.Course.class})
    private TeacherDTO teacher;
    @JsonView({GroupViews.Course.class, StudentGroupView.Course.class})
    private Date examDate;
}
