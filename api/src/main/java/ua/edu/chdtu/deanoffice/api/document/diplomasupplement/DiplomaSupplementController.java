package ua.edu.chdtu.deanoffice.api.document.diplomasupplement;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.service.FacultyService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;
import ua.edu.chdtu.deanoffice.service.document.diploma.supplement.DiplomaSupplementService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.io.File;

@RestController
@RequestMapping("/documents/supplements")
public class DiplomaSupplementController extends DocumentResponseController {

    private DiplomaSupplementService diplomaSupplementService;
    private FacultyService facultyService;

    public DiplomaSupplementController(DiplomaSupplementService diplomaSupplementService,
                                       FacultyService facultyService) {
        this.diplomaSupplementService = diplomaSupplementService;
        this.facultyService = facultyService;
    }

    @GetMapping("/degrees/{studentDegreeId}/docx")
    public ResponseEntity<Resource> generateDocxForStudent(@PathVariable Integer studentDegreeId,
                                                           @CurrentUser ApplicationUser user) {
        try {
            facultyService.checkStudentDegree(studentDegreeId, user.getFaculty().getId());
            File studentDiplomaSupplement = diplomaSupplementService.formDiplomaSupplement(studentDegreeId, FileFormatEnum.DOCX);
            return buildDocumentResponseEntity(studentDiplomaSupplement, studentDiplomaSupplement.getName(), MEDIA_TYPE_DOCX);
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @GetMapping("/degrees/{studentDegreeId}/pdf")
    public ResponseEntity<Resource> generatePdfForStudent(@PathVariable Integer studentDegreeId,
                                                          @CurrentUser ApplicationUser user) {
        try {
            facultyService.checkStudentDegree(studentDegreeId, user.getFaculty().getId());
            File studentDiplomaSupplement = diplomaSupplementService.formDiplomaSupplement(studentDegreeId, FileFormatEnum.PDF);
            return buildDocumentResponseEntity(studentDiplomaSupplement, studentDiplomaSupplement.getName(), MEDIA_TYPE_PDF);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, DiplomaSupplementController.class);
    }
}
