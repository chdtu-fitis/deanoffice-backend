package ua.edu.chdtu.deanoffice.api.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.document.DocumentResponseController;
import ua.edu.chdtu.deanoffice.api.order.paragraphdto.StudentExpelParagraphDtoV1;
import ua.edu.chdtu.deanoffice.api.student.StudentExpelController;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentExpelDTO;
import ua.edu.chdtu.deanoffice.entity.order.OrderSerializedData;
import ua.edu.chdtu.deanoffice.entity.order.OrderType;
import ua.edu.chdtu.deanoffice.service.order.OrderDtoSerializationService;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController extends DocumentResponseController {

    private final OrderService orderService;
    private final OrderDtoSerializationService orderDtoSerializationService;
    private final StudentExpelController studentExpelController;
    private final ObjectMapper objectMapper;

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
                return ResponseEntity.ok(new StudentExpelParagraphDtoV1(orderService.getParagraphByOrderType(orderType)));
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