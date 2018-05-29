package ua.edu.chdtu.deanoffice.api.specialization;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.specialization.dto.AcquiredCompetenciesDTO;
import ua.edu.chdtu.deanoffice.api.specialization.dto.SpecializationView;
import ua.edu.chdtu.deanoffice.entity.AcquiredCompetencies;
import ua.edu.chdtu.deanoffice.service.AcquiredCompetenciesService;

import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;

@RestController
@RequestMapping("/specialization")
public class AcquiredCompetenciesController {
    private final AcquiredCompetenciesService acquiredCompetenciesService;

    @Autowired
    public AcquiredCompetenciesController(AcquiredCompetenciesService acquiredCompetenciesService) {
        this.acquiredCompetenciesService = acquiredCompetenciesService;
    }

    @GetMapping("/{specialization_id}/competencies/ukr")
    @JsonView(SpecializationView.AcquiredCompetenciesUkr.class)
    public ResponseEntity getCompetenciesForSpecialization(@PathVariable("specialization_id") int specializationId) {
        AcquiredCompetencies acquiredCompetencies = acquiredCompetenciesService.getAcquiredCompetencies(specializationId);
        return ResponseEntity.ok(map(acquiredCompetencies, AcquiredCompetenciesDTO.class));
    }
}
