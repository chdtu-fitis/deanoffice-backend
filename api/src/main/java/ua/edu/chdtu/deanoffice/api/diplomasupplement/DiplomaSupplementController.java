package ua.edu.chdtu.deanoffice.api.diplomasupplement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.service.document.diploma.supplement.DiplomaSupplementService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@RestController
@RequestMapping("/diplsuppl")
public class DiplomaSupplementController {

    private static Logger log = LoggerFactory.getLogger(DiplomaSupplementController.class);

    private DiplomaSupplementService diplomaSupplementService;

    public DiplomaSupplementController(DiplomaSupplementService diplomaSupplementService) {
        this.diplomaSupplementService = diplomaSupplementService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public void start() {

    }

    @RequestMapping(method = RequestMethod.GET, path = "/students/{studentId}")
    public ResponseEntity<Resource> generateForStudent(@PathVariable Integer studentId) {
        File studentDiplomaSupplement = diplomaSupplementService.formDiplomaSupplementForStudent(studentId);
        return getResourceResponseEntity(studentDiplomaSupplement, studentDiplomaSupplement.getName());
    }

    @RequestMapping(method = RequestMethod.GET, path = "/groups/{groupId}")
    public ResponseEntity<Resource> generateForGroup(@PathVariable Integer groupId) {
        File groupDiplomaSupplements = diplomaSupplementService.formDiplomaSupplementForGroup(groupId);
        return getResourceResponseEntity(groupDiplomaSupplements, groupDiplomaSupplements.getName());
    }

    private static ResponseEntity<Resource> getResourceResponseEntity(File result, String asciiName) {
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
}
