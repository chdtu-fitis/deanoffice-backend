package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.ProfessionalQualification;
import ua.edu.chdtu.deanoffice.entity.QualificationForSpecialization;
import ua.edu.chdtu.deanoffice.entity.Specialization;
import ua.edu.chdtu.deanoffice.repository.QualificationForSpecializationRepository;

import java.util.List;
import java.util.stream.Collectors;

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

    public void deleteAll(List<Integer> deleted) throws Exception {
        List<QualificationForSpecialization> qualificationForSpecializations = qualificationForSpecializationRepository.findAllByIds(deleted);
        if (isCurrentYear(qualificationForSpecializations)) {
            qualificationForSpecializationRepository.delete(qualificationForSpecializations);
        } else {
            throw new Exception("You can delete qualification for specialization only for current year (" + currentYearService.get() + ")");
        }
    }

    private boolean isCurrentYear(List<QualificationForSpecialization> qualificationForSpecializations) {
        int currentYear = currentYearService.getYear();
        List<Boolean> isCurrentYear = qualificationForSpecializations.stream()
                .map(QualificationForSpecialization::getYear)
                .map(year -> year == currentYear)
                .collect(Collectors.toList());
        return isCurrentYear.stream().reduce((aBoolean, aBoolean2) -> aBoolean && aBoolean2).get();
    }
}
