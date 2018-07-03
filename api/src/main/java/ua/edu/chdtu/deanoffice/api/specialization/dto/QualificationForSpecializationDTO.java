package ua.edu.chdtu.deanoffice.api.specialization.dto;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.ProfessionalQualification;

@Getter
@Setter
public class QualificationForSpecializationDTO {
    private int id;
    private ProfessionalQualification professionalQualification;
    private Integer year;
}
