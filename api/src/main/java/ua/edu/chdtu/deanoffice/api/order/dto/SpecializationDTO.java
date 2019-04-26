package ua.edu.chdtu.deanoffice.api.order.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpecializationDTO {
    private int id;
    private String name;
    private String code;
    private SpecialityDTO speciality;
}
