package ua.edu.chdtu.deanoffice.api.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.entity.StudentExpel;
import ua.edu.chdtu.deanoffice.entity.order.Order;
import ua.edu.chdtu.deanoffice.entity.order.OrderType;
import ua.edu.chdtu.deanoffice.service.StudentExpelService;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;
import ua.edu.chdtu.deanoffice.service.document.TemplateUtil;
import ua.edu.chdtu.deanoffice.service.order.OrderCreateCommand;
import ua.edu.chdtu.deanoffice.service.order.OrderParagraphPiece;
import ua.edu.chdtu.deanoffice.service.order.OrderParsedParagraphDto;
import ua.edu.chdtu.deanoffice.service.order.OrderService;
import ua.edu.chdtu.deanoffice.service.order.StudentExpelBusinessInformation;
import ua.edu.chdtu.deanoffice.service.order.StudentExpelCreateCommand;

import java.io.IOException;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController extends DocumentResponseController {

    private final OrderService orderService;
    private final DocumentIOService documentIOService;
    private final StudentExpelService studentExpelService;
    private final ObjectMapper objectMapper;

    @GetMapping("/{studentExpelId}/student-expel")
    public ResponseEntity<Resource> generateStudentExpelDocument(@PathVariable Integer studentExpelId)
            throws Docx4JException, IOException {
        StudentExpel studentExpel = studentExpelService.getById(studentExpelId);
        StudentExpelBusinessInformation studentExpelBusinessInformation = objectMapper.readValue(studentExpel.getOrderBusinessOperation(), StudentExpelBusinessInformation.class);
        Order order = orderService.getOrderById(studentExpelBusinessInformation.getOrderId());
        String orderName = order.getOrderTemplateVersion().getDbTableName() + " " + order.getOrderNumber();
        WordprocessingMLPackage orderDocument = documentIOService.loadTemplate(DocumentIOService.ORDERS_PATH +
                order.getOrderTemplateVersion().getTemplateName());
        Map<String, String> placeholderValue = new HashMap<>();
        placeholderValue.put("PHParagraph", objectMapper
                .readValue(studentExpel.getOrderParagraphJson(),
                        OrderParsedParagraphDto.class).getParagraphFields().stream()
                .map(OrderParagraphPiece::getValue).collect(Collectors.joining()));
        placeholderValue.put("PHOrderDate", order.getOrderDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString());
        placeholderValue.put("PHNumber", order.getOrderNumber());
        TemplateUtil.replaceTextPlaceholdersInTemplate(orderDocument, placeholderValue);
        return buildDocumentResponseEntity(documentIOService.saveDocumentToTemp(orderDocument, orderName, FileFormatEnum.DOCX),
                orderName, MEDIA_TYPE_DOCX);
    }

    @PutMapping("/{orderId}/student-expel")
    public ResponseEntity<Integer> saveStudentExpelOperation(@RequestBody StudentExpelCreateCommand studentExpelCreateCommand) throws IOException {
        return ResponseEntity.ok(orderService.saveStudentExpel(studentExpelCreateCommand));
    }

    @PutMapping
    public ResponseEntity<Integer> saveOrder(@RequestBody OrderCreateCommand orderCreateCommand) {
        return ResponseEntity.ok(orderService.createOrder(orderCreateCommand));
    }

    @GetMapping("/paragraph")
    public ResponseEntity<List<OrderParagraphPiece>> getJsonParagraphByOrderType(OrderType orderType) {
        return ResponseEntity.ok(orderService.getParsedParagraph(orderType));
    }

    @GetMapping("/order-type")
    public ResponseEntity<List<String>> getAvailableOrderTypes() {
        return ResponseEntity.ok(Arrays.stream(OrderType.values())
                .map(Enum::toString)
                .collect(Collectors.toList()));
    }
}