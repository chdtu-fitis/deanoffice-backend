package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;

import java.util.List;

public interface CourseForGroupRepository extends JpaRepository<CourseForGroup, Integer> {

    @Query("select cfg.course from CourseForGroup as cfg " +
            "join cfg.studentGroup studentGroup join studentGroup.specialization specialization " +
            "where studentGroup.active = true and specialization.id = :specializationId and cfg.course.semester = :semester")
    List<CourseForGroup> findAllBySpecialization(@Param("specializationId") int specId, @Param("semester") int semester);

    @Query("select cfg.course from CourseForGroup as cfg " +
            "where cfg.course.semester = :semester " +
            "order by cfg.course.courseName.name desc, cfg.course.knowledgeControl.name desc, cfg.course.hours")
    List<CourseForGroup> findAllBySemester(@Param("semester") int semester);

    List<CourseForGroup> findAllByStudentGroupId(@Param("groupId") int groupId);

    @Query("select cfg from CourseForGroup as cfg " +
            "where cfg.course.semester = :semester and cfg.studentGroup.id=:groupId " +
            "order by cfg.course.knowledgeControl.id, cfg.course.courseName.name")
    List<CourseForGroup> findAllByStudentGroupIdAndCourseSemester(@Param("groupId") int groupId, @Param("semester") int semester);

    CourseForGroup findByStudentGroupIdAndCourseId(@Param("studentGroupId") int groupId, @Param("courseId") int courseId);

    int countByCourseId(Integer courseId);

    @Query("SELECT cfg FROM CourseForGroup AS cfg " +
            "WHERE cfg.id IN (:ids)")
    List<CourseForGroup> findByIds(@Param("ids") int[] ids);

    @Query( value =
            "SELECT (count(*) > 0) AS result " +
            "  FROM courses_for_groups cfg " +
            "       INNER JOIN student_degree sd ON sd.student_group_id = cfg.student_group_id " +
            "       INNER JOIN grade g on sd.id = g.student_degree_id " +
            " WHERE cfg.id = :courseForGroupId", nativeQuery = true)
    boolean areGradesFor(@Param("courseForGroupId") int courseForGroupId);
}
