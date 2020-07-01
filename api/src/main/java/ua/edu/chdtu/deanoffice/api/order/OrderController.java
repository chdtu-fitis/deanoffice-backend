package ua.edu.chdtu.deanoffice.api.order;

import lombok.RequiredArgsConstructor;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.api.order.dto.OrderNumberAndDateDTO;
import ua.edu.chdtu.deanoffice.api.order.dto.OrderTypeDTO;
import ua.edu.chdtu.deanoffice.entity.order.Order;
import ua.edu.chdtu.deanoffice.service.order.beans.OrderCreateCommand;
import ua.edu.chdtu.deanoffice.service.order.beans.OrderParagraphPiece;
import ua.edu.chdtu.deanoffice.service.order.OrderService;
import ua.edu.chdtu.deanoffice.service.order.beans.OrderTypeBean;
import ua.edu.chdtu.deanoffice.service.order.beans.StudentExpelCreateCommand;
import ua.edu.chdtu.deanoffice.service.order.beans.StudentExpelResponseDto;

import javax.validation.constraints.Min;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Validated
public class OrderController extends DocumentResponseController {

    private final OrderService orderService;

    @GetMapping("/{studentExpelId}/student-expel")
    public ResponseEntity<Resource> generateStudentExpelDocument(@PathVariable Integer studentExpelId)
            throws Docx4JException, IOException {
        File orderDocument = orderService.generateStudentExpelDocument(studentExpelId);
        return buildDocumentResponseEntity(orderDocument, orderDocument.getName(), MEDIA_TYPE_DOCX);
    }

    @PutMapping("/{orderId}/student-expel")
    public ResponseEntity<List<StudentExpelResponseDto>> saveStudentExpelOperation(@PathVariable Integer orderId,
                                                                                   @RequestBody List<StudentExpelCreateCommand> studentExpelCreateCommand) throws IOException {
        return ResponseEntity.ok(orderService.saveStudentExpel(orderId,studentExpelCreateCommand));
    }

    @PostMapping
    public ResponseEntity<Integer> saveOrder(@RequestBody OrderCreateCommand orderCreateCommand) {
        return ResponseEntity.ok(orderService.createOrder(orderCreateCommand));
    }

    @GetMapping("/paragraph")
    public ResponseEntity<List<OrderParagraphPiece>> getJsonParagraphByOrderType(String orderType) {
        return ResponseEntity.ok(orderService.getParsedParagraphByOrderType(orderType));
    }

    @GetMapping("/order-type")
    public ResponseEntity<Set<OrderTypeDTO>> getAvailableOrderTypes() {
        Set<OrderTypeBean> orderTypes = orderService.getOrderTypes();
        return ResponseEntity.ok(map(orderTypes, OrderTypeDTO.class));
    }

    @PutMapping("/{id}")
    public ResponseEntity signOrder(@PathVariable @Min(1) int id,
                                    @Validated @RequestBody OrderNumberAndDateDTO orderNumberAndDateDto) throws Exception {
        Order order = orderService.getOrderById(id);
        if (order == null || !order.getActive()) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Даний наказ не можна підписувати");
        } else {
            order.setOrderNumber(orderNumberAndDateDto.getOrderNumber());
            order.setOrderDate(orderNumberAndDateDto.getOrderDate());
            order.setSigned(true);
            orderService.updateOrder(order);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
    }
}
