package ua.edu.chdtu.deanoffice.api.specialization.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AcquiredCompetenciesDTO {
//    @JsonView(SpecializationView.AcquiredCompetencies.class)
    private Integer id;
//    @JsonView(SpecializationView.AcquiredCompetencies.class)
    private List<CompetenceDTO> competencies;
    private Integer year;
    private Integer specializationId;
}
