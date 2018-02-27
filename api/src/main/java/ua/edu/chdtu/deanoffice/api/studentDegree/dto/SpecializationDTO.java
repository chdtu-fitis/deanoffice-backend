package ua.edu.chdtu.deanoffice.api.studentDegree.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SpecializationDTO {
    private SpecialityDTO speciality;
    private DepartmentDTO department;
    private String name;
    private Integer id;
}
