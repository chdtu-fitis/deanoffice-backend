package ua.edu.chdtu.deanoffice.api.speciality;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.speciality.dto.SpecialityDTO;
import ua.edu.chdtu.deanoffice.entity.Speciality;
import ua.edu.chdtu.deanoffice.service.SpecialityService;

import java.lang.reflect.Type;
import java.util.List;

@RestController
@RequestMapping("/specialities")
public class SpecialityController {
    private final SpecialityService specialityService;

    @Autowired
    public SpecialityController(SpecialityService specialityService) {
        this.specialityService = specialityService;
    }

    @GetMapping("")
    public ResponseEntity getSpecialities(
            @RequestParam(value = "only-active", required = false, defaultValue = "true") boolean onlyAcyive
    ) {
        List<Speciality> specialities = specialityService.getSpecialitybyActive();
        return ResponseEntity.ok(parseToSpecialityDTO(specialities));
    }

    private List<SpecialityDTO> parseToSpecialityDTO(List<Speciality> specialities) {
        Type type = new TypeToken<List<SpecialityDTO>>() {}.getType();
        return new ModelMapper().map(specialities, type);
    }
}
