package ua.edu.chdtu.deanoffice.api.document.individualcurriculum;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/documents/individual-curriculum")
public class IndividualCurriculumController {
    @GetMapping
    public ResponseEntity generateIndividualCurriculum(
            @RequestParam(required = false, defaultValue = "0") Integer groupId,
            @RequestParam(required = false) List<Integer> studentsIds
            ) {
        return new ResponseEntity(HttpStatus.OK);
    }
}
