package ua.edu.chdtu.deanoffice.api.document.consolidatedreport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
import ua.edu.chdtu.deanoffice.service.GradeService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;
import ua.edu.chdtu.deanoffice.service.course.CourseService;
import ua.edu.chdtu.deanoffice.service.document.ConsolidatedReportService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.io.File;
import java.util.*;

import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;

@RestController
@RequestMapping("/documents/consolidated-report")
public class ConsolidatedReportController extends DocumentResponseController {

    private final CourseForGroupService courseForGroupService;
    private final StudentGroupService studentGroupService;
    private final CourseService courseService;
    private final ConsolidatedReportService consolidatedReportService;
    private final GradeService gradeService;

    @Autowired
    public ConsolidatedReportController(
            CourseForGroupService courseForGroupService,
            StudentGroupService studentGroupService,
            CourseService courseService,
            ConsolidatedReportService consolidatedReportService,
            GradeService gradeService
    ) {
        this.courseForGroupService = courseForGroupService;
        this.studentGroupService = studentGroupService;
        this.courseService = courseService;
        this.consolidatedReportService = consolidatedReportService;
        this.gradeService = gradeService;
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
    public ResponseEntity<Map<Integer, List<StudentGroupDTO>>> getGroupsThatAreStudyingSameCourseTo(
            @RequestBody List<Integer> courseIds,
            @RequestParam Integer degreeId,
            @CurrentUser ApplicationUser user) {
        try {
            Map<Integer, List<StudentGroup>> mapWithStudentGroup =
                    studentGroupService.getGroupsThatAreStudyingSameCoursesTo(
                            courseIds, user.getFaculty().getId(), degreeId
                    );
            Map<Course, List<StudentGroup>> mapWithCourseAndStudentGroup = new HashMap<>();
            mapWithStudentGroup.forEach((courseId, studentGroups) ->
                    mapWithCourseAndStudentGroup.put(courseService.getById(courseId), studentGroups)
            );
            Map<Integer, List<StudentGroupDTO>> mapWithCourseAndStudentGroupsDTOs = new HashMap<>();
            mapWithCourseAndStudentGroup.forEach((course, studentGroups) ->
                    mapWithCourseAndStudentGroupsDTOs.put(course.getId(), map(studentGroups, StudentGroupDTO.class))
            );
            return ResponseEntity.ok(mapWithCourseAndStudentGroupsDTOs);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity getGroupsByCourse(
            @PathVariable Integer courseId,
            @RequestParam Integer degreeId,
            @CurrentUser ApplicationUser user
    ) {
        try {
            List<StudentGroup> studentGroups = studentGroupService.getGroupsThatAreStudyingSameCourseTo(courseId, user.getFaculty().getId(), degreeId);
            return ResponseEntity.ok(map(studentGroups, StudentGroupDTO.class));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PostMapping("/create-document")
    public ResponseEntity getConsolidatedDocumentDocx(
            @RequestBody Map<Integer, List<Integer>> courseForGroupIdsToStudentGroupsIds,
            @CurrentUser ApplicationUser user
    ) {
        try {
            File consolidatedDocument = consolidatedReportService.formConsolidatedReportDocx(
                    getStudentGroupsWithStudentDegreesWhichHaveGoodMarkOrNotFromTheCourse(courseForGroupIdsToStudentGroupsIds, false), user);

            return buildDocumentResponseEntity(
                    consolidatedDocument,
                    consolidatedDocument.getName(),
                    DocumentResponseController.MEDIA_TYPE_DOCX
            );
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private Map<CourseForGroup, List<StudentGroup>> getStudentGroupsWithStudentDegreesWhichHaveGoodMarkOrNotFromTheCourse(
            Map<Integer, List<Integer>> courseForGroupIdsToStudentGroupsIds, boolean isGoodMark
    ) {
        Map<CourseForGroup, List<StudentGroup>> courseForGroupToGroup = new HashMap<>();
        courseForGroupIdsToStudentGroupsIds.forEach((courseForGroupId, studentGroupIds) -> {
            List<StudentGroup> studentGroups = studentGroupService.getByIds(studentGroupIds);
            courseForGroupToGroup.put(courseForGroupService.getCourseForGroup(courseForGroupId), studentGroups);
        });
        Map<CourseForGroup, List<StudentGroup>> courseToStudentGroupsForCreate = new HashMap<>();
        courseForGroupToGroup.forEach((courseForGroup, studentGroups) -> {
            studentGroups.forEach(group -> {
                StudentGroup cloneStudentGroup = createClone(group);
                cloneStudentGroup.setStudentDegrees(
                        gradeService.filterStudentByGrade(cloneStudentGroup.getStudentDegrees(), courseForGroup, isGoodMark)
                );
                if (!cloneStudentGroup.getStudentDegrees().isEmpty()) {
                    List<StudentGroup> item = courseToStudentGroupsForCreate.getOrDefault(courseForGroup, new ArrayList<>());
                    item.add(cloneStudentGroup);
                    courseToStudentGroupsForCreate.put(courseForGroup, item);
                }

            });
        });

        return courseToStudentGroupsForCreate;
    }

    private StudentGroup createClone(StudentGroup group) {
        StudentGroup studentGroup = new StudentGroup();
        studentGroup.setStudentDegrees(group.getStudentDegrees());
        studentGroup.setSpecialization(group.getSpecialization());
        studentGroup.setBeginYears(group.getBeginYears());
        studentGroup.setStudySemesters(group.getStudySemesters());
        studentGroup.setStudyYears(group.getStudyYears());
        studentGroup.setTuitionForm(group.getTuitionForm());
        studentGroup.setTuitionTerm(group.getTuitionTerm());
        studentGroup.setActive(group.isActive());
        studentGroup.setId(group.getId());
        studentGroup.setName(group.getName());
        return studentGroup;
    }

    private static ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, ConsolidatedReportController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
