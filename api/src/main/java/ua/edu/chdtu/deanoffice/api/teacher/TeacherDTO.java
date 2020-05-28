package ua.edu.chdtu.deanoffice.api.teacher;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.dto.NamedDTO;

@Getter
@Setter
public class TeacherDTO extends TeacherBasicDTO {
    private int id;
    private NamedDTO department;
    private NamedDTO position;
    private ScientificDegreeDTO scientificDegree;
}
