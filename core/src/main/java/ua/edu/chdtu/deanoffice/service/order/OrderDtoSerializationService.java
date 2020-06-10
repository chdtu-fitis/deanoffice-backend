package ua.edu.chdtu.deanoffice.service.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.order.OrderSerializedData;
import ua.edu.chdtu.deanoffice.entity.order.OrderType;
import ua.edu.chdtu.deanoffice.repository.order.OrderDtoSerializedRepository;

@RequiredArgsConstructor

@Service
public class OrderDtoSerializationService {

    private final OrderDtoSerializedRepository orderDtoSerializedRepository;

    public void serializeOrderDto(Integer orderId, OrderType orderType, String dtoType, String dtoJson) {
        orderDtoSerializedRepository.save(new OrderSerializedData(orderId, orderType, dtoType, dtoJson));
    }

    public OrderSerializedData orderSerializedData(Integer id) {
        return orderDtoSerializedRepository.findByOrderId(id);
    }

    public void setOrderSerializedDataDeserialized(OrderSerializedData orderSerializedData){
        orderSerializedData.setDeserialized(true);
        orderDtoSerializedRepository.save(orderSerializedData);
    }
}
