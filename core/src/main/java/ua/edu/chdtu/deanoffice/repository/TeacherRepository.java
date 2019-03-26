package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.Teacher;

import java.util.List;

public interface TeacherRepository extends JpaRepository<Teacher, Integer> {
    List<Teacher> findAllByOrderBySurname();

    @Query("SELECT t FROM Teacher t WHERE t.active = :active")
    List<Teacher> findAllByActive(
            @Param("active") boolean active
    );



//    List<Teacher> findById(List<Integer> ids);
//
//    List<Teacher> getTeachersByIdIn(List<Integer> ids);

    @Transactional
    void deleteByIdIn(List<Integer> ids);


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
