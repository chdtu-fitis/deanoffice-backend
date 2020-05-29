package ua.edu.chdtu.deanoffice.service.order;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.order.OrderApprover;
import ua.edu.chdtu.deanoffice.exception.UnauthorizedFacultyDataException;
import ua.edu.chdtu.deanoffice.repository.order.OrderApproverRepository;
import ua.edu.chdtu.deanoffice.util.FacultyUtil;
import java.util.List;

@Service
public class OrderApproverService {
    private OrderApproverRepository orderApproverRepository;

    public OrderApproverService(OrderApproverRepository orderApproverRepository) {
        this.orderApproverRepository = orderApproverRepository;
    }

    public List<OrderApprover> getApproversForFaculty(boolean active) {
        int facultyId = FacultyUtil.getUserFacultyIdInt();
        return orderApproverRepository.findApproversForFacultyByActive(active, facultyId);
    }

    public OrderApprover getApproverById(int id) {
        return orderApproverRepository.findOne(id);
    }

//    @FacultyAuthorized
    public void delete(OrderApprover orderApprover) throws UnauthorizedFacultyDataException {
        orderApprover.setActive(false);
        orderApproverRepository.save(orderApprover);
    }

    public OrderApprover create(OrderApprover orderApprover) {
        orderApprover.setActive(true);
        return this.orderApproverRepository.save(orderApprover);
    }

    public void restore(OrderApprover orderApprover) {
        orderApprover.setActive(true);
        orderApproverRepository.save(orderApprover);
    }
}
