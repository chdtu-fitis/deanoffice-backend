package ua.edu.chdtu.deanoffice.api.specialization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.api.specialization.dto.QualificationEventsDTO;
import ua.edu.chdtu.deanoffice.api.specialization.dto.QualificationForSpecializationDTO;
import ua.edu.chdtu.deanoffice.entity.ProfessionalQualification;
import ua.edu.chdtu.deanoffice.entity.QualificationForSpecialization;
import ua.edu.chdtu.deanoffice.service.ProfessionalQualificationService;
import ua.edu.chdtu.deanoffice.service.QualificationForSpecializationService;

import java.net.URI;
import java.util.List;

import static ua.edu.chdtu.deanoffice.api.general.Util.getNewResourceLocation;
import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;

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

    @RequestMapping(method = RequestMethod.HEAD, value = "/specializations/{specialization-id}/professional-qualifications")
    public ResponseEntity canEdit(@PathVariable("specialization-id") int specializationsId) {
        try {
            if (qualificationForSpecializationService.canEdit(specializationsId)) {
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.noContent().build();
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @GetMapping("/specializations/{specialization-id}/professional-qualifications")
    public ResponseEntity getQualificationsForSpecialization(@PathVariable("specialization-id") int specializationsId) {
        try {
            List<QualificationForSpecialization> qualificationForSpecializations = qualificationForSpecializationService
                    .findAllBySpecializationIdAndYear(specializationsId);
            return ResponseEntity.ok(map(qualificationForSpecializations, QualificationForSpecializationDTO.class));
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @GetMapping("/professional-qualifications")
    public ResponseEntity getQualifications() {
        try {
            List<ProfessionalQualification> professionalQualifications = professionalQualificationService.getAll();
            return ResponseEntity.ok(professionalQualifications);
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @Secured({"ROLE_DEANOFFICER", "ROLE_NAVCH_METHOD"})
    @PostMapping("/specializations/{specialization-id}/professional-qualifications")
    public ResponseEntity changeQualification(
        @PathVariable("specialization-id") int specializationId,
        @RequestBody QualificationEventsDTO events
    ) {
        try {
            if (!events.getSelected().isEmpty()) {
                qualificationForSpecializationService.createAll(specializationId, events.getSelected());
            }
            if (!events.getDeleted().isEmpty()) {
                qualificationForSpecializationService.deleteAll(events.getDeleted());
            }
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @Secured({"ROLE_DEANOFFICER", "ROLE_NAVCH_METHOD"})
    @PostMapping("/professional-qualifications")
    public ResponseEntity create(@RequestBody ProfessionalQualification body) {
        try {
            ProfessionalQualification professionalQualification = professionalQualificationService.create(body);
            URI location = getNewResourceLocation(professionalQualification.getId());
            return ResponseEntity.created(location).body(professionalQualification);
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, ProfessionalQualificationController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
