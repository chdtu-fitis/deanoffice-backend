package ua.edu.chdtu.deanoffice.api.order;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.course.CourseController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.api.order.dto.OrderApproverDTO;
import ua.edu.chdtu.deanoffice.entity.order.OrderApprover;
import ua.edu.chdtu.deanoffice.service.order.OrderApproverService;
import java.util.List;
import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;
import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.strictMap;

@RestController
public class OrderApproverController {
    private OrderApproverService orderApproverService;

    public OrderApproverController(OrderApproverService orderApproverService) {
        this.orderApproverService = orderApproverService;
    }

    @GetMapping("/orders/approvers")
    public ResponseEntity<List<OrderApproverDTO>> getOrderApprovers() {
        List<OrderApprover> orderApprovers = orderApproverService.getApproversForFaculty();
        return ResponseEntity.ok(strictMap(orderApprovers, OrderApproverDTO.class));
    }

    @DeleteMapping("/orders/{id}")
    public ResponseEntity deleteApprover(@PathVariable("id") int id) {
        try {
            OrderApprover orderApprover = orderApproverService.getApproverById(id);
            orderApproverService.delete(orderApprover);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @PostMapping("/orders/approver")
    public ResponseEntity createApprover(@RequestBody OrderApproverDTO orderApproverDTO) {
        try {
            OrderApprover newApprover = map(orderApproverDTO, OrderApprover.class);
            orderApproverService.create(newApprover);
            return ResponseEntity.ok(newApprover);
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, CourseController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
