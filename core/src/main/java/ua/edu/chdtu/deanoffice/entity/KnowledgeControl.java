package ua.edu.chdtu.deanoffice.entity;

import ua.edu.chdtu.deanoffice.entity.superclasses.NameWithEngEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="knowledge_control")
public class KnowledgeControl extends NameWithEngEntity {
    @Column(name="has_grade", nullable = false)
    private boolean hasGrade;

    public boolean isHasGrade() {
        return hasGrade;
    }

    public void setHasGrade(boolean hasGrade) {
        this.hasGrade = hasGrade;
    }
}
