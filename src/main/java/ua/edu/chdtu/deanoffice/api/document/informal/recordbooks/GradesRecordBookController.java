package ua.edu.chdtu.deanoffice.api.document.informal.recordbooks;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.TuitionForm;
import ua.edu.chdtu.deanoffice.service.document.informal.recordbooks.grades.GradesRecordBookService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.io.File;

@RestController
@RequestMapping("/documents/grades-journal")
public class GradesRecordBookController extends DocumentResponseController {

    private GradesRecordBookService gradesRecordBookService;

    public GradesRecordBookController(GradesRecordBookService gradesRecordBookService){
        this.gradesRecordBookService = gradesRecordBookService;
    }

    @GetMapping("/students")
    public ResponseEntity getStudentGroupFile(@RequestParam int degreeId, @RequestParam int year,
                                              @RequestParam(required = false, defaultValue = "0") int groupId,
                                              @CurrentUser ApplicationUser user){
        try {
            File file = gradesRecordBookService.createStudentsListsPdf(degreeId, year, user.getFaculty().getId(), groupId);
            return buildDocumentResponseEntity(file, file.getName(), MEDIA_TYPE_PDF);
        } catch (Exception e){
            return handleException(e);
        }
    }

    @GetMapping("/courses/pdf")
    public ResponseEntity getSubjectsFile(@RequestParam int degreeId, @RequestParam int year,
                                          @RequestParam(required = false, defaultValue = "0") int semester,
                                          @RequestParam(required = false) TuitionForm tuitionForm,
                                          @RequestParam(required = false, defaultValue = "0") int groupId,
                                          @CurrentUser ApplicationUser user) {
        try{
            File file = gradesRecordBookService.createCoursesListsPdf(degreeId, year, semester, tuitionForm, groupId, user.getFaculty().getId());
            return buildDocumentResponseEntity(file, file.getName(), MEDIA_TYPE_PDF);
        } catch (Exception e){
            return handleException(e);
        }
    }

    @GetMapping("/courses/docx")
    public ResponseEntity getCoursesInDocxFile(@RequestParam int degreeId, @RequestParam int year,
                                               @RequestParam(required = false, defaultValue = "0") int semester,
                                               @RequestParam(required = false) TuitionForm tuitionForm,
                                               @RequestParam(required = false, defaultValue = "0") int groupId,
                                               @CurrentUser ApplicationUser user) {
        try{
            File file = gradesRecordBookService.createCoursesListsDocx(degreeId, year, semester, tuitionForm, groupId, user.getFaculty().getId());
            return buildDocumentResponseEntity(file, file.getName(), MEDIA_TYPE_DOCX);
        } catch (Exception e){
            return handleException(e);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, GradesRecordBookController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
