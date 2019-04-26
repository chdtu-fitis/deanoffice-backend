package ua.edu.chdtu.deanoffice.api.order.dto;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.dto.NamedDTO;

@Getter
@Setter
public class StudentDegreeDTO {
    private Integer id;
    private boolean active;
    private StudentDTO student;
    private NamedDTO studentGroup;
    private SpecializationDTO specialization;
}
