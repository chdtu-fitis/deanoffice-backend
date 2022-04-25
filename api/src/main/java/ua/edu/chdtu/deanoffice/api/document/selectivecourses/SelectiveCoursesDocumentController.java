package ua.edu.chdtu.deanoffice.api.document.selectivecourses;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.api.document.groupgrade.GroupGradeReportController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.service.document.selectivecourses.SelectiveCoursesDocumentService;

import java.io.File;

@RestController
@RequestMapping("/documents/selective-courses")
public class SelectiveCoursesDocumentController extends DocumentResponseController {

    private SelectiveCoursesDocumentService selectiveCoursesDocumentService;

    public SelectiveCoursesDocumentController(SelectiveCoursesDocumentService selectiveCoursesDocumentService) {
        this.selectiveCoursesDocumentService = selectiveCoursesDocumentService;
    }

    @GetMapping("/docx")
    public ResponseEntity generateIndividualCurriculum(
            @RequestParam int studyYear,
            @RequestParam int course,
            @RequestParam int degreeId
    ) {
        File file = null;
        try {
            file = selectiveCoursesDocumentService.formDocument(studyYear, course, degreeId);
        } catch (Exception e) {
            return handleException(e);
        }
        return buildDocumentResponseEntity(file, file.getName(), MEDIA_TYPE_DOCX);
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, GroupGradeReportController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
