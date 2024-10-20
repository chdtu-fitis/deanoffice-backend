package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
public class SelectiveCourse extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private Course course;
    @ManyToOne(fetch = FetchType.LAZY)
    private Teacher teacher;
    @ManyToOne(fetch = FetchType.LAZY)
    private Degree degree;
    @ManyToOne(fetch = FetchType.LAZY)
    private Department department;
    @ManyToOne(fetch = FetchType.LAZY)
    private FieldOfKnowledge fieldOfKnowledge;
    private String otherFieldsOfKnowledge;
    @Enumerated(value = EnumType.STRING)
    private TrainingCycle trainingCycle;
    private String description;
    private int studyYear;
    private boolean available;
    private String groupName;
}
