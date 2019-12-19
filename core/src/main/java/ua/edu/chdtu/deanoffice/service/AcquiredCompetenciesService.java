package ua.edu.chdtu.deanoffice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.AcquiredCompetencies;
import ua.edu.chdtu.deanoffice.repository.AcquiredCompetenciesRepository;
import ua.edu.chdtu.deanoffice.service.specialization.CompetenceBean;
import ua.edu.chdtu.deanoffice.service.specialization.CompetenciesBean;

import java.io.IOException;
import java.util.List;

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

    public CompetenciesBean getLastAcquiredCompetencies(int specializationId) throws IOException {
        AcquiredCompetencies lastCompForSpec = acquiredCompetenciesRepository.findLastCompetenciesForSpecialization(specializationId);
        if (lastCompForSpec == null)
            return null;
        ObjectMapper mapper = new ObjectMapper();
        String competenciesJsonStr = lastCompForSpec.getCompetencies();
        List<CompetenceBean> competenciesBeans = mapper.readValue(competenciesJsonStr, new TypeReference<List<CompetenceBean>>() {});
        CompetenciesBean competenciesBean = new CompetenciesBean(lastCompForSpec.getId(), competenciesBeans, lastCompForSpec.getSpecialization().getId(), lastCompForSpec.getYear());
        return competenciesBean;
    }

    public void updateCompetencies(Integer acquiredCompetenciesId, String competencies) {
        AcquiredCompetencies acquiredCompetencies = this.getById(acquiredCompetenciesId);
        acquiredCompetencies.setCompetencies(competencies);
        this.acquiredCompetenciesRepository.save(acquiredCompetencies);
    }

    private AcquiredCompetencies getById(Integer acquiredCompetenciesId) {
        return this.acquiredCompetenciesRepository.findOne(acquiredCompetenciesId);
    }

    public void create(AcquiredCompetencies acquiredCompetencies) {
        acquiredCompetencies.setYear(currentYearService.getYear());
        this.acquiredCompetenciesRepository.save(acquiredCompetencies);
    }

    public boolean isNotExist(int specializationId, boolean forCurrentYear) throws IOException {
        AcquiredCompetencies acquiredCompetencies = null;
        if (forCurrentYear) {
            int currentYear = currentYearService.getYear();
            acquiredCompetencies = acquiredCompetenciesRepository.findBySpecializationIdAndYear(specializationId, currentYear);
        } else {
            acquiredCompetencies = acquiredCompetenciesRepository.findLastCompetenciesForSpecialization(specializationId);
        }
        return acquiredCompetencies == null;
    }

    public String getCompetenciesStringUkr(List<CompetenceBean> competencies) {
        String result = "";
        for (CompetenceBean competence : competencies) {
            result += competence.getName() + ".";
        }
        return result;
    }

    public String getCompetenciesStringEng(List<CompetenceBean> competencies) {
        String result = "";
        for (CompetenceBean competence : competencies) {
            result += competence.getNameEng() + ".";
        }
        return result;
    }
}
