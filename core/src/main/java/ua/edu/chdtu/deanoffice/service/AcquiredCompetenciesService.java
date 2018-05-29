package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.AcquiredCompetencies;
import ua.edu.chdtu.deanoffice.repository.AcquiredCompetenciesRepository;

import java.util.List;

@Service
public class AcquiredCompetenciesService {
    private final AcquiredCompetenciesRepository acquiredCompetenciesRepository;

    @Autowired
    public AcquiredCompetenciesService(AcquiredCompetenciesRepository acquiredCompetenciesRepository) {
        this.acquiredCompetenciesRepository = acquiredCompetenciesRepository;
    }

    public AcquiredCompetencies getAcquiredCompetencies(int specializationId) {
        List<AcquiredCompetencies> acquiredCompetencies =
                acquiredCompetenciesRepository.findLastCompetenciesForSpecialization(specializationId);
        if (acquiredCompetencies.isEmpty()) {
            return null;
        }
        return acquiredCompetencies.get(0);
    }
}
