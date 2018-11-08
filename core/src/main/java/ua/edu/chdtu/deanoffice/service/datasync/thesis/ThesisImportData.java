package ua.edu.chdtu.deanoffice.service.datasync.thesis;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ThesisImportData {
    String lastName;
    String firstName;
    String middleName;
    String groupName;
    String thesisName;
    String thesisNameEng;
    String fullSuperVitherName;

    public ThesisImportData(){
        lastName = "";
        firstName = "";
        middleName = "";
        groupName = "";
        thesisName = "";
        thesisNameEng = "";
        fullSuperVitherName = "";
    }
}
