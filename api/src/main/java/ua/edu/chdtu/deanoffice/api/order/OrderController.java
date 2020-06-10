package ua.edu.chdtu.deanoffice.api.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.api.order.paragraphdto.StudentExpelParagraphDto;
import ua.edu.chdtu.deanoffice.api.student.StudentExpelController;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentExpelDTO;
import ua.edu.chdtu.deanoffice.entity.order.Order;
import ua.edu.chdtu.deanoffice.entity.order.OrderSerializedData;
import ua.edu.chdtu.deanoffice.entity.order.OrderType;
import ua.edu.chdtu.deanoffice.service.document.DocumentIOService;
import ua.edu.chdtu.deanoffice.service.document.FileFormatEnum;
import ua.edu.chdtu.deanoffice.service.document.TemplateUtil;
import ua.edu.chdtu.deanoffice.service.order.OrderDtoSerializationService;

import javax.validation.Valid;
import java.io.FileNotFoundException;
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
    private final OrderDtoSerializationService orderDtoSerializationService;
    private final StudentExpelController studentExpelController;
    private final ObjectMapper objectMapper;
    private final DocumentIOService documentIOService;

    @GetMapping("/{orderId}/document")
    public ResponseEntity<Resource> generateDocument(@PathVariable Integer orderId)
            throws Docx4JException, FileNotFoundException {
        Order order = orderService.getOrderById(orderId);
        String orderName = order.getOrderTemplateVersion().getDbTableName() + " " + order.getOrderNumber();
        WordprocessingMLPackage orderDocument = documentIOService.loadTemplate(DocumentIOService.ORDERS_PATH +
                order.getOrderTemplateVersion().getTemplateName());
        Map<String, String> placeholderValue = new HashMap<>();
        placeholderValue.put("PHParagraph", order.getOrderParagraph());
        placeholderValue.put("PHOrderDate", order.getOrderDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString());
        placeholderValue.put("PHNumber", order.getOrderNumber());
        TemplateUtil.replaceTextPlaceholdersInTemplate(orderDocument, placeholderValue);
        return buildDocumentResponseEntity(documentIOService.saveDocumentToTemp(orderDocument, orderName, FileFormatEnum.DOCX),
                orderName, MEDIA_TYPE_DOCX);
    }

    @PutMapping("/{orderId}/student-expel")
    public void saveStudentExpelOperation(@PathVariable Integer orderId, @RequestBody StudentExpelDTO studentExpelDTO) throws IOException {
        orderDtoSerializationService.serializeOrderDto(orderId, OrderType.STUDENT_EXPEL,
                studentExpelDTO.getClass().toString(),
                objectMapper.writeValueAsString(studentExpelDTO));
    }

    @PutMapping
    public ResponseEntity<Integer> saveOrder(@RequestBody @Valid OrderCreateCommand orderCreateCommand) {
        return ResponseEntity.ok(orderService.createOrder(orderCreateCommand));
    }

    @PostMapping("/student-expel/{orderId}")
    public ResponseEntity applyStudentExpelOrder(@PathVariable Integer orderId) throws IOException {
        OrderSerializedData orderSerializedData = orderDtoSerializationService.orderSerializedData(orderId);
        ResponseEntity responseEntity = studentExpelController
                .expelStudent(getJsonDtoByOrderSerializedData(orderSerializedData, StudentExpelDTO.class));
        if (responseEntity.getStatusCode() == HttpStatus.OK)
            orderDtoSerializationService.setOrderSerializedDataDeserialized(orderSerializedData);
        return responseEntity;
    }

    public <T> T getJsonDtoByOrderSerializedData(OrderSerializedData orderSerializedData, Class<T> className) throws IOException {
        return objectMapper.readValue(orderSerializedData.getData(), className);
    }

    @GetMapping("/paragraph")
    public ResponseEntity getParagraphByOrderType(OrderType orderType) {
        switch (orderType) {
            case STUDENT_EXPEL:
                return ResponseEntity.ok(new StudentExpelParagraphDto(orderService.getParagraphByOrderType(orderType)));
            default:
                return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/order-type")
    public ResponseEntity<List<String>> getAvailableOrderTypes() {
        return ResponseEntity.ok(Arrays.stream(OrderType.values())
                .map(Enum::toString)
                .collect(Collectors.toList()));
    }
}