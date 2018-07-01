package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.ProfessionalQualification;
import ua.edu.chdtu.deanoffice.entity.QualificationForSpecialization;
import ua.edu.chdtu.deanoffice.entity.Specialization;
import ua.edu.chdtu.deanoffice.repository.QualificationForSpecializationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


import static java.util.Arrays.asList;

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

    public void createAll(int specializationId, List<Integer> selected) {
        QualificationForSpecialization baseQualificationForSpecialization = new QualificationForSpecialization();

        Specialization specialization = new Specialization();
        specialization.setId(specializationId);
        baseQualificationForSpecialization.setSpecialization(specialization);

        baseQualificationForSpecialization.setYear(currentYearService.getYear());

        List<QualificationForSpecialization> qualificationForSpecializations = selected.stream()
                .map(qualificationId -> create(baseQualificationForSpecialization, qualificationId))
                .collect(Collectors.toList());
        this.qualificationForSpecializationRepository.save(qualificationForSpecializations);
    }

    public QualificationForSpecialization create(
            QualificationForSpecialization baseQualificationForSpecialization, int qualificationId
    ) {
        QualificationForSpecialization qualificationForSpecialization = baseQualificationForSpecialization.clone();
        ProfessionalQualification professionalQualification = new ProfessionalQualification();
        professionalQualification.setId(qualificationId);
        qualificationForSpecialization.setProfessionalQualification(professionalQualification);
        return qualificationForSpecialization;
    }
}
