package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.OrderReason;
import ua.edu.chdtu.deanoffice.repository.OrderReasonRepository;

import java.util.List;

@Service
public class OrderReasonService {
    private final OrderReasonRepository orderReasonRepository;

    public OrderReasonService(OrderReasonRepository orderReasonRepository) {
        this.orderReasonRepository = orderReasonRepository;
    }

    public OrderReason getById(Integer id) {
        return this.orderReasonRepository.findById(id).get();
    }

    public List<OrderReason> getAllByKind(String kind) {
        return this.orderReasonRepository.findAllByKindOrderByName(kind);
    }
}
