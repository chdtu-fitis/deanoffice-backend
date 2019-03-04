package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.StudentTransfer;

public interface StudentTransferRepository extends JpaRepository<StudentTransfer, Integer> {

    @Modifying
    @Query(value = "UPDATE StudentDegree sd " +
            "SET  sd.specialization.id = :new_specialization_id where sd.id = :student_degree_id ")
    void updateSpecialization(
            @Param("new_specialization_id") Integer newSpecializationId,
            @Param("student_degree_id") Integer studentDegreeId
    );
}