package ua.edu.chdtu.deanoffice.service.datasync.thesis;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ThesisImportData {
    private String lastName;
    private String firstName;
    private String middleName;
    private String groupName;
    private String thesisName;
    private String thesisNameEng;
    private String fullSupervisorName;

    public ThesisImportData(){
        lastName = "";
        firstName = "";
        middleName = "";
        groupName = "";
        thesisName = "";
        thesisNameEng = "";
        fullSupervisorName = "";
    }
}
