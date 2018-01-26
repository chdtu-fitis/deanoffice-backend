package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.Course;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    @Query
    List<Course> findCourseById(@Param("courseId") int courseId);

    @Query("select C from CourseForGroup CG join CG.course C where CG.studentGroup.id=:groupId")
    //TODO cr: прийміть якийсть стандарт як ви аліаси будете іменувати великияи чи маленькими буквами. Це не помилка але повинно бути видно що це одна команда пише а не окремі люди і читати так простіше
    List<Course> getByGroupId(@Param("groupId") Integer groupId);
}