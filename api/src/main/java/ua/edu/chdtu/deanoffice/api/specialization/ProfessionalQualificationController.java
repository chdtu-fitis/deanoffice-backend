package ua.edu.chdtu.deanoffice.api.specialization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.specialization.dto.QualificationEventsDTO;
import ua.edu.chdtu.deanoffice.entity.ProfessionalQualification;
import ua.edu.chdtu.deanoffice.service.ProfessionalQualificationService;
import ua.edu.chdtu.deanoffice.service.QualificationForSpecializationService;

import java.net.URI;
import java.util.List;


import static ua.edu.chdtu.deanoffice.api.general.Util.getNewResourceLocation;

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

    @GetMapping("/specializations/{specialization-id}/professional-qualifications")
    public ResponseEntity getQualificationsForSpecialization(@PathVariable("specialization-id") int specializationsId) {
        List<ProfessionalQualification> professionalQualifications = professionalQualificationService
                .getAllBySpecializationAndYear(specializationsId);
        return ResponseEntity.ok(professionalQualifications);
    }

    @GetMapping("/professional-qualifications")
    public ResponseEntity getQualifications() {
        List<ProfessionalQualification> professionalQualifications = professionalQualificationService.getAll();
        return ResponseEntity.ok(professionalQualifications);
    }

    @PostMapping("/specializations/{specialization-id}/professional-qualifications")
    public ResponseEntity setQualificationForSpecialization(
        @PathVariable("specialization-id") int specializationId,
        @RequestBody QualificationEventsDTO events
    ) {
        try {
            qualificationForSpecializationService.createAll(specializationId, events.getSelected());
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            return ExceptionHandlerAdvice.handleException(exception, ProfessionalQualificationController.class);
        }
    }

    @PostMapping("/professional-qualifications")
    public ResponseEntity create(@RequestBody ProfessionalQualification body) {
        try {
            ProfessionalQualification professionalQualification = professionalQualificationService.create(body);
            URI location = getNewResourceLocation(professionalQualification.getId());
            return ResponseEntity.created(location).body(professionalQualification);
        } catch (Exception exception) {
            return ExceptionHandlerAdvice.handleException(exception, ProfessionalQualificationController.class);
        }
    }
}
