package ua.edu.chdtu.deanoffice.entity.order;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Getter
@Setter
@Entity
public class OrderTemplateVersion {
    @Id
    private Integer id;
    private String dbTableName;
    private String templateName;
    private String paragraphTemplate;
    @Temporal(TemporalType.DATE)
    private Date introducedOn;
    private Boolean active;
}
