package ua.edu.chdtu.deanoffice.service.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderCreateCommand {
    @NotNull
    private String orderType;
    @NotNull
    private String orderNumber;
    @NotNull
    private Date orderDate;
    private Integer facultyId;
}
