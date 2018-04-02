package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.Course;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    @Query
    List<Course> findCourseById(@Param("courseId") int courseId);

    @Query("select course from CourseForGroup courseForGroup " +
            "join courseForGroup.course course " +
            "where courseForGroup.studentGroup.id=:groupId")
    List<Course> getByGroupId(@Param("groupId") Integer groupId);

    @Query("select course from Course as course " +
            "where course.semester = :semester order by course.courseName.name, course.knowledgeControl.name, course.hours")
    List<Course> findAllBySemester(@Param("semester") int semester);
}