package ua.edu.chdtu.deanoffice.api.student.synchronization.thesis;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImportedThesisDataDTO {
    String fullName;
    String thesisName;
    String thesisNameEng;
    String oldThesisName;
}
