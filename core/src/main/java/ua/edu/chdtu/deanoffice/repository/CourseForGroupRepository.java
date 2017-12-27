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
//
//    @Query("select cfg.course from CourseForGroup as cfg " +
//            "join cfg.studentGroup sg join sg.specialization spec " +
//            "where sg.active = true and spec.id = :specId")
//    List<CourseForGroup> findAllBySpecialization(@Param("specId") int specId);

    @Query("select cfg from CourseForGroup as cfg where cfg.studentGroup.id = :groupId and cfg.studentGroup.active = false ")//Тут треба true, але оскільки в базі тільки не активні групи тому false покищо
    List<CourseForGroup> findAllByStudentGroup(@Param("groupId") int groupId);

}
