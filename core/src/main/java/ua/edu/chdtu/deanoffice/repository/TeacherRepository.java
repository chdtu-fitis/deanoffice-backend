package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.Teacher;

import java.util.List;

/**
 * Created by os199 on 06.11.2017.
 */
public interface TeacherRepository extends JpaRepository<Teacher, Integer> {
    @Query("select t from Teacher t where t.department.faculty.id = :facultyId and t.active = true ")
    List<Teacher> findAllByFaculty(@Param("facultyId") int facultyId);

    @Query
    List<Teacher> findTeacherById(@Param("teacherId") int teacherId);

}
