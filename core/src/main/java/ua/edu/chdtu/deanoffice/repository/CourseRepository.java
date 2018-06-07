package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.Course;

import java.math.BigDecimal;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    @Query("select course from CourseForGroup courseForGroup " +
            "join courseForGroup.course course " +
            "where courseForGroup.studentGroup.id=:groupId")
    List<Course> getByGroupId(@Param("groupId") Integer groupId);

    @Query("select course from CourseForGroup courseForGroup " +
            "join courseForGroup.course course " +
            "where courseForGroup.studentGroup.id=:groupId " +
            "and course.semester = :semester")
    List<Course> getByGroupIdAndSemester(@Param("groupId") Integer groupId,
                                         @Param("semester") Integer semester);

    @Query("select course from Course as course " +
            "where course.semester = :semester order by course.courseName.name, course.knowledgeControl.name, course.hours")
    List<Course> findAllBySemester(@Param("semester") int semester);

    @Query("select c from Course as c where " +
            "c.semester = :semester and c.knowledgeControl.id = :kc_id " +
            "and c.courseName.id = :course_name_id and c.hours = :hours " +
            "and c.credits = :credits and c.hoursPerCredit = :hours_per_credit")
    Course findOne(@Param("semester") int semester, @Param("kc_id") int kcId,
                   @Param("course_name_id") int courseNameId,
                   @Param("hours") int hours, @Param("credits") BigDecimal credits,
                   @Param("hours_per_credit") int hoursPerCredit);
}