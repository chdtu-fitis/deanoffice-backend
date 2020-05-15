package ua.edu.chdtu.deanoffice.entity.order;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;
import javax.persistence.Entity;
import java.util.Date;

@Getter
@Setter
@Entity
public class LegalBasis extends BaseEntity {
    private String legalBasisText;
    private Date introducedOn;
    private boolean active;
}
