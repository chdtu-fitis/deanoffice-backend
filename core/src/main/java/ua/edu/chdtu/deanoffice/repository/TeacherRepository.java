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

/*
    @Modifying
    @Query(value = "UPDATE student_group sg SET active = false WHERE sg.id IN (:ids)", nativeQuery = true)
    void setStudentGroupInactiveByIds(@Param("ids") Set<Integer> ids);*/

//    List<Teacher> findById(List<Integer> ids);
//
//    List<Teacher> getTeachersByIdIn(List<Integer> ids);
/*
    @Transactional
    void deleteByIdIn(List<Integer> ids);*/


//    @Query("SELECT sd from StudentDegree sd " +
//            "where sd.id in :student_degree_ids")
//    List<StudentDegree> getAllByIds(@Param("student_degree_ids") List<Integer> studentDegreeIds);


//    @Query("SELECT sd from StudentDegree sd " +
//            "where sd.active = :active " +
//            "and sd.specialization.faculty.id = :facultyId " +
//            "order by sd.student.surname, sd.student.name, sd.student.patronimic, sd.specialization.name")
//    List<StudentDegree> findAllByActive(
//            @Param("active") boolean active,
//            @Param("facultyId") Integer facultyId
//    );
}
