package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;

import java.util.List;

/**
 * Created by os199 on 06.11.2017.
 */
public interface CourseForGroupRepository extends JpaRepository<CourseForGroup, Integer> {
//    @Query("select cfg.course from CourseForGroup as cfg " +
//            "inner join ")
//    List<CourseForGroup> findAllByGroup();
}
