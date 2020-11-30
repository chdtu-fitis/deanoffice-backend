package ua.edu.chdtu.deanoffice.api.teacher;

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
    private int id;
    private String surname;
    private String name;
    private String patronimic;
    private Sex sex;
    private boolean active;
    private AcademicTitle academicTitle;
    private NamedDTO department;
    private NamedDTO position;
    private ScientificDegreeDTO scientificDegree;
}
