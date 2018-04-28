package ua.edu.chdtu.deanoffice.api.specialization.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.dto.DegreeDTO;
import ua.edu.chdtu.deanoffice.api.general.dto.DepartmentDTO;
import ua.edu.chdtu.deanoffice.api.general.dto.NamedDTO;
import ua.edu.chdtu.deanoffice.api.speciality.dto.SpecialityDTO;

import java.math.BigDecimal;

@Getter
@Setter
public class SpecializationDTO {
    @JsonView(SpecializationView.Basic.class)
    private int id;
    @JsonView(SpecializationView.Basic.class)
    private String name;
    @JsonView(SpecializationView.Extended.class)
    private String nameEng;
    @JsonView(SpecializationView.Extended.class)
    private boolean active;
    @JsonView(SpecializationView.WithDegreeAndSpeciality.class)
    private SpecialityDTO speciality;
    @JsonView(SpecializationView.WithDegreeAndSpeciality.class)
    private DegreeDTO degree;
    @JsonView(SpecializationView.Extended.class)
    private DepartmentDTO department;
    @JsonView(SpecializationView.Extended.class)
    private NamedDTO faculty;
    @JsonView(SpecializationView.Extended.class)
    private String qualification;
    @JsonView(SpecializationView.Extended.class)
    private String qualificationEng;
    @JsonView(SpecializationView.Extended.class)
    private BigDecimal paymentFulltime;
    @JsonView(SpecializationView.Extended.class)
    private BigDecimal paymentExtramural;
    @JsonView(SpecializationView.Extended.class)
    private String educationalProgramHeadName;
    @JsonView(SpecializationView.Extended.class)
    private String educationalProgramHeadNameEng;
    @JsonView(SpecializationView.Extended.class)
    private String educationalProgramHeadInfo;
    @JsonView(SpecializationView.Extended.class)
    private String educationalProgramHeadInfoEng;
    @JsonView(SpecializationView.Extended.class)
    private BigDecimal requiredCredits;
    @JsonView(SpecializationView.Extended.class)
    private String knowledgeAndUnderstandingOutcomes;
    @JsonView(SpecializationView.Extended.class)
    private String knowledgeAndUnderstandingOutcomesEng;
    @JsonView(SpecializationView.Extended.class)
    private String applyingKnowledgeAndUnderstandingOutcomes;
    @JsonView(SpecializationView.Extended.class)
    private String applyingKnowledgeAndUnderstandingOutcomesEng;
    @JsonView(SpecializationView.Extended.class)
    private String makingJudgementsOutcomes;
    @JsonView(SpecializationView.Extended.class)
    private String makingJudgementsOutcomesEng;

    private Integer specialityId;
    private Integer degreeId;
    private Integer departmentId;
}
