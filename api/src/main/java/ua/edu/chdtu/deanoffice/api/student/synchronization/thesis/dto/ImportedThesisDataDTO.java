package ua.edu.chdtu.deanoffice.api.student.synchronization.thesis.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImportedThesisDataDTO {
    private int id;
    private String fullName;
    private String thesisName;
    private  String thesisNameEng;
    private String oldThesisName;
    private String fullSupervisorName;
}
