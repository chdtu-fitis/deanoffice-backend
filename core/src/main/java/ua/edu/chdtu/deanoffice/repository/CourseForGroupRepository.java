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

    @Query("select courseForGroup.course from CourseForGroup as courseForGroup " +
            "join courseForGroup.studentGroup studentGroup join studentGroup.specialization specialization " +
            "where studentGroup.active = true and specialization.id = :specializationId and courseForGroup.course.semester = :semester")
    List<CourseForGroup> findAllBySpecialization(@Param("specializationId") int specId, @Param("semester") int semester);


    @Query("select courseForGroup.course from CourseForGroup as courseForGroup " +
            "where courseForGroup.course.semester = :semester order by courseForGroup.course.courseName.name desc, courseForGroup.course.knowledgeControl.name desc, courseForGroup.course.hours")
    List<CourseForGroup> findAllBySemester(@Param("semester") int semester);
    List<CourseForGroup> findAllByStudentGroupId(@Param("groupId") int groupId);
    List<CourseForGroup> findAllByStudentGroupIdAndCourse_Semester(@Param("groupId") int groupId,@Param("Course_Semester") int semester);

}
