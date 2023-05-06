package ua.edu.chdtu.deanoffice.api.teacher;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScientificDegreeDTO {
    @JsonView(TeacherView.Basic.class)
    private int id;
    @JsonView(TeacherView.Basic.class)
    private String name;
    private String nameEng;
    @JsonView(TeacherView.Basic.class)
    private String abbr;
}
