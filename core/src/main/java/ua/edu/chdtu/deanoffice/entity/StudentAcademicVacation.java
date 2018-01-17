package ua.edu.chdtu.deanoffice.entity;

import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name="student_academic_vacation")
public class StudentAcademicVacation extends BaseEntity {
    @ManyToOne
    private Student student;
    @Column(name="vacation_start_date", nullable = false)
    private Date vacationStartDate;
    @Column(name="vacation_end_date", nullable = false)
    private Date vacationEndDate;
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

    public Date getVacationStartDate() {
        return vacationStartDate;
    }

    public void setVacationStartDate(Date vacationStartDate) {
        this.vacationStartDate = vacationStartDate;
    }

    public Date getVacationEndDate() {
        return vacationEndDate;
    }

    public void setVacationEndDate(Date vacationEndDate) {
        this.vacationEndDate = vacationEndDate;
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
