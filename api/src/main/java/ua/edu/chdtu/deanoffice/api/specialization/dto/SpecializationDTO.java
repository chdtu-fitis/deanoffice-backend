package ua.edu.chdtu.deanoffice.api.specialization.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.dto.NamedDTO;

import java.math.BigDecimal;

@Getter
@Setter
public class SpecializationDTO {
    @JsonView(SpecializationView.Basic.class)
    private int id;
    @JsonView(SpecializationView.Basic.class)
    private String name;
    private String nameEng;
    private boolean active;

    @JsonView(SpecializationView.WithDegreeAndSpeciality.class)
    private NamedDTO speciality;
    @JsonView(SpecializationView.WithDegreeAndSpeciality.class)
    private NamedDTO degree;
    @JsonView(SpecializationView.Extend.class)
    private NamedDTO department;
    private String qualification;
    private String qualificationEng;
    @JsonView(SpecializationView.Extend.class)
    private BigDecimal paymentFulltime;
    @JsonView(SpecializationView.Extend.class)
    private BigDecimal paymentExtramural;
    @JsonView(SpecializationView.Extend.class)
    private String educationalProgramHeadName;
    @JsonView(SpecializationView.Extend.class)
    private String educationalProgramHeadNameEng;
    private String educationalProgramHeadInfo;
    private String educationalProgramHeadInfoEng;
    private String knowledgeAndUnderstandingOutcomes;
    private String knowledgeAndUnderstandingOutcomesEng;
    private String applyingKnowledgeAndUnderstandingOutcomes;
    private String applyingKnowledgeAndUnderstandingOutcomesEng;
    private String makingJudgementsOutcomes;
    private String makingJudgementsOutcomesEng;
}
