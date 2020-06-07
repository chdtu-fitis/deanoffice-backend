package ua.edu.chdtu.deanoffice.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.chdtu.deanoffice.entity.order.OrderTemplateVersion;

public interface OrderTemplateVersionRepository extends JpaRepository<OrderTemplateVersion, Integer> {

    OrderTemplateVersion findByDbTableNameAndActive(String orderType, Boolean active);

}
