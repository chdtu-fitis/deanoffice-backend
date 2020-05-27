package ua.edu.chdtu.deanoffice.api.order.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
public class LegalBasisDTO {
    private int id;
    private String legalBasisText;
    private Date introducedOn;
    private boolean active;
}
