package ua.edu.chdtu.deanoffice.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.chdtu.deanoffice.entity.order.OrderSerializedData;

public interface OrderDtoSerializedRepository extends JpaRepository<OrderSerializedData, Long> {

    OrderSerializedData findByOrderId(Integer id);

}
