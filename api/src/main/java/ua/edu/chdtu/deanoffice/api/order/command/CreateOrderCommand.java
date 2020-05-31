package ua.edu.chdtu.deanoffice.api.order.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderCommand {
    private String orderTemplateName;
    private String orderNumber;
    private String orderDate;
    private String paragraph;
    private String fileName;
}
