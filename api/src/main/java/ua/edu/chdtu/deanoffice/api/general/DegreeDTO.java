package ua.edu.chdtu.deanoffice.api.general;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.dto.GeneralView;

@Getter
@Setter
public class DegreeDTO {
    @JsonView(GeneralView.BasicDegree.class)
    private int id;
    @JsonView(GeneralView.BasicDegree.class)
    private String name;
    private String nameEng;
    private String qualificationLevelDescription;
    private String qualificationLevelDescriptionEng;
    private String admissionRequirements;
    private String admissionRequirementsEng;
    private String furtherStudyAccess;
    private String furtherStudyAccessEng;
    private String professionalStatus;
    private String professionalStatusEng;
}
