package ua.edu.chdtu.deanoffice.api.student.synchronization.thesis.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ThesisDataForSaveDTO {
    int studentDegreeId;
    private String thesisName;
    private String thesisNameEng;
    private String thesisSupervisor;
}
