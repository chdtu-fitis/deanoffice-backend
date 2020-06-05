package ua.edu.chdtu.deanoffice.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.chdtu.deanoffice.entity.order.OrderApproveTemplate;
import java.util.List;

public interface ApproveTemplateRepository extends JpaRepository<OrderApproveTemplate, Integer> {
    @Query("select at from OrderApproveTemplate as at where at.active =:active AND (at.faculty.id=:facultyId or at.faculty is null)")
    List<OrderApproveTemplate> findApproverTemplateForFacultyByActive(
            @Param("active") boolean active,
            @Param("facultyId") int facultyId);
}
