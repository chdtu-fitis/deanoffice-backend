package ua.edu.chdtu.deanoffice.api.speciality;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.api.general.mapper.Mapper;
import ua.edu.chdtu.deanoffice.api.speciality.dto.SpecialityDTO;
import ua.edu.chdtu.deanoffice.api.specialization.dto.SpecializationDTO;
import ua.edu.chdtu.deanoffice.api.specialization.dto.SpecializationView;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.Speciality;
import ua.edu.chdtu.deanoffice.entity.Specialization;
import ua.edu.chdtu.deanoffice.service.SpecialityService;
import ua.edu.chdtu.deanoffice.service.SpecializationService;
import ua.edu.chdtu.deanoffice.util.FacultyUtil;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.util.List;

import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;

@RestController
@RequestMapping("/specialities")
public class SpecialityController {
    private final SpecialityService specialityService;
    private final SpecializationService specializationService;

    @Autowired
    public SpecialityController(SpecialityService specialityService, SpecializationService specializationService) {
        this.specialityService = specialityService;
        this.specializationService = specializationService;
    }

    @GetMapping
    public ResponseEntity getAllSpecialities(@CurrentUser ApplicationUser user) {
        try {
            List<Speciality> specialities = specialityService.getAllInFaculty(user.getFaculty().getId());
            return ResponseEntity.ok(mapToSpecialityDTO(specialities));
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @GetMapping("/active")
    public ResponseEntity getActiveSpecialitiesInFaculty(@RequestParam(required = false) Integer facultyId) {
        try {
            if (facultyId == null)
                facultyId = FacultyUtil.getUserFacultyIdInt();
            List<Speciality> specialities = specialityService.getAllActiveInFaculty(facultyId);
            return ResponseEntity.ok(mapToSpecialityDTO(specialities));
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @GetMapping("/all")
    public ResponseEntity getAllActiveSpecialities() {
        try {
            List<Speciality> specialities = specialityService.getAllActive(true);
            return ResponseEntity.ok(mapToSpecialityDTO(specialities));
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @GetMapping("/{id}/specializations")
    @JsonView(SpecializationView.Basic.class)
    public ResponseEntity getActiveSpecializations(@PathVariable int id, @RequestParam(required = false) Integer facultyId, @RequestParam int degreeId) {
        try {
            if (facultyId == null)
                facultyId = FacultyUtil.getUserFacultyIdInt();
            List<Specialization> specializations = specializationService.getAllActiveBySpecialityAndDegree(id, facultyId, degreeId);
            return ResponseEntity.ok(Mapper.map(specializations, SpecializationDTO.class));
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private List<SpecialityDTO> mapToSpecialityDTO(List<Speciality> source) {
        return map(source, SpecialityDTO.class);
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, SpecialityController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
