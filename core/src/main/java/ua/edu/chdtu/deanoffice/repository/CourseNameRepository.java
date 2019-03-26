package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.chdtu.deanoffice.entity.CourseName;

import java.util.List;

public interface CourseNameRepository extends JpaRepository<CourseName, Integer> {
    CourseName findByName(String name);

    @Query("select cn from CourseName as cn " +
            "where cn.id = :id")
    CourseName findCourseNameById(@Param("id") int id);

    @Query("select cn from CourseName as cn " +
            "where cn.id not in(select distinct c.courseName.id from Course as c)")
    List<CourseName> findUnusedCoursesNames();

    @Transactional
    void deleteCourseNameByIdIn(List<Integer> ids);

    @Transactional
    void deleteCourseNameById(int id);

}
