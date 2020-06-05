package ua.edu.chdtu.deanoffice.api.order.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class OrderApproveTemplateDTO {
    private int id;
    private OrderApproverDTO mainApprover;
    private List<OrderApproverDTO> approvers;
    private OrderApproverDTO initiatorApprover;
    private boolean active;
}
