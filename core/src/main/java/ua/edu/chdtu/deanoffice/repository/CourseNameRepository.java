package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.CourseName;

import java.util.List;

public interface CourseNameRepository extends JpaRepository<CourseName, Integer> {
    @Query
    List<CourseName> findCourseNameById(@Param("courseId") int courseId);

}
