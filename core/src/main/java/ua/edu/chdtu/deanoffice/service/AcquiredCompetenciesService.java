package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.AcquiredCompetencies;
import ua.edu.chdtu.deanoffice.repository.AcquiredCompetenciesRepository;

@Service
public class AcquiredCompetenciesService {
    private AcquiredCompetenciesRepository acquiredCompetenciesRepository;

    public AcquiredCompetenciesService(AcquiredCompetenciesRepository acquiredCompetenciesRepository) {
        this.acquiredCompetenciesRepository = acquiredCompetenciesRepository;
    }

    public AcquiredCompetencies findBySpecializationIdAndYear(Integer specializationId, Integer year) {
        return acquiredCompetenciesRepository.findBySpecializationIdAndYear(specializationId, year);
    }
}
