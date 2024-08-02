package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.NameWithEngAndActiveEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Setter
@Getter
public class Specialization extends NameWithEngAndActiveEntity {
    @ManyToOne
    private Speciality speciality;
    @ManyToOne
    private Degree degree;
    @ManyToOne
    private Faculty faculty;
    @ManyToOne
    private Department department;
    @ManyToOne
    private Teacher programHead;
    private String code;
    private BigDecimal paymentFulltime;
    private BigDecimal paymentExtramural;
    private String certificateNumber;
    @Temporal(TemporalType.DATE)
    private Date certificateDate;
    private Date certificateExpires;
    private String certificateIssuedBy;
    private String certificateIssuedByEng;
    private String specializationName;
    private String specializationNameEng;
    private int normativeCreditsNumber;
    private BigDecimal normativeTermOfStudy;

    public Specialization() {
        certificateNumber = "";
        try {
            certificateDate = new SimpleDateFormat("dd.MM.yyyy").parse("01.01.1980");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
