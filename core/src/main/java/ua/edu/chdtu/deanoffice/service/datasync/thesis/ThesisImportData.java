package ua.edu.chdtu.deanoffice.service.datasync.thesis;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ThesisImportData {
    private String studentFullName;
    private String groupName;
    private String thesisName;
    private String thesisNameEng;
    private String fullSupervisorName;

    public ThesisImportData(){
        studentFullName = "";
        groupName = "";
        thesisName = "";
        thesisNameEng = "";
        fullSupervisorName = "";
    }
}
