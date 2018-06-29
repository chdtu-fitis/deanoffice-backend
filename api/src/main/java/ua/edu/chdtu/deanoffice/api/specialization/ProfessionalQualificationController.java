package ua.edu.chdtu.deanoffice.api.specialization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.entity.ProfessionalQualification;
import ua.edu.chdtu.deanoffice.service.QualificationForSpecializationService;

@RestController
@RequestMapping("/specializations")
public class ProfessionalQualificationController {
    private final QualificationForSpecializationService qualificationForSpecializationService;

    @Autowired
    public ProfessionalQualificationController(QualificationForSpecializationService qualificationForSpecializationService) {
        this.qualificationForSpecializationService = qualificationForSpecializationService;
    }

    @GetMapping("/{specialization-id}/professional-qualification")
    public ResponseEntity getProfessionalQualificationForSpecialization(@PathVariable("specialization-id") int specializationsId) {
        ProfessionalQualification professionalQualification = qualificationForSpecializationService.getLastQualification(specializationsId);
        return ResponseEntity.ok(professionalQualification);
    }
}
