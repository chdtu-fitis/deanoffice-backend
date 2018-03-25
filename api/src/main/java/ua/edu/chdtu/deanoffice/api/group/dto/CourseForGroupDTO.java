package ua.edu.chdtu.deanoffice.api.group.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.PersonFullNameDTO;

import java.util.Date;

@Getter
@Setter
public class CourseForGroupDTO {
    @JsonView(StudentGroupView.BasicCourse.class)
    private int id;
    @JsonView(StudentGroupView.BasicCourse.class)
    private CourseDTO course;
    @JsonView(StudentGroupView.Course.class)
    private PersonFullNameDTO teacher;
    @JsonView(StudentGroupView.Course.class)
    private Date examDate;
}
