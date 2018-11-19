package ua.edu.chdtu.deanoffice.api.document.diplomasupplement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;
import ua.edu.chdtu.deanoffice.service.GraduateGroupCoursesService;
import ua.edu.chdtu.deanoffice.service.security.FacultyAuthorizationService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/documents")
public class GraduatesDocumentsController extends DocumentResponseController {

    private final CourseForGroupService courseForGroupService;
    private final GraduateGroupCoursesService graduateGroupCoursesService;
    private final FacultyAuthorizationService facultyAuthorizationService;

    @Autowired
    public GraduatesDocumentsController(
            CourseForGroupService courseForGroupService,
            GraduateGroupCoursesService graduateGroupCoursesService,
            FacultyAuthorizationService facultyAuthorizationService) {
        this.courseForGroupService = courseForGroupService;
        this.graduateGroupCoursesService = graduateGroupCoursesService;
        this.facultyAuthorizationService = facultyAuthorizationService;
    }

    @GetMapping("/groups/{groupId}/graduate-courses")
    public ResponseEntity generateListOfCourseForGraduationGroups(
            @PathVariable("groupId") Integer groupId,
            @CurrentUser ApplicationUser user) {
        try {
            List<CourseForGroup> courseForGroups = courseForGroupService.getCoursesForOneGroup(groupId);
            validateBody(courseForGroups);
            facultyAuthorizationService.verifyAccessibilityOfStudentGroup(user, courseForGroups.get(0).getStudentGroup());
            File file = graduateGroupCoursesService.formDocument(courseForGroups);
            return buildDocumentResponseEntity(file, file.getName(), MEDIA_TYPE_PDF);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private void validateBody(List<CourseForGroup> courseForGroups) throws OperationCannotBePerformedException {
        if (courseForGroups.size() == 0) {
            String exceptionMessage = "Групу не вдалося знайти, або в групи відсутні предмети для формування документу";
            throw new OperationCannotBePerformedException(exceptionMessage);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, GraduatesDocumentsController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
