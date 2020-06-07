package ua.edu.chdtu.deanoffice.api.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.order.Order;
import ua.edu.chdtu.deanoffice.entity.order.OrderType;
import ua.edu.chdtu.deanoffice.repository.order.OrderApproverTemplateRepository;
import ua.edu.chdtu.deanoffice.repository.order.OrderRepository;
import ua.edu.chdtu.deanoffice.repository.order.OrderTemplateVersionRepository;
import ua.edu.chdtu.deanoffice.service.FacultyService;
import ua.edu.chdtu.deanoffice.service.OrderReasonService;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderTemplateVersionRepository orderTemplateVersionRepository;
    private final OrderRepository orderRepository;
    private final OrderApproverTemplateRepository orderApproverTemplateRepository;
    private final FacultyService facultyService;
    private final OrderReasonService orderReasonService;

    public String getParagraphByOrderType(OrderType orderType) {
        return orderTemplateVersionRepository.findByDbTableNameAndActive(orderType.toString(), true).getParagraphTemplate();
    }

    public Integer createOrder(OrderCreateCommand orderCreateCommand) {
        return orderRepository
                .save(new Order().setOrderApproveTemplate(orderApproverTemplateRepository
                        .getOne(orderCreateCommand.getOrderApproveTemplateId()))
                        .setOrderDate(orderCreateCommand.getOrderDate())
                        .setOrderNumber(orderCreateCommand.getOrderNumber())
                        .setOrderTemplateVersion(orderTemplateVersionRepository
                                .findByDbTableNameAndActive(orderCreateCommand.getOrderType().toString(), true))
                        .setFaculty(facultyService.getById(orderCreateCommand.getFacultyId()))
                        .setOrderParagraph(orderCreateCommand.getParagraph())
                        .setComment(orderCreateCommand.getOrderComment()))
                .getId();
    }
}
