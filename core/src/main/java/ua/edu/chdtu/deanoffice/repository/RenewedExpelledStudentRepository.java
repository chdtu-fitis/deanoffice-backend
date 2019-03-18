package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.RenewedExpelledStudent;

public interface RenewedExpelledStudentRepository extends JpaRepository<RenewedExpelledStudent, Integer> {
    @Query("select re from RenewedExpelledStudent re " +
            "where re.studentExpel.id =  :student_expel_id")
    RenewedExpelledStudent findRenewedStudentByExpelId(@Param("student_expel_id") Integer studentExpelId);
}
