package ua.edu.chdtu.deanoffice.api.diplomasupplement;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.service.document.diploma.supplement.DiplomaSupplementService;

import java.io.File;

@RestController
@RequestMapping("/diplsuppl")
public class DiplomaSupplementController {

    private DiplomaSupplementService diplomaSupplementService;

    public DiplomaSupplementController(DiplomaSupplementService diplomaSupplementService) {
        this.diplomaSupplementService = diplomaSupplementService;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/students/{studentId}")
    public ResponseEntity<File> generateForStudent(@PathVariable Integer studentId) {
        return ResponseEntity.ok(diplomaSupplementService.fillDiplomaSupplementTemplate(studentId));
    }
}
