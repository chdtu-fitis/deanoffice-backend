package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;

import java.math.BigDecimal;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    @Query("select course from CourseForGroup courseForGroup " +
            "join courseForGroup.course course " +
            "where courseForGroup.studentGroup.id=:groupId " +
            "order by course.semester, course.knowledgeControl.name, course.courseName.name")
    List<Course> getByGroupId(@Param("groupId") Integer groupId);

    @Query("select course from CourseForGroup courseForGroup " +
            "join courseForGroup.course course " +
            "where courseForGroup.studentGroup.id=:groupId " +
            "and course.semester = :semester")
    List<Course> getByGroupIdAndSemester(@Param("groupId") Integer groupId,
                                         @Param("semester") Integer semester);

    @Query("select course from Course as course " +
            "where course.semester = :semester " +
            "order by course.courseName.name, course.knowledgeControl.name, course.hours")
    List<Course> findAllBySemester(@Param("semester") int semester);

    @Query("select course from Course as course " +
            "where course.semester = :semester " +
            "and course.hoursPerCredit = :hoursPerCredits " +
            "order by course.courseName.name, course.knowledgeControl.name, course.hours")
    List<Course> findAllBySemesterAndHoursPerCredit(
            @Param("semester") int semester,
            @Param("hoursPerCredits") int hoursPerCredit
    );

    @Query("select c from Course as c where " +
            "c.semester = :semester and c.knowledgeControl.id = :kc_id " +
            "and c.courseName.id = :course_name_id and c.hours = :hours " +
            "and c.hoursPerCredit = :hours_per_credit")
    Course findOne(@Param("semester") int semester, @Param("kc_id") int kcId,
                   @Param("course_name_id") int courseNameId,
                   @Param("hours") int hours,
                   @Param("hours_per_credit") int hoursPerCredit);

    long count(Specification<Course> specification);

    PageImpl findAll(Specification<Course> specification, Pageable pageable);

    @Query("select course from Course as course " +
            "order by course.semester, course.knowledgeControl.name, course.courseName.name")
    List<Course> findAllCourses(Pageable pageable);

    @Query("select count(course) from Course as course ")
    int findTotalOfAllCourses();

    @Query("select c from Course as c " +
            "where c.id not in" +
            "(select distinct cfg.course.id from CourseForGroup as cfg) " +
            "and c.id not in (select distinct g.course.id from Grade as g) " +
            "order by c.semester, c.knowledgeControl.name, c.courseName.name")
    List<Course> findUnusedCourses(Pageable pageable);

    @Query("select count(c) from Course as c " +
            "where c.id not in(select distinct cfg.course.id from CourseForGroup as cfg) " +
            "and c.id not in (select distinct g.course.id from Grade as g)")
    int findTotalOfUnusedCourses();

    @Query("update Course as c set c.courseName.id = : correctId " +
            "where c.courseName.id = :wrongId")
    List<Course> updateCourseNameIdInCourse(@Param("correctId") int correctId, @Param("wrongId") int wrongId);

    @Transactional
    void deleteByIdIn(List<Integer> ids);
}
