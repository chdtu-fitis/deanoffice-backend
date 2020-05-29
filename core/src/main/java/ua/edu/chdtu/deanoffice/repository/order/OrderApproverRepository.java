package ua.edu.chdtu.deanoffice.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.order.OrderApprover;
import java.util.List;

public interface OrderApproverRepository extends JpaRepository<OrderApprover,Integer> {
    @Query("select oa from OrderApprover as oa where oa.active =:active AND (oa.faculty.id=:facultyId or oa.faculty is null)")
    List<OrderApprover> findApproversForFacultyAndActive(
            @Param("active") boolean active,
            @Param("facultyId") int facultyId);
}