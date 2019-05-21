package ua.edu.chdtu.deanoffice.api.course;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.course.dto.CourseDTO;
import ua.edu.chdtu.deanoffice.api.course.dto.CourseForGroupDTO;
import ua.edu.chdtu.deanoffice.api.course.dto.CourseForGroupView;
import ua.edu.chdtu.deanoffice.api.course.dto.CoursePaginationDTO;
import ua.edu.chdtu.deanoffice.api.course.util.CourseForGroupUpdateHolder;
import ua.edu.chdtu.deanoffice.api.course.util.CoursesForGroupHolder;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.api.general.dto.NamedDTO;
import ua.edu.chdtu.deanoffice.api.general.mapper.Mapper;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.entity.CourseName;
import ua.edu.chdtu.deanoffice.entity.Grade;
import ua.edu.chdtu.deanoffice.entity.KnowledgeControl;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.entity.Teacher;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.service.*;
import ua.edu.chdtu.deanoffice.service.course.CoursePaginationBean;
import ua.edu.chdtu.deanoffice.service.course.CourseService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;

@RestController
public class CourseController {
    private CourseForGroupService courseForGroupService;
    private CourseService courseService;
    private StudentGroupService studentGroupService;
    private TeacherService teacherService;
    private GradeService gradeService;
    private CourseNameService courseNameService;
    private KnowledgeControlService knowledgeControlService;

    @Autowired
    public CourseController(
            CourseForGroupService courseForGroupService,
            CourseService courseService,
            StudentGroupService studentGroupService,
            TeacherService teacherService,
            CourseNameService courseNameService,
            GradeService gradeService,
            KnowledgeControlService knowledgeControlService
    ) {
        this.courseForGroupService = courseForGroupService;
        this.courseService = courseService;
        this.studentGroupService = studentGroupService;
        this.teacherService = teacherService;
        this.courseNameService = courseNameService;
        this.gradeService = gradeService;
        this.knowledgeControlService = knowledgeControlService;
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

    @GetMapping("/courses/hours-per-credit")
    public ResponseEntity getCoursesBySemesterAndHoursPerCredit(@RequestParam(value = "semester") int semester,
                                                                @RequestParam(value = "hoursPerCredit") int hoursPerCredit) {
        try {
            List<Course> courses = courseService.getCoursesBySemesterAndHoursPerCredit(semester, hoursPerCredit);
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
            Course newCourse = map(coursesForGroupHolder.getNewCourse(), Course.class);
            int oldCourseId = coursesForGroupHolder.getOldCourseId();
            Course oldCourse = courseService.getById(oldCourseId);
            Course courseFromDb = courseService.getCourseByAllAttributes(newCourse);
            if (courseFromDb != null) {
                newCourse = courseFromDb;
                double correctCredits = Math.abs((0.0 + courseFromDb.getHours()) / courseFromDb.getHoursPerCredit());
                if (Math.abs(correctCredits - courseFromDb.getCredits().doubleValue()) > 0.005) {
                    courseFromDb.setCredits(new BigDecimal(correctCredits));
                    courseService.createOrUpdateCourse(courseFromDb);
                }
                CourseForGroup courseForGroup = courseForGroupService.getCourseForGroup(coursesForGroupHolder.getCourseForGroupId());
                updateCourseInCoursesForGroupsAndGrade(courseForGroup, courseFromDb, oldCourseId, groupId, oldCourse.getKnowledgeControl().getId());
            } else {
                CourseName courseName = map(coursesForGroupHolder.getNewCourse().getCourseName(), CourseName.class);
                newCourse = updateCourseName(courseName, newCourse);
                if (courseForGroupService.hasSoleCourse(oldCourseId)) {
                    int oldKnowledgeControlId = oldCourse.getKnowledgeControl().getId();
                    int newKnowledgeControlId = newCourse.getKnowledgeControl().getId();
                    courseService.createOrUpdateCourse(newCourse);
                    adjustNationalGrade(oldKnowledgeControlId, newKnowledgeControlId, newCourse.getId());
                } else {
                    newCourse.setId(0);
                    newCourse = courseService.createOrUpdateCourse(newCourse);
                    CourseForGroup courseForGroup = courseForGroupService.getCourseForGroup(coursesForGroupHolder.getCourseForGroupId());
                    updateCourseInCoursesForGroupsAndGrade(courseForGroup, newCourse, oldCourseId, groupId, oldCourse.getKnowledgeControl().getId());
                }
            }
            return ResponseEntity.ok(map(newCourse, CourseDTO.class));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private void adjustNationalGrade(int oldKnowledgeControlId, int newKnowledgeControlId, int newCourseId) {
        Map<String, Boolean> gradeDefinition = gradeService.evaluateGradedChange(oldKnowledgeControlId, newKnowledgeControlId);
        if (gradeDefinition.get(GradeService.NEW_GRADED_VALUE) != null) {
            if (gradeDefinition.get(GradeService.NEW_GRADED_VALUE)) {
                gradeService.updateNationalGradeByCourseIdAndGradedTrue(newCourseId);
            } else {
                gradeService.updateNationalGradeByCourseIdAndGradedFalse(newCourseId);
            }
        }
    }

    private Course updateCourseName(CourseName courseName, Course newCourse) {
        CourseName courseNameFromDB = courseNameService.getCourseNameByName(courseName.getName());
        if (courseNameFromDB != null) {
            newCourse.setCourseName(courseNameFromDB);
        } else {
            CourseName newCourseName = new CourseName();
            newCourseName.setName(courseName.getName());
            newCourse.setCourseName(courseNameService.saveCourseName(newCourseName));
        }
        return newCourse;
    }

    private void updateCourseInCoursesForGroupsAndGrade(CourseForGroup courseForGroup, Course newCourse, int oldCourseId, int groupId, int oldKnowledgeControlId) {
        courseForGroup.setCourse(newCourse);
        courseForGroupService.save(courseForGroup);
        List<Grade> grades = gradeService.getGradesByCourseAndGroup(oldCourseId, groupId);
        Map<String, Boolean> gradedChange = gradeService.evaluateGradedChange(oldKnowledgeControlId, newCourse.getKnowledgeControl().getId());
        gradeService.saveGradesByCourse(newCourse, grades, gradedChange);
    }

    @PostMapping("/groups/{groupId}/courses")
    public ResponseEntity addCoursesForGroup(@RequestBody CoursesForGroupHolder coursesForGroupHolder, @PathVariable Integer groupId) {
        try {
            courseForGroupService.validateDeleteCourseForGroups(coursesForGroupHolder.getDeleteCoursesIds());

            List<CourseForGroupDTO> newCourses = coursesForGroupHolder.getNewCourses();
            List<CourseForGroupDTO> updatedCourses = coursesForGroupHolder.getUpdatedCourses();
            List<Integer> deleteCoursesIds = coursesForGroupHolder.getDeleteCoursesIds();

            Set<CourseForGroup> newCoursesForGroup = new HashSet<>();
            Set<CourseForGroup> courseForGroupWithNewAcademicDifference = new HashSet<>();
            Set<CourseForGroup> courseForGroupWithOldAcademicDifference = new HashSet<>();
            //Set<CourseForGroup> updatedCoursesForGroup = new HashSet<>();
            Map<Boolean, Set<CourseForGroup>> updatedCoursesForGroup = new HashMap<>();
            for (CourseForGroupDTO newCourseForGroup : newCourses) {
                CourseForGroup courseForGroup = new CourseForGroup();
                Course course = courseService.getById(newCourseForGroup.getCourse().getId());
                courseForGroup.setCourse(course);
                StudentGroup studentGroup = studentGroupService.getById(groupId);
                courseForGroup.setStudentGroup(studentGroup);
                if (newCourseForGroup.getTeacher().getId() != 0) {
                    Teacher teacher = teacherService.getTeacher(newCourseForGroup.getTeacher().getId());
                    courseForGroup.setTeacher(teacher);
                }
                courseForGroup.setExamDate(newCourseForGroup.getExamDate());
                courseForGroup.setAcademicDifference(newCourseForGroup.isAcademicDifference());
                newCoursesForGroup.add(courseForGroup);
            }

            for (CourseForGroupDTO updatedCourseForGroup : updatedCourses) {
                CourseForGroup courseForGroup = courseForGroupService.getCourseForGroup(updatedCourseForGroup.getId());
                if (updatedCourseForGroup.getTeacher().getId() != 0) {
                    Teacher teacher = teacherService.getTeacher(updatedCourseForGroup.getTeacher().getId());
                    courseForGroup.setTeacher(teacher);
                }
                courseForGroup.setExamDate(updatedCourseForGroup.getExamDate());
                boolean academicDifference = courseForGroup.isAcademicDifference();
                courseForGroup.setAcademicDifference(updatedCourseForGroup.isAcademicDifference());
                if (academicDifference != updatedCourseForGroup.isAcademicDifference()) {
                    courseForGroupWithNewAcademicDifference.add(courseForGroup);
                    //gradeService.setAcademicDifferenceByCoueseId(updatedCourseForGroup.isAcademicDifference(), updatedCourseForGroup.getCourse().getId());
                } else {
                    courseForGroupWithOldAcademicDifference.add(courseForGroup);
                }
//                updatedCoursesForGroup.put(academicDifference,courseForGroup);
            }
            updatedCoursesForGroup.put(true, courseForGroupWithNewAcademicDifference);
            updatedCoursesForGroup.put(false, courseForGroupWithOldAcademicDifference);
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

    @GetMapping("/filtered-courses")
    public ResponseEntity getFilteredCourses(@RequestParam(required = false, name = "page", defaultValue = "1") Integer page,
                                             @RequestParam(required = false, name = "courseName") String courseName,
                                             @RequestParam(required = false, name = "hours") Integer hours,
                                             @RequestParam(required = false, name = "hoursPerCredit") Integer hoursPerCredit,
                                             @RequestParam(required = false, name = "knowledgeControl") String knowledgeControl,
                                             @RequestParam(required = false, name = "nameStartingWith") String nameStartingWith,
                                             @RequestParam(required = false, name = "nameContains") String nameContains,
                                             @RequestParam(required = false, name = "semester") Integer semester
    ) {
        try {
            validatePageParameter(page);
            CoursePaginationBean courseByFilters = courseService.getCourseByFilters(
                    page, courseName, hours, hoursPerCredit, knowledgeControl, nameStartingWith, nameContains, semester);
            return ResponseEntity.ok(Mapper.strictMap(courseByFilters, CoursePaginationDTO.class));
        } catch (Exception exception) {
            return handleException(exception);
        }

    }

    @GetMapping("/all-courses")
    public ResponseEntity getAllCourses(@RequestParam(required = false, name = "page", defaultValue = "1") Integer page) {
        try {
            validatePageParameter(page);
            CoursePaginationBean allCourses = courseService.getAllCourses(page);
            return ResponseEntity.ok(Mapper.strictMap(allCourses, CoursePaginationDTO.class));
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @GetMapping("/course/unused")
    public ResponseEntity getUnusedCourses(
            @RequestParam(required = false, name = "page", defaultValue = "1") Integer page) {
        try {
            validatePageParameter(page);
            CoursePaginationBean unusedCourses = courseService.getPaginatedUnusedCourses(page);
            return ResponseEntity.ok(Mapper.strictMap(unusedCourses, CoursePaginationDTO.class));
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @DeleteMapping("/courses")
    public ResponseEntity deleteCoursesByIds(@RequestParam List<Integer> ids) {
        try {
            validateIdsList(ids);
            courseService.deleteCoursesByIds(ids);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @GetMapping("/credits/wrong")
    public ResponseEntity getCoursesWithWrongCredits() {
        try {
            List<Course> coursesWithWrongCredits = courseService.getCoursesWithWrongCredits();
            return ResponseEntity.ok(map(coursesWithWrongCredits, CourseDTO.class));
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @PutMapping("/credits/wrong")
    public ResponseEntity updateCourseCreditsByIds(@RequestParam("ids") List<Integer> ids) {
        try {
            courseService.updateCoursesCreditsByIds(ids);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @PostMapping("/merge")
    public ResponseEntity mergeCoursesByName(@RequestBody Map<Integer, List<Integer>> idToId) {
        try {
            courseService.mergeCourseNamesByIdToId(idToId);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private void validatePageParameter(Integer page) throws OperationCannotBePerformedException {
        if (page == null)
            throw new OperationCannotBePerformedException("Сторінка дорівнє null");
        if (page <= 0)
            throw new OperationCannotBePerformedException("Сторінка меньше або дорівнює нулю");
    }

    private void validateIdsList(List<Integer> ids) throws OperationCannotBePerformedException {
        if (ids == null || ids.isEmpty())
            throw new OperationCannotBePerformedException("Немає жодного предмету для обробки");
        if (ids.contains(null))
            throw new OperationCannotBePerformedException("Серед предметів є null");
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, CourseController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
