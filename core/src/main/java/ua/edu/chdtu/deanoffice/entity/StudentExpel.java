package ua.edu.chdtu.deanoffice.entity;

import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name="student_expel")
public class StudentExpel extends BaseEntity {
    @ManyToOne
    private Student student;
    @Column(name="expel_date", nullable = false)
    private Date expelDate;
    @Column(name="order_number", nullable = false, length = 15)
    private String orderNumber;
    @Column(name="order_date", nullable = false)
    private Date orderDate;
    @ManyToOne
    private StudentGroup group;
    @ManyToOne
    private OrderReason reason;
    @Column(name="application_date", nullable = false)
    private Date applicationDate;

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Date getExpelDate() {
        return expelDate;
    }

    public void setExpelDate(Date expelDate) {
        this.expelDate = expelDate;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public StudentGroup getGroup() {
        return group;
    }

    public void setGroup(StudentGroup group) {
        this.group = group;
    }

    public OrderReason getReason() {
        return reason;
    }

    public void setReason(OrderReason reason) {
        this.reason = reason;
    }

    public Date getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(Date applicationDate) {
        this.applicationDate = applicationDate;
    }
}
