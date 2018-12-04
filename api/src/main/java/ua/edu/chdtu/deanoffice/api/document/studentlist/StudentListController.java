package ua.edu.chdtu.deanoffice.api.document.studentlist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.api.document.reportsjournal.ReportsJournalController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.service.document.report.studentslist.StudentsListService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.io.File;

@RestController
@RequestMapping("/documents/student-list")
public class StudentListController extends DocumentResponseController {
    private StudentsListService studentsListService;

    @Autowired
    public StudentListController(StudentsListService studentsListService) {
        this.studentsListService = studentsListService;
    }

    @GetMapping("/year/{year}/degree/{degreeId}")
    public ResponseEntity<Resource> generateForYear(
        @PathVariable Integer year, @PathVariable Integer degreeId,
        @RequestParam("tuitionForm") String tuitionForm,
        @CurrentUser ApplicationUser user
    ) {
        try {
            File reportsJournal = studentsListService.prepareReport(degreeId, year , user.getFaculty().getId(),tuitionForm);
            return buildDocumentResponseEntity(reportsJournal, reportsJournal.getName(), MEDIA_TYPE_DOCX);
        } catch (Exception e) {
            return handleException(e);
        }
    }
    private static ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, ReportsJournalController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
