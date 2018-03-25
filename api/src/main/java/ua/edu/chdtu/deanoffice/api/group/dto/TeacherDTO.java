package ua.edu.chdtu.deanoffice.api.group.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.Department;
import ua.edu.chdtu.deanoffice.entity.Position;
import ua.edu.chdtu.deanoffice.entity.superclasses.Sex;

@Getter
@Setter
public class TeacherDTO {
    @JsonView({GroupViews.Name.class, StudentGroupView.Course.class})
    private int id;
    @JsonView({GroupViews.Name.class, StudentGroupView.Course.class})
    private String name;
    @JsonView({GroupViews.Name.class, StudentGroupView.Course.class})
    private String surname;
    @JsonView({GroupViews.Name.class, StudentGroupView.Course.class})
    private String patronimic;
    private boolean active = true;
    private Sex sex;
    private Department department;
    private Position position;
    private String scientificDegree;
}
