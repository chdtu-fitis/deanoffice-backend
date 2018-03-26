package ua.edu.chdtu.deanoffice.api.general;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.dto.GeneralView;

@Getter
@Setter
public class OrderReasonDTO {
    @JsonView(GeneralView.OrderReason.class)
    private String kind;
    @JsonView(GeneralView.OrderReason.class)
    private boolean active;
    @JsonView(GeneralView.OrderReason.class)
    private String name;
    @JsonView(GeneralView.OrderReason.class)
    private int id;
}
