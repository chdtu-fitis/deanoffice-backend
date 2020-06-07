package ua.edu.chdtu.deanoffice.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.chdtu.deanoffice.entity.order.OrderApproveTemplate;

public interface OrderApproverTemplateRepository extends JpaRepository<OrderApproveTemplate, Integer> {
}
