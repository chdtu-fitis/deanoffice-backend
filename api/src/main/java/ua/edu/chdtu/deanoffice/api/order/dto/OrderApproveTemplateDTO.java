package ua.edu.chdtu.deanoffice.api.order.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderApproveTemplateDTO {
    private int id;
    private OrderApproverDTO mainApprover;
    private OrderApproverDTO[] approvers;
    private OrderApproverDTO initiatorApprover;
    private boolean active;
}
