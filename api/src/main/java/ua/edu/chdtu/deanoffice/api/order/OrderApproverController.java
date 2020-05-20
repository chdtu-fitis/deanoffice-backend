package ua.edu.chdtu.deanoffice.api.order;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.entity.order.OrderApprover;
import ua.edu.chdtu.deanoffice.service.order.OrderApproverService;

import java.util.List;

@RestController("/orders/approvers")
public class OrderApproverController {
    private OrderApproverService orderApproverService;

    public OrderApproverController(OrderApproverService orderApproverService) {
        this.orderApproverService = orderApproverService;
    }

    @GetMapping
    public ResponseEntity<List<OrderApprover>> getOrderApprovers() {
        List<OrderApprover> orderApprovers = orderApproverService.getApproversForFaculty();
        return ResponseEntity.ok(orderApprovers);
    }
}
