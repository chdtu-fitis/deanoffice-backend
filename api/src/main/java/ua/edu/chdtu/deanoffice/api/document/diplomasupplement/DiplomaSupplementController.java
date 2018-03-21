package ua.edu.chdtu.deanoffice.api.document.diplomasupplement;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.service.document.diploma.supplement.DiplomaSupplementService;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/documents/supplements")
public class DiplomaSupplementController extends DocumentResponseController {

    private DiplomaSupplementService diplomaSupplementService;

    public DiplomaSupplementController(DiplomaSupplementService diplomaSupplementService) {
        this.diplomaSupplementService = diplomaSupplementService;
    }

    @GetMapping(path = "/degrees/{studentDegreeId}/{format}")
    public ResponseEntity<Resource> generateForStudent(@PathVariable Integer studentDegreeId,
                                                       @PathVariable String format)
            throws IOException, Docx4JException {
        File studentDiplomaSupplement;
        switch (format) {
            case "pdf": {
                studentDiplomaSupplement = diplomaSupplementService.formDiplomaSupplement(studentDegreeId, format);
                return buildDocumentResponseEntity(studentDiplomaSupplement, studentDiplomaSupplement.getName(), MEDIA_TYPE_PDF);
            }
            default: {
                studentDiplomaSupplement = diplomaSupplementService.formDiplomaSupplement(studentDegreeId, format);
                return buildDocumentResponseEntity(studentDiplomaSupplement, studentDiplomaSupplement.getName(), MEDIA_TYPE_DOCX);
            }
        }
    }
}
