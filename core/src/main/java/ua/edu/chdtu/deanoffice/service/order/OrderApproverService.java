package ua.edu.chdtu.deanoffice.service.order;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.order.OrderApprover;
import ua.edu.chdtu.deanoffice.repository.order.OrderApproverRepository;
import ua.edu.chdtu.deanoffice.util.FacultyUtil;

import java.util.List;

@Service
public class OrderApproverService {
    private OrderApproverRepository orderApproverRepository;

    public OrderApproverService(OrderApproverRepository orderApproverRepository) {
        this.orderApproverRepository = orderApproverRepository;
    }

    public List<OrderApprover> getApproversForFaculty() {
        return orderApproverRepository.findApproversForFaculty(FacultyUtil.getUserFacultyIdInt());
    }
}
