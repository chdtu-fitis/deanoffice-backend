package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.StudentExpel;

import java.util.List;

public interface StudentExpelRepository extends JpaRepository<StudentExpel, Integer> {
    @Query("select se from StudentExpel se " +
            "where se.reason.id not in :success_reason_id")
    List<StudentExpel> findAllFired(@Param("success_reason_id") Integer[] successReasonId);
}
