package ua.edu.chdtu.deanoffice.entity.order;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Getter
@Setter
@Entity
public class OrderTemplateVersion extends BaseEntity {
    private String dbTableName;
    private String templateName;
    private String paragraphTemplate;
    @Temporal(TemporalType.DATE)
    private Date introducedOn;
    private boolean active;
}
