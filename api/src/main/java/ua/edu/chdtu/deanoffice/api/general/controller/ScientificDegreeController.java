package ua.edu.chdtu.deanoffice.api.general.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.teacher.ScientificDegreeDTO;
import ua.edu.chdtu.deanoffice.entity.ScientificDegree;
import ua.edu.chdtu.deanoffice.service.ScientificDegreeService;

import java.util.List;

import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;

@RestController
@RequestMapping("/scientific-degrees")
public class ScientificDegreeController {
    private final ScientificDegreeService scientificDegreeService;

    @Autowired
    public ScientificDegreeController(ScientificDegreeService scientificDegreeService) {
        this.scientificDegreeService = scientificDegreeService;
    }

    @GetMapping
    public ResponseEntity getScientificDegrees() {
        List<ScientificDegree> scientificDegrees = scientificDegreeService.getScientificDegrees();
        return ResponseEntity.ok(map(scientificDegrees, ScientificDegreeDTO.class));
    }
}
