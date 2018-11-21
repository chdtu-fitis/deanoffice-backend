package ua.edu.chdtu.deanoffice.api.document.consolidatedreport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.course.dto.CourseDTO;
import ua.edu.chdtu.deanoffice.api.course.dto.CourseForGroupDTO;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.api.group.dto.StudentGroupDTO;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.exception.UnauthorizedFacultyDataException;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;
import ua.edu.chdtu.deanoffice.service.course.CourseService;
import ua.edu.chdtu.deanoffice.service.document.ConsolidatedReportService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;

@RestController
@RequestMapping("/documents/consolidated-report")
public class ConsolidatedReportController extends DocumentResponseController {

    private final CourseForGroupService courseForGroupService;
    private final StudentGroupService studentGroupService;
    private final CourseService courseService;
    private final ConsolidatedReportService consolidatedReportService;

    @Autowired
    public ConsolidatedReportController(
            CourseForGroupService courseForGroupService,
            StudentGroupService studentGroupService,
            CourseService courseService,
            ConsolidatedReportService consolidatedReportService
    ) {
        this.courseForGroupService = courseForGroupService;
        this.studentGroupService = studentGroupService;
        this.courseService = courseService;
        this.consolidatedReportService = consolidatedReportService;
    }

    @GetMapping("/groups/{groupId}/subjects")
    public ResponseEntity getCourseForGroupByGroupAndSemester(
            @PathVariable Integer groupId,
            @RequestParam Integer semester,
            @CurrentUser ApplicationUser user) {
        try {
            List<CourseForGroup> coursesForGroupBySemester = courseForGroupService.getCoursesForGroupBySemester(groupId, semester);
            if (coursesForGroupBySemester.stream().anyMatch(courseForGroup ->
                    courseForGroup.getStudentGroup().getSpecialization().getFaculty().getId() != user.getFaculty().getId())) {
                throw new UnauthorizedFacultyDataException("Група знаходиться в недоступному для користувача факультеті");
            }
            return ResponseEntity.ok(map(coursesForGroupBySemester, CourseForGroupDTO.class));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PostMapping("/groups/subjects")
    public ResponseEntity getGroupsThatAreStudyingSameCourseTo(
            @RequestBody List<Integer> courseIds,
            @CurrentUser ApplicationUser user) {
        try {
            Map<Integer, List<StudentGroup>> mapWithStudentGroup =
                    studentGroupService.getGroupsThatAreStudyingSameCoursesTo(courseIds, user.getFaculty().getId());
            Map<Course, List<StudentGroup>> mapWithCourseAndStudentGroup = new HashMap<>();
            mapWithStudentGroup.forEach((courseId, studentGroups) ->
                    mapWithCourseAndStudentGroup.put(courseService.getById(courseId), studentGroups)
            );
            Map<CourseDTO, List<StudentGroupDTO>> mapWithCourseAndStundentGroupsDTOs = new HashMap<>();
            mapWithCourseAndStudentGroup.forEach((course, studentGroups) ->
                    mapWithCourseAndStundentGroupsDTOs
                            .put((CourseDTO) map(course, CourseDTO.class), map(studentGroups, StudentGroupDTO.class))
            );
            return ResponseEntity.ok(mapWithCourseAndStundentGroupsDTOs);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity getGroupsByCourse(@PathVariable Integer courseId, @CurrentUser ApplicationUser user) {
        try {
            List<StudentGroup> studentGroups = studentGroupService.getGroupsThatAreStudyingSameCourseTo(courseId, user.getFaculty().getId());
            return ResponseEntity.ok(map(studentGroups, StudentGroupDTO.class));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PostMapping("/create-document")
    public ResponseEntity getConsolidatedDocument(
            @RequestBody Map<Integer, List<Integer>> mapCourseForGroupIdToStudentGroupsIds,
            @CurrentUser ApplicationUser user
    ) {
        try {
            Map<CourseForGroup, List<StudentGroup>> mapCourseToStudentGroups = new HashMap<>();
            mapCourseForGroupIdToStudentGroupsIds.forEach((courseForGroupId, studentGroupId) -> {
                Integer[] studentGroupIdsArray = new Integer[studentGroupId.size()];
                List<StudentGroup> studentGroups = studentGroupService.getByIds(studentGroupId.toArray(studentGroupIdsArray));
                mapCourseToStudentGroups.put(courseForGroupService.getCourseForGroup(courseForGroupId), studentGroups);
            });
            File consolidatedDocument = consolidatedReportService.formConsolidatedReport(mapCourseToStudentGroups, user);
            return buildDocumentResponseEntity(
                    consolidatedDocument,
                    consolidatedDocument.getName(),
                    DocumentResponseController.MEDIA_TYPE_PDF
            );
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private static ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, ConsolidatedReportController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
