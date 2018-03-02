package ua.edu.chdtu.deanoffice.api.general;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentDegreeViews;

@Getter
@Setter
public class NamedDTO {
    @JsonView(StudentDegreeViews.Simple.class)
    private int id;
    @JsonView(StudentDegreeViews.Simple.class)
    private String name;
}
