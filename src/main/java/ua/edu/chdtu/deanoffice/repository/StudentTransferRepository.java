package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.Payment;
import ua.edu.chdtu.deanoffice.entity.StudentTransfer;

public interface StudentTransferRepository extends JpaRepository<StudentTransfer, Integer> {

    @Modifying
    @Query(value = "UPDATE StudentDegree sd " +
            "SET  sd.specialization.id = :new_specialization_id, " +
            "sd.studentGroup.id = :new_student_group_id, " +
            "sd.payment = :new_payment " +
            "where sd.id = :student_degree_id ")
    void updateSpecializationAndStudentGroupAndPayment(
            @Param("new_specialization_id") Integer newSpecializationId,
            @Param("new_student_group_id") Integer newStudentGroupId,
            @Param("new_payment") Payment newPayment,
            @Param("student_degree_id") Integer studentDegreeId
    );

    @Query("select sd.specialization.id from StudentDegree sd " +
           "where sd.id = :student_degree_id ")
    Integer getSpecializationIdByStudentDegreeId(
            @Param("student_degree_id") Integer studentDegreeId
    );

    @Query("select sd.studentGroup.id from StudentDegree sd " +
            "where sd.id = :student_degree_id ")
    Integer getStudentGroupIdByStudentDegreeId(
            @Param("student_degree_id") Integer studentDegreeId
    );

    @Query("select sd.payment from StudentDegree sd " +
            "where sd.id = :student_degree_id ")
    Payment getPaymentByStudentDegreeId(
            @Param("student_degree_id") Integer studentDegreeId
    );
}