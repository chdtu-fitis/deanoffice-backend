package ua.edu.chdtu.deanoffice.api.document.teacher;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.service.TeacherService;
import ua.edu.chdtu.deanoffice.service.document.teacher.exam.ticket.TeacherDocumentsService;

import java.io.File;
import java.io.IOException;

@RestController
public class ExamTasksController extends DocumentResponseController {
    private TeacherDocumentsService teacherDocumentsService;
    private TeacherService teacherService;

    public ExamTasksController(TeacherDocumentsService teacherDocumentsService, TeacherService teacherService) {
        this.teacherDocumentsService = teacherDocumentsService;
        this.teacherService = teacherService;
    }

    @GetMapping("/teachers/{teacherId}/documents/exam-tickets")
    public ResponseEntity<Resource> createExamTickets(@PathVariable  int teacherId, @RequestParam int courseId,
                                                      @RequestParam int groupId, @RequestParam int departmentId,
                                                      @RequestParam String protocolNumber, @RequestParam String protocolDate)
            throws Docx4JException, IOException {
        File examReport = teacherDocumentsService.generateCourseTicketsDocument(teacherId, courseId, groupId, departmentId, protocolNumber, protocolDate);
        return buildDocumentResponseEntity(examReport, examReport.getName(), MEDIA_TYPE_DOCX);
    }
}
