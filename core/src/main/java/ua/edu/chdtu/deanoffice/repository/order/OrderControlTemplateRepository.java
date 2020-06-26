package ua.edu.chdtu.deanoffice.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.chdtu.deanoffice.entity.order.OrderControlTemplate;

public interface OrderControlTemplateRepository extends JpaRepository<OrderControlTemplate, Integer> {

    OrderControlTemplate findByFacultyIdAndActive(Integer facultyId, Boolean active);

}
