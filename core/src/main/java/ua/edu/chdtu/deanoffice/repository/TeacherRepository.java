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
    List<Teacher> findAllByOrderBySurname();

    @Query("SELECT t FROM Teacher t WHERE t.active = :active")
    List<Teacher> findAllByActive(
            @Param("active") boolean active
    );

    @Query("SELECT t FROM Teacher t WHERE t.active = :active AND t.department.faculty.id = :facultyId")
    List<Teacher> findAllByActiveAndFacultyId(
            @Param("active") boolean active,
            @Param("facultyId") int facultyId
    );

    @Query("SELECT t FROM Teacher t WHERE t.active = :active AND t.department.id = :departmentId")
    List<Teacher> findAllByActiveAndDepartmentId(
            @Param("active") boolean active,
            @Param("departmentId") int departmentId
    );

    @Query("SELECT t FROM Teacher t WHERE t.active = :active AND t.surname = :surname")
    List<Teacher> findAllByActiveAndSurname(
            @Param("active") boolean active,
            @Param("surname") String surname
    );

    @Query("SELECT t FROM Teacher t WHERE t.active = :active AND t.department.id = :departmentId AND t.surname = :surname")
    List<Teacher> findAllByActiveAndDepartmentIdAndSurname(
            @Param("active") boolean active,
            @Param("departmentId") int departmentId,
            @Param("surname") String surname
    );

    @Modifying
    @Transactional
    @Query(value = "UPDATE teacher t SET active = false WHERE t.id IN (:ids)", nativeQuery = true)
    void setTeachersInactiveByIds(@Param("ids") List<Integer> ids);

    @Query(value = "SELECT t.id FROM teacher t INNER JOIN department d ON t.department_id = d.id " +
            "WHERE t.id IN (:teacherIds) AND d.faculty_id <> :facultyId", nativeQuery = true)
    List<Integer> findIdsEveryoneWhoDoesNotBelongToThisFacultyId(
            @Param("facultyId") int facultyId,
            @Param("teacherIds") List<Integer> teacherIds
    );

    @Query(value = "SELECT count(t.id) FROM teacher t WHERE t.id IN (:teacherIds) AND t.active = false", nativeQuery = true)
    int countInactiveTeachersByIds(
            @Param("teacherIds") List<Integer> teacherIds
    );
}
