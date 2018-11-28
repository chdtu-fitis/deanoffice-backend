package ua.edu.chdtu.deanoffice.api.student.synchronization.thesis;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImportedThesisDataDTO {
    int id;
    String fullName;
    String thesisName;
    String thesisNameEng;
    String oldThesisName;
    String fullSupervisorName;
}
