package ua.edu.chdtu.deanoffice.entity.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_template_version")
public class OrderTemplateVersion {

    @Id
    private Integer id;
    @Column(name = "db_table_name")
    private String dbTableName;
    @Column(name = "template_name")
    private String templateName;
    @Column(name = "paragraph_template")
    private String paragraphTemplate;
    @Column(name = "introduced_on")
    private Date introducedOn;
    @Column(name = "active")
    private Boolean active;
}
