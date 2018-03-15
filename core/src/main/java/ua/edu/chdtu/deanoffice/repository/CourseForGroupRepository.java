package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;

import java.util.List;

/**
 * Created by os199 on 06.11.2017.
 */
public interface CourseForGroupRepository extends JpaRepository<CourseForGroup, Integer> {

    //TODO аліас "courseForGroup" краще скоротити до cfg, та знову метод, який не використувається
    @Query("select courseForGroup.course from CourseForGroup as courseForGroup " +
            "join courseForGroup.studentGroup studentGroup join studentGroup.specialization specialization " +
            "where studentGroup.active = true and specialization.id = :specializationId")
    List<CourseForGroup> findAllBySpecialization(@Param("specializationId") int specId);

    List<CourseForGroup> findAllByStudentGroupId(@Param("groupId") int groupId);

    List<CourseForGroup> findAllByStudentGroupIdAndCourse_Semester(@Param("groupId") int groupId, @Param("semester") int semester);

    CourseForGroup findByStudentGroupIdAndCourseId(@Param("studentGroupId") int groupId, @Param("courseId") int courseId);

}
