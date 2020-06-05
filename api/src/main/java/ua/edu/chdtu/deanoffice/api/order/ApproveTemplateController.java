package ua.edu.chdtu.deanoffice.api.order;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
import ua.edu.chdtu.deanoffice.service.FacultyService;
import ua.edu.chdtu.deanoffice.service.order.ApproveTemplateService;
import ua.edu.chdtu.deanoffice.service.order.OrderApproverService;
import ua.edu.chdtu.deanoffice.util.FacultyUtil;
import java.util.List;
import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;
import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.strictMap;


@RestController
public class ApproveTemplateController {
    private ApproveTemplateService approveTemplateService;
    private FacultyService facultyService;
    private OrderApproverService orderApproverService;

    public ApproveTemplateController(ApproveTemplateService approveTemplateService, FacultyService facultyService, OrderApproverService orderApproverService) {
        this.approveTemplateService = approveTemplateService;
        this.facultyService = facultyService;
        this.orderApproverService = orderApproverService;
    }

    @PostMapping("/orders/approve/template/create")
    public ResponseEntity createApprover(@RequestBody OrderApproveTemplateDTO approveTemplateDTO) {
        try {
            OrderApproveTemplate newApproveTemplate = map(approveTemplateDTO, OrderApproveTemplate.class);
            Integer facultyId = FacultyUtil.getUserFacultyIdInt();
            Faculty faculty = facultyService.getById(facultyId);
            OrderApprover mainApprover = orderApproverService.getApproverById(newApproveTemplate.getMainApprover().getId());
            OrderApprover initiatorApprover = orderApproverService.getApproverById(newApproveTemplate.getInitiatorApprover().getId());
            orderApproverService.getApproverById(newApproveTemplate.getApprovers());
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

    @GetMapping("/orders/approve/template")
    public ResponseEntity<List<OrderApproveTemplateDTO>> getApproveTemplates(@RequestParam(required = false, defaultValue = "true") boolean active) {
        List<OrderApproveTemplate> approveTemplates = approveTemplateService.getApproveTemplateForFaculty(active);
        return ResponseEntity.ok(strictMap(approveTemplates, OrderApproveTemplateDTO.class));
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, CourseController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
