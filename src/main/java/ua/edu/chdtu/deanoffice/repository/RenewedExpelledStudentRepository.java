package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.RenewedExpelledStudent;

import java.util.List;

public interface RenewedExpelledStudentRepository extends JpaRepository<RenewedExpelledStudent, Integer> {
    @Query("select re from RenewedExpelledStudent re " +
            "where re.studentExpel.id =  :student_expel_id")
    RenewedExpelledStudent findRenewedStudentByExpelId(@Param("student_expel_id") Integer studentExpelId);

    @Query(value = "select * from renewed_expelled_student re " +
            "inner join student_expel se on re.student_expel_id = se.id " +
            "WHERE se.student_degree_id = :studentDegreeId", nativeQuery = true)
    List<RenewedExpelledStudent> findAllByStudentDegreeId(@Param("studentDegreeId") Integer studentDegreeId);
}
