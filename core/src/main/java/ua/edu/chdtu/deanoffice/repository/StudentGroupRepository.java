package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;

import java.util.List;

/**
 * Created by os199 on 05.11.2017.
 */
public interface StudentGroupRepository extends JpaRepository<StudentGroup, Integer> {

    @Query("select studentGroup from StudentGroup as studentGroup " +
            "join studentGroup.specialization " +
            "join studentGroup.specialization.faculty " +
            "where studentGroup.active = 'T' and studentGroup.specialization.faculty.id = :facultyId")
    List<StudentGroup> findAllByFaculty(@Param("facultyId") int facultyId);

    @Query("select cfg.studentGroup from CourseForGroup as cfg " +
            "join cfg.studentGroup sg " +
            "where sg.active = true and cfg.course.id = :courseId")
    List<StudentGroup> findAllByCourse(@Param("courseId") int courseId);
}
