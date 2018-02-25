package ua.edu.chdtu.deanoffice.api.studentDegree.dto;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.Department;
import ua.edu.chdtu.deanoffice.entity.Faculty;

import java.math.BigDecimal;

@Getter
@Setter
public class SpecializationDTO {
    private SpecialityDTO speciality;
    private Faculty faculty;
    private Department department;
    private String qualification;
    private BigDecimal paymentFulltime;
    private BigDecimal paymentExtramural;
    private String educationalProgramHeadName;
    private String educationalProgramHeadInfo;
    private BigDecimal requiredCredits;
    private String knowledgeAndUnderstandingOutcomes;
    private String applyingKnowledgeAndUnderstandingOutcomes;
    private String makingJudgementsOutcomes;
    private boolean active;
    private String name;
    private Integer id;
}
