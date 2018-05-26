package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.QualificationForSpecialization;
import ua.edu.chdtu.deanoffice.repository.QualificationForSpecializationRepository;

@Service
public class QualificationForSpecializationService {
    private QualificationForSpecializationRepository qualificationForSpecializationRepository;

    public QualificationForSpecializationService(QualificationForSpecializationRepository qualificationForSpecializationRepository) {
        this.qualificationForSpecializationRepository = qualificationForSpecializationRepository;
    }

    public QualificationForSpecialization findBySpecializationIdAndYear(Integer specializationId, Integer year) {
        return qualificationForSpecializationRepository.findBySpecializationIdAndYear(specializationId, year);
    }
}
