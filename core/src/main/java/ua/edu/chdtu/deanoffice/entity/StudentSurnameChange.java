package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
public class StudentSurnameChange extends BaseEntity {
    @Column(nullable = false)
    Date orderDate;

    @Column(nullable = false)
    String orderNumber;

    @ManyToOne
    Faculty faculty;

    @ManyToOne
    StudentDegree studentDegree;

    @Column(nullable = false)
    Date surnameChangeDate;

    @Column(nullable = false)
    String specialityName;

    @Column
    String facultyName;

    @Column(nullable = false)
    String specializationName;

    @Column
    Integer studentYear;

    @Column(nullable = false)
    String studentGroupName;

    @Column(nullable = false)
    String tuitionForm;

    @Column(nullable = false)
    String payment;

    @Column(nullable = false)
    Date applicationDate;

    @Column(nullable = false)
    String applicationBasedOn;

    @Column(nullable = false)
    String oldSurname;

    @Column(nullable = false)
    String newSurname;
}
