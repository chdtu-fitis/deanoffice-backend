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
    private GroupDTO studentGroup;
    @JsonView(GroupViews.Course.class)
    private TeacherDTO teacher;
    @JsonView(GroupViews.Course.class)
    private Date examDate;
}
