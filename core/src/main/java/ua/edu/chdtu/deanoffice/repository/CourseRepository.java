package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.domain.Pageable;
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

    //
    @Query("select course from Course as course " +
            "order by course.semester, course.knowledgeControl.name, course.courseName.name")
    List<Course> findAllCourses();

    @Query("select course from Course as course " +
            "where course.hours = :hours " +
            "order by course.semester, course.knowledgeControl.name, course.courseName.name")
    List<Course> findCoursesByHours(@Param("hours") int hours);

    @Query("select course from Course as course " +
            "where course.hoursPerCredit = :hours " +
            "order by course.semester, course.knowledgeControl.name, course.courseName.name")
    List<Course> findCoursesByHoursPerCredit(@Param("hours") int hours);

    @Query("select course from Course as course " +
            "where course.knowledgeControl.name = :knowledgeControl " +
            "order by course.semester, course.knowledgeControl.name, course.courseName.name")
    List<Course> findCoursesByKnowledgeControl(@Param("knowledgeControl") String knowledgeControl);

    @Query("select course from Course as course " +
            "where course.courseName.name like :startingWith% " +
            "order by course.semester, course.knowledgeControl.name, course.courseName.name")
    List<Course> findCoursesByCourseNameStartingWith(@Param("startingWith") String startingWith);

    @Query(value = "select course from Course as course " +
            "where course.courseName.name like %:text% " +
            "order by course.semester, course.knowledgeControl.name, course.courseName.name")
    List<Course> findCoursesByCourseNameContaining(@Param("text") String text);
    //

//    long countAll (Specification<Course> specification);

    List<Course> findAll(Specification<Course> specification, Pageable pageable);

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

    @Transactional
    void deleteByIdIn(List<Integer> ids);
}
