package ua.edu.chdtu.deanoffice.api.teacher;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.dto.NamedDTO;
import ua.edu.chdtu.deanoffice.entity.AcademicTitle;
import ua.edu.chdtu.deanoffice.entity.superclasses.Sex;

@Getter
@Setter
public class TeacherDTO extends TeacherBasicDTO {
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
