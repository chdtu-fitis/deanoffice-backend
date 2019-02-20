package ua.edu.chdtu.deanoffice.api.course;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.course.dto.CourseDTO;
import ua.edu.chdtu.deanoffice.api.course.dto.CourseForGroupDTO;
import ua.edu.chdtu.deanoffice.api.course.dto.CourseForGroupView;
import ua.edu.chdtu.deanoffice.api.course.util.CourseForGroupUpdateHolder;
import ua.edu.chdtu.deanoffice.api.course.util.CoursesForGroupHolder;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.api.general.dto.NamedDTO;
import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.service.*;

import java.math.BigDecimal;
import java.util.*;

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
        try {
            List<Course> courses = courseService.getCoursesBySemester(semester);
            return ResponseEntity.ok(map(courses, CourseDTO.class));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/groups/{groupId}/courses")
    @JsonView(CourseForGroupView.Course.class)
    public ResponseEntity getCoursesByGroupAndSemester(@PathVariable int groupId, @RequestParam int semester) {
        try {
            List<CourseForGroup> coursesForGroup = courseForGroupService.getCoursesForGroupBySemester(groupId, semester);
            return ResponseEntity.ok(map(coursesForGroup, CourseForGroupDTO.class));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/courses/other-semester")
    public ResponseEntity getCourseForGroupsFromOtherGroupAndSemester(
            @RequestParam int semester,
            @RequestParam int[] courseForGroupIds) {
        try {
            // Отримуємо всі курси для груп, що були передані для обробки
            List<CourseForGroup> inCoursesForGroups = courseForGroupService.getCoursesForGroups(courseForGroupIds);
            // Шукаємо за всіма параметрами той же курс за потрібний семестр, якщо
            // знаходимо, то додаємо їх до списку existCoursesAndTeacherForSemester.
            Map<Course, Teacher> existCoursesAndTeacherForSemester = new HashMap<>();
            Map<Course, Teacher> mustBeCreatedCoursesAndTeacher = new HashMap<>();

            inCoursesForGroups.forEach(item -> {
                Course courseFromCourseForGroup = item.getCourse();
                Course course = createCourse(semester,
                        courseFromCourseForGroup.getCourseName(),
                        courseFromCourseForGroup.getHours(),
                        courseFromCourseForGroup.getHoursPerCredit(),
                        courseFromCourseForGroup.getKnowledgeControl());
                Course foundInDBCourse = courseService.getCourseByAllAttributes(course);

                if (foundInDBCourse == null) {
                    mustBeCreatedCoursesAndTeacher.put(courseFromCourseForGroup, item.getTeacher());
                } else {
                    existCoursesAndTeacherForSemester.put(foundInDBCourse, item.getTeacher());
                }
            });
            List<CourseForGroup> createdCourseForGroups = new ArrayList<>(mustBeCreatedCoursesAndTeacher.size() + existCoursesAndTeacherForSemester.size());
            // Для всіх курсів, які не були знайдені виконуємо їх створення та збереження до бази даних
            mustBeCreatedCoursesAndTeacher.forEach((course, teacher) -> {
                Course tempCourse = createCourse(semester, course.getCourseName(), course.getHours(), course.getHoursPerCredit(), course.getKnowledgeControl());
                Course createdCourse = courseService.createOrUpdateCourse(tempCourse);
                if (createdCourse != null) {
                    createdCourseForGroups.add(createCourseForGroup(createdCourse, null, null, teacher));
                }
            });
            // Для існуючих курсів виконуємо створення CourseForGroup в якого ми встановлюємо викладача на того, який був переданий
            existCoursesAndTeacherForSemester.forEach((course, teacher) -> {
                createdCourseForGroups.add(createCourseForGroup(course, null, null, teacher));
            });
            return ResponseEntity.ok(map(createdCourseForGroups, CourseForGroupDTO.class));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private CourseForGroup createCourseForGroup(Course course, StudentGroup studentGroup, Date date, Teacher teacher) {
        CourseForGroup courseForGroup = new CourseForGroup();
        courseForGroup.setCourse(course);
        courseForGroup.setStudentGroup(studentGroup);
        courseForGroup.setExamDate(date);
        courseForGroup.setTeacher(teacher);
        return courseForGroup;
    }

    private Course createCourse(Integer semester, CourseName courseName, Integer hours, Integer hoursPerCredit, KnowledgeControl knowledgeControl) {
        Course course = new Course();
        course.setSemester(semester);
        course.setCourseName(courseName);
        if (hoursPerCredit.equals(0) || hours.equals(0)) {
            course.setCredits(BigDecimal.ZERO);
        } else {
            course.setCredits(BigDecimal.valueOf(hours).divide(BigDecimal.valueOf(hoursPerCredit), 2, BigDecimal.ROUND_HALF_UP));
        }
        course.setHours(hours);
        course.setHoursPerCredit(hoursPerCredit);
        course.setKnowledgeControl(knowledgeControl);
        return course;
    }

    @PutMapping("/groups/{groupId}/courses")
    @JsonView(CourseForGroupView.Course.class)
    public ResponseEntity updateCourseForGroup(@PathVariable int groupId, @RequestBody CourseForGroupUpdateHolder coursesForGroupHolder) {
        try {
            Course newCourse = (Course) map(coursesForGroupHolder.getNewCourse(), Course.class);
            int oldCourseId = coursesForGroupHolder.getOldCourseId();
            Course courseFromDb = courseService.getCourseByAllAttributes(newCourse);
            if (courseFromDb != null) {
                newCourse = courseFromDb;
                CourseForGroup courseForGroup = courseForGroupService.getCourseForGroup(coursesForGroupHolder.getCourseForGroupId());
                updateCourseInCoursesForGroupsAndGrade(courseForGroup, courseFromDb, oldCourseId, groupId);
            } else {
                CourseName courseName = (CourseName) map(coursesForGroupHolder.getNewCourse().getCourseName(), CourseName.class);
                newCourse = updateCourseName(courseName, newCourse);
                if (courseForGroupService.hasSoleCourse(oldCourseId)){
                    courseService.createOrUpdateCourse(newCourse);
                } else {
                    newCourse.setId(0);
                    newCourse = courseService.createOrUpdateCourse(newCourse);
                    CourseForGroup courseForGroup = courseForGroupService.getCourseForGroup(coursesForGroupHolder.getCourseForGroupId());
                    updateCourseInCoursesForGroupsAndGrade(courseForGroup, newCourse, oldCourseId, groupId);
                }
            }
            return ResponseEntity.ok(map(newCourse, CourseDTO.class));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private Course updateCourseName(CourseName courseName, Course newCourse){
        CourseName courseNameFromDB = courseNameService.getCourseNameByName(courseName.getName());
        if (courseNameFromDB != null){
            newCourse.setCourseName(courseNameFromDB);
        } else {
            CourseName newCourseName = new CourseName();
            newCourseName.setName(courseName.getName());
            newCourse.setCourseName(courseNameService.saveCourseName(newCourseName));
        }
        return newCourse;
    }

    private void updateCourseInCoursesForGroupsAndGrade(CourseForGroup courseForGroup, Course newCourse, int oldCourseId, int groupId) {
        courseForGroup.setCourse(newCourse);
        courseForGroupService.save(courseForGroup);
        List<Grade> grades = gradeService.getGradesByCourseAndGroup(oldCourseId, groupId);
        gradeService.saveGradesByCourse(newCourse, grades);
    }

    @PostMapping("/groups/{groupId}/courses")
    public ResponseEntity addCoursesForGroup(@RequestBody CoursesForGroupHolder coursesForGroupHolder, @PathVariable Integer groupId) {
        try {
            courseForGroupService.validateDeleteCourseForGroups(coursesForGroupHolder.getDeleteCoursesIds());

            List<CourseForGroupDTO> newCourses = coursesForGroupHolder.getNewCourses();
            List<CourseForGroupDTO> updatedCourses = coursesForGroupHolder.getUpdatedCourses();
            List<Integer> deleteCoursesIds = coursesForGroupHolder.getDeleteCoursesIds();

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
        try {
            List<CourseForGroup> courseForGroups = courseForGroupService.getCoursesForOneGroup(groupId);
            return ResponseEntity.ok(map(courseForGroups, CourseForGroupDTO.class));
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @GetMapping("/specialization/{id}/courses")
    @JsonView(CourseForGroupView.Basic.class)
    public ResponseEntity getCoursesBySpecialization(@PathVariable int id, @RequestParam("semester") int semester) {
        try {
            List<CourseForGroup> courseForGroups = courseForGroupService.getCourseForGroupBySpecialization(id, semester);
            return ResponseEntity.ok(map(courseForGroups, CourseForGroupDTO.class));
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @PostMapping("/courses")
    public ResponseEntity createCourse(@RequestBody CourseDTO courseDTO) {
        try {
            Course course = (Course) map(courseDTO, Course.class);
            if (courseDTO.getCourseName().getId() != 0) {
                Course newCourse = this.courseService.createOrUpdateCourse(course);
                return ResponseEntity.ok(newCourse);
            } else {
                CourseName courseName = new CourseName();
                courseName.setName(courseDTO.getCourseName().getName());
                this.courseNameService.saveCourseName(courseName);
                CourseName newCourseName = this.courseNameService.getCourseNameByName(courseName.getName());
                course.setCourseName(newCourseName);
                Course newCourse = this.courseService.createOrUpdateCourse(course);
                return ResponseEntity.ok(newCourse);
            }
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @GetMapping("courses/names")
    public ResponseEntity getCourseNames() {
        try {
            List<CourseName> courseNames = this.courseNameService.getCourseNames();
            return ResponseEntity.ok(map(courseNames, NamedDTO.class));
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, CourseController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
