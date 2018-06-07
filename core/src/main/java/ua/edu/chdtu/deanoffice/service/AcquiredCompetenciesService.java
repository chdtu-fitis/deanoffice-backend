package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.AcquiredCompetencies;
import ua.edu.chdtu.deanoffice.repository.AcquiredCompetenciesRepository;

@Service
public class AcquiredCompetenciesService {
    private final AcquiredCompetenciesRepository acquiredCompetenciesRepository;
    private final CurrentYearService currentYearService;

    @Autowired
    public AcquiredCompetenciesService(
            AcquiredCompetenciesRepository acquiredCompetenciesRepository,
            CurrentYearService currentYearService
    ) {
        this.acquiredCompetenciesRepository = acquiredCompetenciesRepository;
        this.currentYearService = currentYearService;
    }

    public AcquiredCompetencies getLastAcquiredCompetencies(int specializationId) {
        return acquiredCompetenciesRepository.findLastCompetenciesForSpecialization(specializationId);
    }

    public AcquiredCompetencies findBySpecializationIdAndYear(Integer specializationId, Integer year) {
        return acquiredCompetenciesRepository.findBySpecializationIdAndYear(specializationId, year);
    }

    public void updateCompetenciesUkr(Integer acquiredCompetenciesId, String competencies) {
        AcquiredCompetencies acquiredCompetencies = this.getById(acquiredCompetenciesId);
        acquiredCompetencies.setCompetencies(competencies);
        this.acquiredCompetenciesRepository.save(acquiredCompetencies);
    }

    public void updateCompetenciesEng(Integer acquiredCompetenciesId, String competenciesEng) {
        AcquiredCompetencies acquiredCompetencies = this.getById(acquiredCompetenciesId);
        acquiredCompetencies.setCompetenciesEng(competenciesEng);
        this.acquiredCompetenciesRepository.save(acquiredCompetencies);
    }

    private AcquiredCompetencies getById(Integer acquiredCompetenciesId) {
        return this.acquiredCompetenciesRepository.findOne(acquiredCompetenciesId);
    }

    public void create(AcquiredCompetencies acquiredCompetencies) {
        acquiredCompetencies.setYear(currentYearService.getYear());
        this.acquiredCompetenciesRepository.save(acquiredCompetencies);
    }

    public boolean isNotExist(int specializationId, boolean forCurrentYear) {
        AcquiredCompetencies acquiredCompetencies;
        if (forCurrentYear) {
            int currentYear = currentYearService.getYear();
            acquiredCompetencies = acquiredCompetenciesRepository.findBySpecializationIdAndYear(specializationId, currentYear);
        } else {
            acquiredCompetencies = getLastAcquiredCompetencies(specializationId);
        }
        return acquiredCompetencies == null;
    }
}
