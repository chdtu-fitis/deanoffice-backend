package ua.edu.chdtu.deanoffice.api.teacher;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.dto.DepartmentDTO;
import ua.edu.chdtu.deanoffice.api.general.dto.NamedDTO;
import ua.edu.chdtu.deanoffice.entity.superclasses.Sex;

@Getter
@Setter
public class NewTeacherDTO {
    private int id;
    private String name;
    private String patronimic;
    private Sex sex;
    private String surname;
    private boolean active;
    private DepartmentDTO department;
    private NamedDTO position;
    private int departmentId;
    private int positionId;
    private String scientificDegreeId;
}
