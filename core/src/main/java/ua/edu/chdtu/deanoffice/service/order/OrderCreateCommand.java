package ua.edu.chdtu.deanoffice.service.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.order.OrderType;

import javax.validation.constraints.NotNull;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderCreateCommand {
    @NotNull
    private OrderType orderType;
    @NotNull
    private String orderNumber;
    @NotNull
    private Date orderDate;
    private Integer facultyId;
}
