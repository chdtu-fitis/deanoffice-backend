package ua.edu.chdtu.deanoffice.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.order.OrderApprover;
import java.util.List;

public interface OrderApproverRepository extends JpaRepository<OrderApprover,Integer> {
    @Query("select oa from OrderApprover as oa " +
            "where (oa.faculty is null OR oa.faculty.id = :facultyId) AND active = true")
    List<OrderApprover> findApproversForFaculty(@Param("facultyId") int facultyId);
}