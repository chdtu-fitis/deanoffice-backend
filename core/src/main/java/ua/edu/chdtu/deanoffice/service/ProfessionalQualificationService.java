package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.ProfessionalQualification;
import ua.edu.chdtu.deanoffice.repository.ProfessionalQualificationRepository;

import java.util.List;

@Service
public class ProfessionalQualificationService {
    private ProfessionalQualificationRepository professionalQualificationRepository;

    public ProfessionalQualificationService(ProfessionalQualificationRepository professionalQualificationRepository) {
        this.professionalQualificationRepository = professionalQualificationRepository;
    }

    public List<ProfessionalQualification> getAll() {
        return professionalQualificationRepository.findAll();
    }
}
