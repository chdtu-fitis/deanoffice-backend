package ua.edu.chdtu.deanoffice.api.group.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.entity.Teacher;

import java.util.Date;

@Getter
@Setter
public class CourseForGroupDTO {
    @JsonView(GroupViews.Name.class)
    private int id;
    @JsonView(GroupViews.Name.class)
    private CourseDTO course;
    @JsonView(GroupViews.Name.class)
    private GroupDTO studentGroup;
    @JsonView(GroupViews.Name.class)
    private TeacherDTO teacher;
    @JsonView(GroupViews.Name.class)
    private Date examDate;
}
