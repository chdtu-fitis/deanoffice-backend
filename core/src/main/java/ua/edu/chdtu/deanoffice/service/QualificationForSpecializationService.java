package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.ProfessionalQualification;
import ua.edu.chdtu.deanoffice.entity.QualificationForSpecialization;
import ua.edu.chdtu.deanoffice.entity.Specialization;
import ua.edu.chdtu.deanoffice.repository.QualificationForSpecializationRepository;

import java.util.List;

@Service
public class QualificationForSpecializationService {
    private QualificationForSpecializationRepository qualificationForSpecializationRepository;
    private CurrentYearService currentYearService;

    public QualificationForSpecializationService(
            QualificationForSpecializationRepository qualificationForSpecializationRepository,
            CurrentYearService currentYearService
    ) {
        this.qualificationForSpecializationRepository = qualificationForSpecializationRepository;
        this.currentYearService = currentYearService;
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

    public void create(int specializationId, int qualificationId) {
        QualificationForSpecialization qualificationForSpecialization = new QualificationForSpecialization();

        Specialization specialization = new Specialization();
        specialization.setId(specializationId);
        qualificationForSpecialization.setSpecialization(specialization);

        ProfessionalQualification professionalQualification = new ProfessionalQualification();
        professionalQualification.setId(qualificationId);
        qualificationForSpecialization.setProfessionalQualification(professionalQualification);

        qualificationForSpecialization.setYear(currentYearService.getYear());

        this.qualificationForSpecializationRepository.save(qualificationForSpecialization);
    }
}
