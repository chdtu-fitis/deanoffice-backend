package ua.edu.chdtu.deanoffice.api.diplomasupplement;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.service.document.diploma.supplement.DiplomaSupplementService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
@RequestMapping("/documents/diplomas/supplements")
public class DiplomaSupplementController {

    private static Logger log = LoggerFactory.getLogger(DiplomaSupplementController.class);
    private DiplomaSupplementService diplomaSupplementService;

    public DiplomaSupplementController(DiplomaSupplementService diplomaSupplementService) {
        this.diplomaSupplementService = diplomaSupplementService;
    }

    private static ResponseEntity<Resource> buildDocumentResponseEntity(File result, String asciiName) {
        try {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(result));
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + asciiName)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                    .contentLength(result.length())
                    .body(resource);
        } catch (FileNotFoundException e) {
            log.error("Created file not found!", e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(path = "/studentdegrees/{studentDegreeId}")
    public ResponseEntity<Resource> generateForStudent(@PathVariable Integer studentDegreeId) throws IOException, Docx4JException {
        File studentDiplomaSupplement = diplomaSupplementService.formDiplomaSupplementForStudent(studentDegreeId);
        return buildDocumentResponseEntity(studentDiplomaSupplement, studentDiplomaSupplement.getName());
    }
}
