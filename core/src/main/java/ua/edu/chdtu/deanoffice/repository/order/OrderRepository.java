package ua.edu.chdtu.deanoffice.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ua.edu.chdtu.deanoffice.entity.order.Order;
import ua.edu.chdtu.deanoffice.service.order.OrderStatusSpecification;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer>, JpaSpecificationExecutor<Order> {
}
