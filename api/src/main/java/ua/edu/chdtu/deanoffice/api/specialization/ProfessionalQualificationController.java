package ua.edu.chdtu.deanoffice.api.specialization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.entity.ProfessionalQualification;
import ua.edu.chdtu.deanoffice.service.ProfessionalQualificationService;
import ua.edu.chdtu.deanoffice.service.QualificationForSpecializationService;

import java.util.List;

@RestController
public class ProfessionalQualificationController {
    private final QualificationForSpecializationService qualificationForSpecializationService;
    private final ProfessionalQualificationService professionalQualificationService;

    @Autowired
    public ProfessionalQualificationController(
            QualificationForSpecializationService qualificationForSpecializationService,
            ProfessionalQualificationService professionalQualificationService
    ) {
        this.qualificationForSpecializationService = qualificationForSpecializationService;
        this.professionalQualificationService = professionalQualificationService;
    }

    @GetMapping("/specializations/{specialization-id}/professional-qualification")
    public ResponseEntity getProfessionalQualificationForSpecialization(@PathVariable("specialization-id") int specializationsId) {
        ProfessionalQualification professionalQualification = qualificationForSpecializationService.getLastQualification(specializationsId);
        return ResponseEntity.ok(professionalQualification);
    }

    @GetMapping("/professional-qualifications")
    public ResponseEntity getQualifications() {
        List<ProfessionalQualification> professionalQualifications = professionalQualificationService.getAll();
        return ResponseEntity.ok(professionalQualifications);
    }

    @PostMapping("/specializations/{specialization-id}/professional-qualifications/{qualification-id}")
    public ResponseEntity setQualificationForSpecialization(
        @PathVariable("specialization-id") int specializationId,
        @PathVariable("qualification-id") int qualificationId
    ) {
        try {
            qualificationForSpecializationService.create(specializationId, qualificationId);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            return ExceptionHandlerAdvice.handleException(exception, ProfessionalQualificationController.class);
        }
    }
}
