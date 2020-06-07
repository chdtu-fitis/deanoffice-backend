package ua.edu.chdtu.deanoffice.service.order;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.order.OrderApproveTemplate;
import ua.edu.chdtu.deanoffice.repository.order.ApproveTemplateRepository;
import ua.edu.chdtu.deanoffice.util.FacultyUtil;
import java.util.List;

@Service
public class OrderApproveTemplateService {
    private ApproveTemplateRepository orderApproveTemplateRepository;

    public OrderApproveTemplateService(ApproveTemplateRepository approveTemplateRepository) {
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
}
