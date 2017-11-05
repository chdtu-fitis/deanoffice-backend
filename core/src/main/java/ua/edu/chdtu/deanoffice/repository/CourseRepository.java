package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.edu.chdtu.deanoffice.entity.Course;

import java.util.List;

/**
 * Created by os199 on 05.11.2017.
 */
public interface CourseRepository extends JpaRepository<Course, Integer> {
//    @Query("select c.courseName.name, c.id, c.hours from Course as c inner join c.courseName on c.courseName.id = c.id " +
//            "inner join c. as cfg on cfg.course_id = c.id inner join studentgroup as sg on sg.id = cfg.studentgroup_id " +
//            "inner join specialization as spec on sg.specialization_id = spec.id inner join faculty as f on f.id = spec.faculty_id " +
//            "where " +
//            "sg.active = 't' and sg.specialization_id = 1 and f.id = 1")
//    List<Course> findAllByGroup();
}
