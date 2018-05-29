package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.repository.ProfessionalQualificationRepository;

@Service
public class ProfessionalQualificationService {
    private ProfessionalQualificationRepository professionalQualificationRepository;

    public ProfessionalQualificationService(ProfessionalQualificationRepository professionalQualificationRepository) {
        this.professionalQualificationRepository = professionalQualificationRepository;
    }
}
