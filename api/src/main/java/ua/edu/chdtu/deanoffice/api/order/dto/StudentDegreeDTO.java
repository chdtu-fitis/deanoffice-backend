package ua.edu.chdtu.deanoffice.api.order.dto;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.dto.NamedDTO;

@Getter
@Setter
public class StudentDegreeDTO {
    private StudentDTO student;
    private NamedDTO studentGroup;
    private boolean active;
    private SpecializationDTO specialization;
}
