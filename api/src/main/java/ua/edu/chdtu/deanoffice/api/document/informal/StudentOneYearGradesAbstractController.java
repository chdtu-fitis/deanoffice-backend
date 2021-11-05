package ua.edu.chdtu.deanoffice.api.document.informal;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.api.document.groupgrade.GroupGradeReportController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.service.FacultyService;
import ua.edu.chdtu.deanoffice.service.document.report.personalstatement.StudentOneYearGradesAbstractService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/documents/student-one-year-grades-abstract")
public class StudentOneYearGradesAbstractController extends DocumentResponseController {

    private StudentOneYearGradesAbstractService studentOneYearGradesAbstractService;
    private FacultyService facultyService;

    public StudentOneYearGradesAbstractController(StudentOneYearGradesAbstractService studentOneYearGradesAbstractService, FacultyService facultyService) {
        this.studentOneYearGradesAbstractService = studentOneYearGradesAbstractService;
        this.facultyService = facultyService;
    }

    @GetMapping(path = "/{year}/docx")
    public ResponseEntity<Resource> getPersonalStatement(@RequestParam Integer[] studentDegreeIds,
                                                         @PathVariable Integer year,
                                                         @CurrentUser ApplicationUser user) {
        try {
            for (Integer studentDegreeId : studentDegreeIds) {
                facultyService.checkStudentDegree(studentDegreeId, user.getFaculty().getId());
            }
            File result = studentOneYearGradesAbstractService.formDocument(year, Arrays.asList(studentDegreeIds));
            return buildDocumentResponseEntity(result, result.getName(), MEDIA_TYPE_DOCX);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/front/docx")
    public ResponseEntity<Resource> generatePersonalWrapperFront(@RequestParam List<Integer> studentDegreeIds,
                                                              @CurrentUser ApplicationUser user) {
        try {
            for (Integer studentDegreeId:studentDegreeIds)
                facultyService.checkStudentDegree(studentDegreeId, user.getFaculty().getId());
            File result  = studentOneYearGradesAbstractService.preparePersonalWrapperFront(studentDegreeIds);
            return buildDocumentResponseEntity(result , result.getName(), MEDIA_TYPE_DOCX);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/back/docx")
    public ResponseEntity<Resource> generatePersonalWrapperBack(@RequestParam List<Integer> studentDegreeIds,
                                                              @CurrentUser ApplicationUser user) {
        try {
            for (Integer studentDegreeId:studentDegreeIds)
                facultyService.checkStudentDegree(studentDegreeId, user.getFaculty().getId());
            File result  = studentOneYearGradesAbstractService.preparePersonalWrapperBack(studentDegreeIds);
            return buildDocumentResponseEntity(result , result.getName(), MEDIA_TYPE_DOCX);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, GroupGradeReportController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
