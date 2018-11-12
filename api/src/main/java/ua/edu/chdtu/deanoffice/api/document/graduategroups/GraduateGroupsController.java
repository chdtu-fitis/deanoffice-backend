package ua.edu.chdtu.deanoffice.api.document.graduategroups;

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
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.exception.UnauthorizedFacultyDataException;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;
import ua.edu.chdtu.deanoffice.service.GraduateGroupsService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.io.File;
import java.util.List;


@RestController
@RequestMapping("/documents/graduate-groups")
public class GraduateGroupsController extends DocumentResponseController {

    private CourseForGroupService courseForGroupService;
    private GraduateGroupsService graduateGroupsService;

    @Autowired
    public GraduateGroupsController(CourseForGroupService courseForGroupService, GraduateGroupsService graduateGroupsService) {
        this.courseForGroupService = courseForGroupService;
        this.graduateGroupsService = graduateGroupsService;
    }

    @GetMapping("/{group_id}/subjects")
    public ResponseEntity generateListOfSubjectForGraduationGroups(
            @PathVariable("group_id") Integer groupId,
            @CurrentUser ApplicationUser user) {
        try {
            List<CourseForGroup> courseForGroups = courseForGroupService.getCoursesForOneGroup(groupId);
            validateBody(courseForGroups);
            verifyAccess(user, courseForGroups.get(0).getStudentGroup());
            File file = graduateGroupsService.formDocument(courseForGroups);
            return buildDocumentResponseEntity(file, file.getName(), MEDIA_TYPE_PDF);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private void verifyAccess(ApplicationUser user, StudentGroup studentGroup) throws UnauthorizedFacultyDataException {
        if (user.getFaculty().getId() != studentGroup.getSpecialization().getFaculty().getId()) {
            throw new UnauthorizedFacultyDataException("Група знаходить в недоступному факультеті для поточного користувача");
        }
    }

    private void validateBody(List<CourseForGroup> courseForGroups) throws OperationCannotBePerformedException {
        if (courseForGroups.size() == 0) {
            String exceptionMessage = "Групу невдалося знайти, або в групи відсутні предмети для формування документу";
            throw new OperationCannotBePerformedException(exceptionMessage);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, GraduateGroupsController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
