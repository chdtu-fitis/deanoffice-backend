package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.NameWithEngEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Table(name = "knowledge_control")
public class KnowledgeControl extends NameWithEngEntity {
    @Column(name = "has_grade", nullable = false)
    private boolean hasGrade;
}
