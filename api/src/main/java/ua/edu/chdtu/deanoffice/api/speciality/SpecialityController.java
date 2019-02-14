package ua.edu.chdtu.deanoffice.api.speciality;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.api.speciality.dto.SpecialityDTO;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.Speciality;
import ua.edu.chdtu.deanoffice.service.SpecialityService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.util.List;

import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;

@RestController
@RequestMapping("/specialities")
public class SpecialityController {
    private final SpecialityService specialityService;

    @Autowired
    public SpecialityController(SpecialityService specialityService) {
        this.specialityService = specialityService;
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
    public ResponseEntity getActiveSpecialities(@CurrentUser ApplicationUser user) {
        try {
            List<Speciality> specialities = specialityService.getAllActiveInFaculty(user.getFaculty().getId());
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

    private List<SpecialityDTO> mapToSpecialityDTO(List<Speciality> source) {
        return map(source, SpecialityDTO.class);
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, SpecialityController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
