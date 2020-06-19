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
import ua.edu.chdtu.deanoffice.api.order.dto.OrderApproveTemplateDTO;
import ua.edu.chdtu.deanoffice.entity.Faculty;
import ua.edu.chdtu.deanoffice.entity.order.OrderApproveTemplate;
import ua.edu.chdtu.deanoffice.entity.order.OrderApprover;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;
import ua.edu.chdtu.deanoffice.service.FacultyService;
import ua.edu.chdtu.deanoffice.service.order.OrderApproveTemplateService;
import ua.edu.chdtu.deanoffice.service.order.OrderApproverService;
import ua.edu.chdtu.deanoffice.util.FacultyUtil;
import java.util.List;
import java.util.stream.Collectors;
import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;
import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.strictMap;

@RestController
public class OrderApproveTemplateController {
    private OrderApproveTemplateService approveTemplateService;
    private FacultyService facultyService;
    private OrderApproverService orderApproverService;

    public OrderApproveTemplateController(OrderApproveTemplateService approveTemplateService, FacultyService facultyService, OrderApproverService orderApproverService) {
        this.approveTemplateService = approveTemplateService;
        this.facultyService = facultyService;
        this.orderApproverService = orderApproverService;
    }

    @PostMapping("/orders/approve-template")
    public ResponseEntity createApproveTemplate(@RequestBody OrderApproveTemplateDTO approveTemplateDTO) {
        try {
            OrderApproveTemplate newApproveTemplate = map(approveTemplateDTO, OrderApproveTemplate.class);
            Integer facultyId = FacultyUtil.getUserFacultyIdInt();
            Faculty faculty = facultyService.getById(facultyId);
            OrderApprover mainApprover = orderApproverService.getApproverById(newApproveTemplate.getMainApprover().getId());
            OrderApprover initiatorApprover = orderApproverService.getApproverById(newApproveTemplate.getInitiatorApprover().getId());
            List<Integer> approversIds = newApproveTemplate.getApprovers().stream().map(BaseEntity::getId).collect(Collectors.toList());
            List<OrderApprover> approvers = orderApproverService.getActiveApproversByIds(approversIds);
            newApproveTemplate.setFaculty(faculty);
            newApproveTemplate.setMainApprover(mainApprover);
            newApproveTemplate.setInitiatorApprover(initiatorApprover);
            newApproveTemplate.setApprovers(approvers);
            OrderApproveTemplate orderApproveTemplate = approveTemplateService.create(newApproveTemplate);
            OrderApproveTemplateDTO templateDTO = map(orderApproveTemplate, OrderApproveTemplateDTO.class);
            return ResponseEntity.ok(templateDTO);
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @GetMapping("/orders/approve-template")
    public ResponseEntity<List<OrderApproveTemplateDTO>> getApproveTemplates(@RequestParam(required = false, defaultValue = "true") boolean active) {
        List<OrderApproveTemplate> approveTemplates = approveTemplateService.getApproveTemplateForFaculty(active);
        return ResponseEntity.ok(strictMap(approveTemplates, OrderApproveTemplateDTO.class));
    }

    @DeleteMapping("/orders/approve-template/{id}")
    public ResponseEntity deleteApproveTemplate(@PathVariable("id") int id) {
        try {
            OrderApproveTemplate orderApproveTemplate = approveTemplateService.getApproveTemplateById(id);
            approveTemplateService.delete(orderApproveTemplate);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @PutMapping("/orders/approve-template/{id}/restore")
    public ResponseEntity restoreApproveTemplate(@PathVariable("id") int id) {
        try {
            OrderApproveTemplate approveTemplateById = approveTemplateService.getApproveTemplateById(id);
            approveTemplateService.restore(approveTemplateById);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, CourseController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
