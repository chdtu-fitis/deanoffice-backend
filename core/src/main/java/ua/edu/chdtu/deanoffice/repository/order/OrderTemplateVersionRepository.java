package ua.edu.chdtu.deanoffice.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.chdtu.deanoffice.entity.order.OrderTemplateVersion;
import ua.edu.chdtu.deanoffice.entity.order.OrderType;

public interface OrderTemplateVersionRepository extends JpaRepository<OrderTemplateVersion, Integer> {

    OrderTemplateVersion findByDbTableName(String orderType);

}
