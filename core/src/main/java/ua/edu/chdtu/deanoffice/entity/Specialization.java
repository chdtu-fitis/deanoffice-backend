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

    public Specialization() {
        educationalProgramHeadName = "";
        educationalProgramHeadNameEng = "";
        educationalProgramHeadInfo = "";
        educationalProgramHeadInfoEng = "";
    }
}
