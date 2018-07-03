package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.ProfessionalQualification;
import ua.edu.chdtu.deanoffice.entity.QualificationForSpecialization;
import ua.edu.chdtu.deanoffice.repository.ProfessionalQualificationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProfessionalQualificationService {
    private final QualificationForSpecializationService qualificationForSpecializationService;
    private ProfessionalQualificationRepository professionalQualificationRepository;

    @Autowired
    public ProfessionalQualificationService(
            ProfessionalQualificationRepository professionalQualificationRepository,
            QualificationForSpecializationService qualificationForSpecializationService
    ) {
        this.professionalQualificationRepository = professionalQualificationRepository;
        this.qualificationForSpecializationService = qualificationForSpecializationService;
    }

    public List<ProfessionalQualification> getAll() {
        return professionalQualificationRepository.findAll();
    }

    public ProfessionalQualification create(ProfessionalQualification body) {
        return professionalQualificationRepository.save(body);
    }

    public List<ProfessionalQualification> getAllBySpecializationAndYear(int specializationsId) {
        return qualificationForSpecializationService.findAllBySpecializationIdAndYear(specializationsId).stream()
                .map(QualificationForSpecialization::getProfessionalQualification)
                .collect(Collectors.toList());
    }
}
