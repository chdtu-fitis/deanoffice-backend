package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.Teacher;

import java.util.List;
import java.util.Set;

public interface TeacherRepository extends JpaRepository<Teacher, Integer> {

    @Query("SELECT t FROM Teacher t WHERE t.active = :active AND t.department.faculty.id = :facultyId " +
            "ORDER BY t.surname, t.name, t.patronimic")
    List<Teacher> findAllByActiveAndFacultyId(
            @Param("active") boolean active,
            @Param("facultyId") int facultyId
    );

    @Query("SELECT t FROM Teacher t WHERE t.active = true AND t.department.faculty.id = :facultyId AND lower(t.surname) like :searchStr% " +
            "ORDER BY t.surname, t.name, t.patronimic")
    List<Teacher> findActiveBySurnamePartAndFacultyId(
            @Param("searchStr") String searchStr,
            @Param("facultyId") int facultyId
    );

    @Query("SELECT t FROM Teacher t WHERE t.active = :active " +
            "ORDER BY t.surname, t.name, t.patronimic")
    List<Teacher> findAllByActive(
            @Param("active") boolean active
    );

    @Modifying
    @Transactional
    @Query(value = "UPDATE teacher t SET active = false WHERE t.id IN (:ids)", nativeQuery = true)
    void setTeachersInactiveByIds(@Param("ids") List<Integer> ids);
}
