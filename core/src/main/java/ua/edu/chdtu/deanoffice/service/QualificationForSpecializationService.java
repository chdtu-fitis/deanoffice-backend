package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.QualificationForSpecialization;
import ua.edu.chdtu.deanoffice.repository.QualificationForSpecializationRepository;

import java.util.List;

@Service
public class QualificationForSpecializationService {
    private QualificationForSpecializationRepository qualificationForSpecializationRepository;

    public QualificationForSpecializationService(QualificationForSpecializationRepository qualificationForSpecializationRepository) {
        this.qualificationForSpecializationRepository = qualificationForSpecializationRepository;
    }

    public List<QualificationForSpecialization> findAllBySpecializationIdAndYear(Integer specializationId, Integer year) {
        return qualificationForSpecializationRepository.findAllBySpecializationIdAndYear(specializationId, year);
    }
}
