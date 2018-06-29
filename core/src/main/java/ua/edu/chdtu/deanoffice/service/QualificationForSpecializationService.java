package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.ProfessionalQualification;
import ua.edu.chdtu.deanoffice.entity.QualificationForSpecialization;
import ua.edu.chdtu.deanoffice.repository.QualificationForSpecializationRepository;

import java.util.List;

@Service
public class QualificationForSpecializationService {
    private QualificationForSpecializationRepository qualificationForSpecializationRepository;

    public QualificationForSpecializationService(QualificationForSpecializationRepository qualificationForSpecializationRepository) {
        this.qualificationForSpecializationRepository = qualificationForSpecializationRepository;
    }

    public List<QualificationForSpecialization> findAllBySpecializationIdAndYear(Integer specializationId) {
        return qualificationForSpecializationRepository.findAllBySpecializationIdAndYear(specializationId);
    }

    public ProfessionalQualification getLastQualification(int specializationsId) {
        QualificationForSpecialization qualificationForSpecialization = qualificationForSpecializationRepository
                .getLastQualificationBySpecializationId(specializationsId);
        if (qualificationForSpecialization == null) {
            return null;
        }
        return qualificationForSpecialization.getProfessionalQualification();
    }
}
