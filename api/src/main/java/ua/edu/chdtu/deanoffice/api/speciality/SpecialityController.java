package ua.edu.chdtu.deanoffice.api.speciality;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.speciality.dto.SpecialityDTO;
import ua.edu.chdtu.deanoffice.entity.Speciality;
import ua.edu.chdtu.deanoffice.service.SpecialityService;

import java.util.List;

import static ua.edu.chdtu.deanoffice.api.general.parser.Parser.parse;

@RestController
@RequestMapping("/specialities")
public class SpecialityController {
    private final SpecialityService specialityService;

    @Autowired
    public SpecialityController(SpecialityService specialityService) {
        this.specialityService = specialityService;
    }

    @GetMapping("")
    public ResponseEntity getAllSpecialities() {
        List<Speciality> specialities = specialityService.getSpecialityByActive(false);
        return ResponseEntity.ok(parse(specialities, SpecialityDTO.class));
    }

    @GetMapping("/active")
    public ResponseEntity getActiveSpecialities() {
        List<Speciality> specialities = specialityService.getSpecialityByActive(true);
        return ResponseEntity.ok(parse(specialities, SpecialityDTO.class));
    }
}
