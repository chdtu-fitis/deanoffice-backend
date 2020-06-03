package ua.edu.chdtu.deanoffice.api.document.individualcurriculum;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.service.document.individualcurriculum.IndividualCurriculumService;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/documents/individual-curriculum")
public class IndividualCurriculumController extends DocumentResponseController {
    private final IndividualCurriculumService individualCurriculumService;

    public IndividualCurriculumController(IndividualCurriculumService individualCurriculumService) {
        this.individualCurriculumService = individualCurriculumService;
    }

    @GetMapping("/{studyYear}/docx")
    public ResponseEntity generateIndividualCurriculum(
            @RequestParam List<Integer> studentDegreeIds,
            @PathVariable Integer studyYear
    ) {
        File file = null;
        try {
            file = individualCurriculumService.createIndividualCurriculumDocx(studentDegreeIds, ""+studyYear);
        } catch (FileNotFoundException | Docx4JException e) {
            e.printStackTrace();
        }

        return buildDocumentResponseEntity(file, file.getName(), MEDIA_TYPE_DOCX);
    }
}
