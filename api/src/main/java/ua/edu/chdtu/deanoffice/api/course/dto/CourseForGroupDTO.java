package ua.edu.chdtu.deanoffice.api.course.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.PersonFullNameDTO;

import java.util.Date;

@Getter
@Setter
public class CourseForGroupDTO {
    @JsonView(CourseForGroupView.Basic.class)
    private int id;
    @JsonView(CourseForGroupView.Basic.class)
    private CourseDTO course;
    @JsonView(CourseForGroupView.Course.class)
    private PersonFullNameDTO teacher;
    @JsonView(CourseForGroupView.Course.class)
    private Date examDate;
}
