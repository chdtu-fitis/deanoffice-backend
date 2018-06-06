package ua.edu.chdtu.deanoffice.api.specialization.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.dto.NamedDTO;

@Getter
@Setter
public class AcquiredCompetenciesDTO {
    @JsonView(SpecializationView.AcquiredCompetencies.class)
    private Integer id;
    @JsonView(SpecializationView.AcquiredCompetenciesUkr.class)
    private String competencies;
    private String competenciesEng;
    //private NamedDTO specialization;
    private Integer year;

    private Integer specializationId;
}
