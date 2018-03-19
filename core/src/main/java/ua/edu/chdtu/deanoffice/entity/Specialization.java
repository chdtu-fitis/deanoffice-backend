package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.NameWithEngAndActiveEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

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
    @Column(name = "qualification", length = 100)
    private String qualification;
    @Column(name = "qualification_eng", length = 100)
    private String qualificationEng;
    @Column(name = "payment_fulltime", precision = 15, scale = 2)
    private BigDecimal paymentFulltime;
    @Column(name = "payment_extramural", precision = 15, scale = 2)
    private BigDecimal paymentExtramural;
    @Column(name = "program_head_name", nullable = false)
    private String educationalProgramHeadName;
    @Column(name = "program_head_name_eng", nullable = false)
    private String educationalProgramHeadNameEng;
    @Column(name = "program_head_info", nullable = false)
    private String educationalProgramHeadInfo;
    @Column(name = "program_head_info_eng", nullable = false)
    private String educationalProgramHeadInfoEng;
    @Column(name = "required_credits", precision = 4, scale = 1)
    private BigDecimal requiredCredits;
    @Column(name = "knowledge_and_understanding_outcomes", columnDefinition = "character varying(1200)", length = 1200)
    private String knowledgeAndUnderstandingOutcomes;
    @Column(name = "knowledge_and_understanding_outcomes_eng", columnDefinition = "character varying(1200)", length = 1200)
    private String knowledgeAndUnderstandingOutcomesEng;
    @Column(name = "applying_knowledge_and_understanding_outcomes", columnDefinition = "character varying(1200)", length = 1200)
    private String applyingKnowledgeAndUnderstandingOutcomes;
    @Column(name = "applying_knowledge_and_understanding_outcomes_eng", columnDefinition = "character varying(1200)", length = 1200)
    private String applyingKnowledgeAndUnderstandingOutcomesEng;
    @Column(name = "making_judgements_outcomes", columnDefinition = "character varying(1200)", length = 1200)
    private String makingJudgementsOutcomes;
    @Column(name = "making_judgements_outcomes_eng", columnDefinition = "character varying(1200)", length = 1200)
    private String makingJudgementsOutcomesEng;

    public Specialization() {
        educationalProgramHeadName = "";
        educationalProgramHeadNameEng = "";
        educationalProgramHeadInfo = "";
        educationalProgramHeadInfoEng = "";
    }
}
