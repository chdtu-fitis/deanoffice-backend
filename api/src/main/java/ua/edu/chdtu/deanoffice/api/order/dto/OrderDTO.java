package ua.edu.chdtu.deanoffice.api.order.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
public class OrderDTO {
    private int id;
    private Date orderDate;
    private String orderNumber;
    private OrderApproveTemplateDTO orderApproveTemplate;
    private String comment;
    private boolean active;
    private boolean signed;
}
