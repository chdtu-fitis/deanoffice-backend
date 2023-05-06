package ua.edu.chdtu.deanoffice.api.teacher;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.dto.NamedDTO;
import ua.edu.chdtu.deanoffice.entity.AcademicTitle;
import ua.edu.chdtu.deanoffice.entity.superclasses.Sex;
import javax.validation.constraints.Min;

@Getter
@Setter
public class TeacherDTO {
    @Min(1)
    @JsonView(TeacherView.Basic.class)
    private int id;
    @JsonView(TeacherView.Basic.class)
    private String surname;
    @JsonView(TeacherView.Basic.class)
    private String name;
    @JsonView(TeacherView.Basic.class)
    private String patronimic;
    private Sex sex;
    private boolean active;
    @JsonView(TeacherView.Basic.class)
    private AcademicTitle academicTitle;
    private NamedDTO department;
    private NamedDTO position;
    @JsonView(TeacherView.Basic.class)
    private ScientificDegreeDTO scientificDegree;
}
