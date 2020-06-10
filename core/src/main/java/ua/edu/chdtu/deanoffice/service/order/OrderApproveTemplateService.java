package ua.edu.chdtu.deanoffice.service.order;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.order.OrderApproveTemplate;
import ua.edu.chdtu.deanoffice.exception.UnauthorizedFacultyDataException;
import ua.edu.chdtu.deanoffice.repository.order.OrderApproveTemplateRepository;
import ua.edu.chdtu.deanoffice.util.FacultyUtil;
import java.util.List;

@Service
public class OrderApproveTemplateService {
    private OrderApproveTemplateRepository orderApproveTemplateRepository;

    public OrderApproveTemplateService(OrderApproveTemplateRepository approveTemplateRepository) {
        this.orderApproveTemplateRepository = approveTemplateRepository;
    }

    public OrderApproveTemplate create(OrderApproveTemplate approveTemplate) {
        approveTemplate.setActive(true);
        return this.orderApproveTemplateRepository.save(approveTemplate);
    }

    public List<OrderApproveTemplate> getApproveTemplateForFaculty(boolean active) {
        int facultyId = FacultyUtil.getUserFacultyIdInt();
        return orderApproveTemplateRepository.findApproverTemplateForFacultyByActive(active, facultyId);
    }

    public OrderApproveTemplate getApproveTemplateById(Integer id) {
        return orderApproveTemplateRepository.findOne(id);
    }

    public void delete(OrderApproveTemplate orderApproveTemplate) throws UnauthorizedFacultyDataException {
        orderApproveTemplate.setActive(false);
        orderApproveTemplateRepository.save(orderApproveTemplate);
    }
}
