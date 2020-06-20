package ua.edu.chdtu.deanoffice.api.order;

import lombok.RequiredArgsConstructor;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.service.order.dto.OrderCreateCommand;
import ua.edu.chdtu.deanoffice.service.order.dto.OrderParagraphPiece;
import ua.edu.chdtu.deanoffice.service.order.OrderService;
import ua.edu.chdtu.deanoffice.service.order.dto.OrderType;
import ua.edu.chdtu.deanoffice.service.order.dto.StudentExpelCreateCommand;
import ua.edu.chdtu.deanoffice.service.order.dto.StudentExpelResponseDto;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
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

    @PutMapping
    public ResponseEntity<Integer> saveOrder(@RequestBody OrderCreateCommand orderCreateCommand) {
        return ResponseEntity.ok(orderService.createOrder(orderCreateCommand));
    }

    @GetMapping("/paragraph")
    public ResponseEntity<List<OrderParagraphPiece>> getJsonParagraphByOrderType(String orderType) {
        return ResponseEntity.ok(orderService.getParsedParagraphByOrderType(orderType));
    }

    @GetMapping("/order-type")
    public ResponseEntity<List<OrderType>> getAvailableOrderTypes() {
        return ResponseEntity.ok(Arrays.asList(new OrderType("STUDENT_EXPEL", "про відрахування")));
    }
}