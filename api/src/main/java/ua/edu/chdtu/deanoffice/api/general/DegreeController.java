package ua.edu.chdtu.deanoffice.api.general;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.general.dto.NamedDTO;
import ua.edu.chdtu.deanoffice.entity.Degree;
import ua.edu.chdtu.deanoffice.service.DegreeService;

import java.lang.reflect.Type;
import java.util.List;

@RestController
@RequestMapping("/degrees")
public class DegreeController {
    @Autowired
    private DegreeService degreeService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<NamedDTO>> getDegrees() {
        List<Degree> degrees = degreeService.getDegrees();
        ModelMapper modelMapper = new ModelMapper();
        Type listType = new TypeToken<List<NamedDTO>>() {}.getType();
        List<NamedDTO> degreeDTOs = modelMapper.map(degrees, listType);
        return ResponseEntity.ok(degreeDTOs);
    }
}
