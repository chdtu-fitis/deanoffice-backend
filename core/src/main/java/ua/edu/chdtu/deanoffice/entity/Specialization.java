package ua.edu.chdtu.deanoffice.entity;

import ua.edu.chdtu.deanoffice.entity.superclasses.NameWithEngAndActiveEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

@Entity
public class Specialization extends NameWithEngAndActiveEntity {
    @ManyToOne
    private Speciality speciality;
    @ManyToOne
    private Degree degree;
    @ManyToOne
    private Faculty faculty;
    @ManyToOne
    private Department department;
    @Column(name="qualification", unique = false, length = 100)
    private String qualification;
    @Column(name="qualification_eng", unique = false, length = 100)
    private String qualificationEng;
    @Column(name="payment_fulltime", nullable = true, precision=15, scale=2)
    private BigDecimal paymentFulltime;
    @Column(name="payment_extramural", nullable = true, precision=15, scale=2)
    private BigDecimal paymentExtramural;

    public Speciality getSpeciality() {
        return speciality;
    }

    public void setSpeciality(Speciality speciality) {
        this.speciality = speciality;
    }

    public Degree getDegree() {
        return degree;
    }

    public void setDegree(Degree degree) {
        this.degree = degree;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getQualificationEng() {
        return qualificationEng;
    }

    public void setQualificationEng(String qualificationEng) {
        this.qualificationEng = qualificationEng;
    }

    public BigDecimal getPaymentFulltime() {
        return paymentFulltime;
    }

    public void setPaymentFulltime(BigDecimal paymentFulltime) {
        this.paymentFulltime = paymentFulltime;
    }

    public BigDecimal getPaymentExtramural() {
        return paymentExtramural;
    }

    public void setPaymentExtramural(BigDecimal paymentExtramural) {
        this.paymentExtramural = paymentExtramural;
    }

}
