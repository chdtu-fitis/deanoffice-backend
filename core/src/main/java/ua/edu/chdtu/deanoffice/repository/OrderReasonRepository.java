package ua.edu.chdtu.deanoffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.chdtu.deanoffice.entity.OrderReason;

import java.util.List;

public interface OrderReasonRepository extends JpaRepository<OrderReason, Integer> {
    List<OrderReason> findAllByKindOrderByName(String kind);
}
