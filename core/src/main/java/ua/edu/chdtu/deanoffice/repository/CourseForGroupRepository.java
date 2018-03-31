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

    @Query("select cfg.course from CourseForGroup as cfg " +
            "join cfg.studentGroup studentGroup join studentGroup.specialization specialization " +
            "where studentGroup.active = true and specialization.id = :specializationId and cfg.course.semester = :semester")
    List<CourseForGroup> findAllBySpecialization(@Param("specializationId") int specId, @Param("semester") int semester);

    @Query("select cfg.course from CourseForGroup as cfg " +
            "where cfg.course.semester = :semester order by cfg.course.courseName.name desc, cfg.course.knowledgeControl.name desc, cfg.course.hours")
    List<CourseForGroup> findAllBySemester(@Param("semester") int semester);
    List<CourseForGroup> findAllByStudentGroupId(@Param("groupId") int groupId);

    List<CourseForGroup> findAllByStudentGroupIdAndCourse_Semester(@Param("groupId") int groupId, @Param("semester") int semester);

    CourseForGroup findByStudentGroupIdAndCourseId(@Param("studentGroupId") int groupId, @Param("courseId") int courseId);

}
