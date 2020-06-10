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
public class OrderUpdateCommand {
    @NotNull
    private OrderType orderType;
    @NotNull
    private String orderNumber;
    @NotNull
    private Date orderDate;
    @NotNull
    private Integer orderApproveTemplateId;
    private String orderComment;
    @NotNull
    private Integer facultyId;
    @NotNull
    private String paragraph;
    @NotNull
    private Integer orderReason;
}
