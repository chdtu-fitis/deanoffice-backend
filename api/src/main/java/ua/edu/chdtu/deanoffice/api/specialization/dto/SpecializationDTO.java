package ua.edu.chdtu.deanoffice.api.specialization.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.DegreeDTO;
import ua.edu.chdtu.deanoffice.api.general.DepartmentDTO;
import ua.edu.chdtu.deanoffice.api.speciality.dto.SpecialityDTO;

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
    private SpecialityDTO speciality;
    @JsonView(SpecializationView.WithDegreeAndSpeciality.class)
    private DegreeDTO degree;
    private DepartmentDTO department;
    private String qualification;
    private String qualificationEng;
    private BigDecimal paymentFulltime;
    private BigDecimal paymentExtramural;
    private String educationalProgramHeadName;
    private String educationalProgramHeadNameEng;
    private String educationalProgramHeadInfo;
    private String educationalProgramHeadInfoEng;
    private BigDecimal requiredCredits;
    private String knowledgeAndUnderstandingOutcomes;
    private String knowledgeAndUnderstandingOutcomesEng;
    private String applyingKnowledgeAndUnderstandingOutcomes;
    private String applyingKnowledgeAndUnderstandingOutcomesEng;
    private String makingJudgementsOutcomes;
    private String makingJudgementsOutcomesEng;
}
