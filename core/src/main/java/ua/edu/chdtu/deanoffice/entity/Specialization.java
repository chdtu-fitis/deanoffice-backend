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
    private String qualification;
    private String qualificationEng;
    private BigDecimal paymentFulltime;
    private BigDecimal paymentExtramural;
    @Column(name = "program_head_name", nullable = false)
    private String educationalProgramHeadName;
    @Column(name = "program_head_name_eng", nullable = false)
    private String educationalProgramHeadNameEng;
    @Column(name = "program_head_info", nullable = false)
    private String educationalProgramHeadInfo;
    @Column(name = "program_head_info_eng", nullable = false)
    private String educationalProgramHeadInfoEng;
    private String knowledgeAndUnderstandingOutcomes;
    private String knowledgeAndUnderstandingOutcomesEng;
    private String applyingKnowledgeAndUnderstandingOutcomes;
    private String applyingKnowledgeAndUnderstandingOutcomesEng;
    private String makingJudgementsOutcomes;
    private String makingJudgementsOutcomesEng;
    @Column(name = "certificate_number", nullable = false)
    private String certificateNumber;
    @Temporal(TemporalType.DATE)
    @Column(name = "certificate_date", nullable = false)
    private Date certificateDate;

    public Specialization() {
        educationalProgramHeadName = "";
        educationalProgramHeadNameEng = "";
        educationalProgramHeadInfo = "";
        educationalProgramHeadInfoEng = "";
        certificateNumber = "";
        try {
            certificateDate = new SimpleDateFormat("dd.MM.yyyy").parse("01.01.1980");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
