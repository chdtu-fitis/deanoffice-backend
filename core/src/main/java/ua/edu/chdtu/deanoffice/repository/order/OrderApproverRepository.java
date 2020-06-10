package ua.edu.chdtu.deanoffice.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.order.OrderApprover;

import java.util.List;

public interface OrderApproverRepository extends JpaRepository<OrderApprover, Integer> {
    @Query("select oa from OrderApprover as oa " +
            "where oa.active =:active " +
            "AND (oa.faculty.id=:facultyId or oa.faculty is null)")
    List<OrderApprover> findApproversForFacultyByActive(
            @Param("active") boolean active,
            @Param("facultyId") int facultyId);

    @Query("select oa from OrderApprover oa " +
            "WHERE oa.id IN :approversIds and oa.active = true " +
            "and (oa.faculty.id=:facultyId or oa.faculty is null)")
    List<OrderApprover> findActiveApproversByIds(
            @Param("approversIds") List<Integer> approversIds,
            @Param("facultyId") int facultyId);
}