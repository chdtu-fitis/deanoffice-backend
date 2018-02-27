package ua.edu.chdtu.deanoffice.api.studentDegree.dto;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.TuitionForm;
import ua.edu.chdtu.deanoffice.entity.TuitionTerm;

import java.math.BigDecimal;

@Setter
@Getter
public class StudentGroupDTO {
    private Integer id;
    private String name;
    private SpecializationDTO specialization;
}
