package ua.edu.chdtu.deanoffice.api.student.synchronization.edebo.dto;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.dto.NamedDTO;
import ua.edu.chdtu.deanoffice.api.speciality.dto.SpecialityDTO;

@Getter
@Setter
public class SpecializationDTO {
    private Integer id;
    private String name;
    private NamedDTO faculty;
    private NamedDTO degree;
    private SpecialityDTO speciality;
}
