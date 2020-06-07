package ua.edu.chdtu.deanoffice.entity.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "order_serialized_data")
public class OrderSerializedData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer orderId;
    private OrderType orderType;
    private String orderDtoName;
    private String data;
    private Boolean deserialized = false;

    public OrderSerializedData(Integer orderId, OrderType orderType, String orderDtoName, String data) {
        this.orderId = orderId;
        this.orderType = orderType;
        this.data = data;
        this.orderDtoName = orderDtoName;
    }
}
