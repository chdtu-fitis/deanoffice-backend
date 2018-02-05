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
            "join cfg.studentGroup sg join sg.specialization spec " +
            "where sg.active = 'T' and spec.id = :specId")
    //TODO cr: не треба все скорочувати - тут краще явно написати true або 1
    List<CourseForGroup> findAllBySpecialization(@Param("specId") int specId);

    List<CourseForGroup> findAllByStudentGroupId(@Param("groupId") int groupId);
    List<CourseForGroup> findAllByStudentGroupIdAndCourse_Semester(@Param("groupId") int groupId,@Param("semester") int semester);

}
