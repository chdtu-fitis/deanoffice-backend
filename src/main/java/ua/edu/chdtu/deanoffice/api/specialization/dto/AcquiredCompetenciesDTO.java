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
    @JsonView(SpecializationView.AcquiredCompetenciesEng.class)
    private String competenciesEng;
    private Integer year;

    private Integer specializationId;
}
