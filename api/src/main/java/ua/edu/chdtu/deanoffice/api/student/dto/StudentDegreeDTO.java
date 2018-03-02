package ua.edu.chdtu.deanoffice.api.student.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.NamedDTO;

@Getter
@Setter
public class StudentDegreeDTO extends BaseStudentDegree {
    @JsonView(StudentDegreeViews.Simple.class)
    private NamedDTO studentGroup;
}
