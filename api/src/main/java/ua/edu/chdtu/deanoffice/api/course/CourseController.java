package ua.edu.chdtu.deanoffice.api.course;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.course.dto.CourseDTO;
import ua.edu.chdtu.deanoffice.api.course.dto.CourseForGroupDTO;
import ua.edu.chdtu.deanoffice.api.course.dto.CourseForGroupView;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.entity.Teacher;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;
import ua.edu.chdtu.deanoffice.service.CourseService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;
import ua.edu.chdtu.deanoffice.service.TeacherService;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice.handleException;
import static ua.edu.chdtu.deanoffice.api.general.parser.Parser.parse;

@RequestMapping("/")
@RestController
public class CourseController {
    private CourseForGroupService courseForGroupService;
    private CourseService courseService;
    private StudentGroupService studentGroupService;
    private TeacherService teacherService;

    @Autowired
    public CourseController(
            CourseForGroupService courseForGroupService,
            CourseService courseService,
            StudentGroupService studentGroupService,
            TeacherService teacherService
    ) {
        this.courseForGroupService = courseForGroupService;
        this.courseService = courseService;
        this.studentGroupService = studentGroupService;
        this.teacherService = teacherService;
    }

    @GetMapping("/courses")
    public ResponseEntity getCoursesBySemester(@RequestParam(value = "semester") int semester) {
        List<Course> courses = courseService.getCoursesBySemester(semester);
        return ResponseEntity.ok(parse(courses, CourseDTO.class));
    }

    @GetMapping("/groups/{groupId}/courses")
    @JsonView(CourseForGroupView.Course.class)
    public ResponseEntity getCoursesByGroupAndSemester(@PathVariable int groupId, @RequestParam int semester) {
        List<CourseForGroup> coursesForGroup = courseForGroupService.getCoursesForGroupBySemester(groupId, semester);
        return ResponseEntity.ok(parse(coursesForGroup, CourseForGroupDTO.class));
    }

    @PostMapping("/groups/{groupId}/courses")
    public ResponseEntity.BodyBuilder addCoursesForGroup(@RequestBody Map<String,List> body, @PathVariable Integer groupId) {
        List<CourseForGroupDTO> newCourses = body.get("newCourses");
        List<CourseForGroupDTO> updatedCourses = body.get("updatedCourses");
        List<Integer> deleteCoursesIds = body.get("deleteCoursesIds");

        if (newCourses == null || updatedCourses == null || deleteCoursesIds == null) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        Set<CourseForGroup> newCoursesForGroup = new HashSet<>();
        Set<CourseForGroup> updatedCoursesForGroup = new HashSet<>();

        for (CourseForGroupDTO newCourseForGroup: newCourses) {
            CourseForGroup courseForGroup = new CourseForGroup();

            Course course = courseService.getCourse(newCourseForGroup.getCourse().getId());
            courseForGroup.setCourse(course);

            StudentGroup studentGroup = studentGroupService.getById(groupId);
            courseForGroup.setStudentGroup(studentGroup);

            Teacher teacher = teacherService.getTeacher(newCourseForGroup.getTeacher().getId());
            courseForGroup.setTeacher(teacher);

            courseForGroup.setExamDate(newCourseForGroup.getExamDate());

            newCoursesForGroup.add(courseForGroup);
        }

        for (CourseForGroupDTO updatedCourseForGroup: updatedCourses) {
            CourseForGroup courseForGroup = courseForGroupService.getCourseForGroup(updatedCourseForGroup.getId());

            Teacher teacher = teacherService.getTeacher(updatedCourseForGroup.getTeacher().getId());
            courseForGroup.setTeacher(teacher);

            courseForGroup.setExamDate(updatedCourseForGroup.getExamDate());

            updatedCoursesForGroup.add(courseForGroup);
        }
        courseForGroupService.addCourseForGroupAndNewChanges(newCoursesForGroup, updatedCoursesForGroup, deleteCoursesIds);
        return ResponseEntity.ok();
    }

    @GetMapping("/groups/{groupId}/courses/all")
    @JsonView(CourseForGroupView.Course.class)
    public ResponseEntity getCourses(@PathVariable int groupId) {
        List<CourseForGroup> courseForGroups = courseForGroupService.getCoursesForOneGroup(groupId);
        return ResponseEntity.ok(parse(courseForGroups, CourseForGroupDTO.class));
    }

    @GetMapping("/specialization/{id}/courses")
    @JsonView(CourseForGroupView.Basic.class)
    public ResponseEntity getCoursesBySpecialization(@PathVariable int id, @RequestParam("semester") int semester) {
        List<CourseForGroup> courseForGroups = courseForGroupService.getCourseForGroupBySpecialization(id, semester);
        return ResponseEntity.ok(parse(courseForGroups, CourseForGroupDTO.class));
    }

    @ResponseBody
    @PostMapping("/courses")
    public ResponseEntity createCourse(@RequestBody Course course) {
        try {
            this.courseService.createCourse(course);
            return new ResponseEntity(HttpStatus.CREATED);
        }
        catch (DataIntegrityViolationException exception) {
            return handleException(exception);
        }
    }
}
