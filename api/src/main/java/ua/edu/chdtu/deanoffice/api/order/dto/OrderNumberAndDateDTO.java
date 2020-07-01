package ua.edu.chdtu.deanoffice.api.order.dto;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Getter
@Setter
public class OrderNumberAndDateDTO {
    @NotNull
    @Size(min=3, max=15)
    private String orderNumber;
    @NotNull
    private Date orderDate;
}
