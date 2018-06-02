package ua.edu.chdtu.deanoffice.api.course;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.course.dto.CourseDTO;
import ua.edu.chdtu.deanoffice.api.course.dto.CourseForGroupDTO;
import ua.edu.chdtu.deanoffice.api.course.dto.CourseForGroupView;
import ua.edu.chdtu.deanoffice.api.course.util.CourseForGroupUpdateHolder;
import ua.edu.chdtu.deanoffice.api.course.util.CoursesForGroupHolder;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.dto.NamedDTO;
import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.service.*;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ua.edu.chdtu.deanoffice.api.general.Util.getNewResourceLocation;
import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;

@RestController
public class CourseController {
    private CourseForGroupService courseForGroupService;
    private CourseService courseService;
    private StudentGroupService studentGroupService;
    private TeacherService teacherService;
    private GradeService gradeService;
    private CourseNameService courseNameService;
    @Autowired
    public CourseController(
            CourseForGroupService courseForGroupService,
            CourseService courseService,
            StudentGroupService studentGroupService,
            TeacherService teacherService,
            CourseNameService courseNameService,
            GradeService gradeService
    ) {
        this.courseForGroupService = courseForGroupService;
        this.courseService = courseService;
        this.studentGroupService = studentGroupService;
        this.teacherService = teacherService;
        this.courseNameService = courseNameService;
        this.gradeService = gradeService;
    }


    @GetMapping("/courses")
    public ResponseEntity getCoursesBySemester(@RequestParam(value = "semester") int semester) {
        List<Course> courses = courseService.getCoursesBySemester(semester);
        return ResponseEntity.ok(map(courses, CourseDTO.class));
    }

    @GetMapping("/groups/{groupId}/courses")
    @JsonView(CourseForGroupView.Course.class)
    public ResponseEntity getCoursesByGroupAndSemester(@PathVariable int groupId, @RequestParam int semester) {
        List<CourseForGroup> coursesForGroup = courseForGroupService.getCoursesForGroupBySemester(groupId, semester);
        return ResponseEntity.ok(map(coursesForGroup, CourseForGroupDTO.class));
    }

    @PutMapping("/groups/{groupId}/courses")
    @JsonView(CourseForGroupView.Course.class)
    public ResponseEntity updateCourseForGroup(@PathVariable int groupId, @RequestBody CourseForGroupUpdateHolder coursesForGroupHolder) {
        try {
            Course newCourse = (Course) map(coursesForGroupHolder.getNewCourse(), Course.class);
            int oldCourseId = coursesForGroupHolder.getOldCourseId();
            Course courseFromDb = courseService.getCourseByAllAttributes(newCourse);
            StudentGroup group =  studentGroupService.getById(groupId);
            if (courseForGroupService.countByGroup(group)==1){
                CourseName courseName = (CourseName) map(coursesForGroupHolder.getNewCourse().getCourseName(), CourseName.class);
                newCourse = updateCourseName(courseName, newCourse);
                courseService.createOrUpdateCourse(newCourse);
                return new ResponseEntity(HttpStatus.CREATED);
            }
            CourseForGroup courseForGroup = courseForGroupService.getCourseForGroup(coursesForGroupHolder.getCourseForGroupId());
            if (courseFromDb != null) {
                newCourse = courseFromDb;
            }
            else {
                CourseName courseName = (CourseName) map(coursesForGroupHolder.getNewCourse().getCourseName(), CourseName.class);
                newCourse = updateCourseName(courseName, newCourse);
                newCourse = courseService.createOrUpdateCourse(newCourse);
            }
            courseForGroup.setCourse(newCourse);
            courseForGroupService.save(courseForGroup);
            List<Grade> grades = gradeService.getGradesByCourseAndGroup(oldCourseId, groupId);
            gradeService.saveGradesByCourse(newCourse, grades);
            return new ResponseEntity(HttpStatus.CREATED);
        } catch (Exception e) {
            return ExceptionHandlerAdvice.handleException("Backend error", CourseController.class);
        }
    }

    private Course updateCourseName(CourseName courseName, Course newCourse){
        CourseName courseNameFromDB = courseNameService.getCourseNameByName(courseName.getName());
        if (courseNameFromDB != null){
            newCourse.setCourseName(courseNameFromDB);
        }
        else {
            CourseName newCourseName = new CourseName();
            newCourseName.setName(courseName.getName());
            newCourse.setCourseName(courseNameService.saveCourseName(newCourseName));
        }
        return newCourse;
    }


    @PostMapping("/groups/{groupId}/courses")
    public ResponseEntity addCoursesForGroup(@RequestBody CoursesForGroupHolder coursesForGroupHolder, @PathVariable Integer groupId) {
        List<CourseForGroupDTO> newCourses = coursesForGroupHolder.getNewCourses();
        List<CourseForGroupDTO> updatedCourses = coursesForGroupHolder.getUpdatedCourses();
        List<Integer> deleteCoursesIds = coursesForGroupHolder.getDeleteCoursesIds();

        if (newCourses == null || updatedCourses == null || deleteCoursesIds == null) {
            return ExceptionHandlerAdvice.handleException("Courses must not be null", CourseController.class);
        }

        try {
            Set<CourseForGroup> newCoursesForGroup = new HashSet<>();
            Set<CourseForGroup> updatedCoursesForGroup = new HashSet<>();

            for (CourseForGroupDTO newCourseForGroup : newCourses) {
                CourseForGroup courseForGroup = new CourseForGroup();

                Course course = courseService.getById(newCourseForGroup.getCourse().getId());
                courseForGroup.setCourse(course);

                StudentGroup studentGroup = studentGroupService.getById(groupId);
                courseForGroup.setStudentGroup(studentGroup);

                Teacher teacher = teacherService.getTeacher(newCourseForGroup.getTeacher().getId());
                courseForGroup.setTeacher(teacher);

                courseForGroup.setExamDate(newCourseForGroup.getExamDate());

                newCoursesForGroup.add(courseForGroup);
            }

            for (CourseForGroupDTO updatedCourseForGroup : updatedCourses) {
                CourseForGroup courseForGroup = courseForGroupService.getCourseForGroup(updatedCourseForGroup.getId());

                Teacher teacher = teacherService.getTeacher(updatedCourseForGroup.getTeacher().getId());
                courseForGroup.setTeacher(teacher);

                courseForGroup.setExamDate(updatedCourseForGroup.getExamDate());

                updatedCoursesForGroup.add(courseForGroup);
            }

            courseForGroupService.addCourseForGroupAndNewChanges(newCoursesForGroup, updatedCoursesForGroup, deleteCoursesIds);
            return ResponseEntity.ok().build();

        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @GetMapping("/groups/{groupId}/courses/all")
    @JsonView(CourseForGroupView.Course.class)
    public ResponseEntity getCourses(@PathVariable int groupId) {
        List<CourseForGroup> courseForGroups = courseForGroupService.getCoursesForOneGroup(groupId);
        return ResponseEntity.ok(map(courseForGroups, CourseForGroupDTO.class));
    }

    @GetMapping("/specialization/{id}/courses")
    @JsonView(CourseForGroupView.Basic.class)
    public ResponseEntity getCoursesBySpecialization(@PathVariable int id, @RequestParam("semester") int semester) {
        List<CourseForGroup> courseForGroups = courseForGroupService.getCourseForGroupBySpecialization(id, semester);
        return ResponseEntity.ok(map(courseForGroups, CourseForGroupDTO.class));
    }

    @PostMapping("/courses")
    public ResponseEntity createCourse(@RequestBody CourseDTO courseDTO) {
        try {
            Course course = (Course) map(courseDTO, Course.class);
            if (courseDTO.getCourseName().getId() != 0) {
                Course newCourse = this.courseService.createOrUpdateCourse(course);
                URI location = getNewResourceLocation(newCourse.getId());
                return ResponseEntity.created(location).build();
            } else {
                CourseName courseName = new CourseName();
                courseName.setName(courseDTO.getCourseName().getName());
                this.courseNameService.saveCourseName(courseName);
                CourseName newCourseName = this.courseNameService.getCourseNameByName(courseName.getName());
                course.setCourseName(newCourseName);
                this.courseService.createOrUpdateCourse(course);
                URI location = getNewResourceLocation(course.getId());
                return ResponseEntity.created(location).build();
            }
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @GetMapping("courses/names")
    public ResponseEntity getCourseNames() {
        List<CourseName> courseNames = this.courseNameService.getCourseNames();
        return ResponseEntity.ok(map(courseNames, NamedDTO.class));
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, CourseController.class);
    }
}
