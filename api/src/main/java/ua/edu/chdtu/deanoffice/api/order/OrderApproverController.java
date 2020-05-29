package ua.edu.chdtu.deanoffice.api.order;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.course.CourseController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.api.order.dto.OrderApproverDTO;
import ua.edu.chdtu.deanoffice.entity.Faculty;
import ua.edu.chdtu.deanoffice.entity.order.OrderApprover;
import ua.edu.chdtu.deanoffice.service.FacultyService;
import ua.edu.chdtu.deanoffice.service.order.OrderApproverService;
import java.util.List;
import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;
import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.strictMap;

@RestController
public class OrderApproverController {
    private OrderApproverService orderApproverService;
    private FacultyService facultyService;

    public OrderApproverController(OrderApproverService orderApproverService, FacultyService facultyService) {
        this.orderApproverService = orderApproverService;
        this.facultyService = facultyService;
    }

    @GetMapping("/orders/approvers")
    public ResponseEntity<List<OrderApproverDTO>> getOrderApprovers(@RequestParam(required = false, defaultValue = "true") boolean active) {
        List<OrderApprover> orderApprovers = orderApproverService.getApproversForFaculty(active);
        return ResponseEntity.ok(strictMap(orderApprovers, OrderApproverDTO.class));
    }

    @DeleteMapping("/orders/approvers/{id}")
    public ResponseEntity deleteApprover(@PathVariable("id") int id) {
        try {
            OrderApprover orderApprover = orderApproverService.getApproverById(id);
            orderApproverService.delete(orderApprover);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @PostMapping("/orders/approvers")
    public ResponseEntity createApprover(@RequestBody OrderApproverDTO orderApproverDTO) {
        try {
            OrderApprover newApprover = map(orderApproverDTO, OrderApprover.class);
            Faculty faculty = facultyService.getById(orderApproverDTO.getFaculty().getId());
            newApprover.setFaculty(faculty);
            orderApproverService.create(newApprover);
            return ResponseEntity.ok(newApprover);
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @PutMapping("/orders/approvers/{id}/restore")
    public ResponseEntity restoreApprover(@PathVariable("id") int id) {
        try {
            OrderApprover orderApprover = orderApproverService.getApproverById(id);
            orderApproverService.restore(orderApprover);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, CourseController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
