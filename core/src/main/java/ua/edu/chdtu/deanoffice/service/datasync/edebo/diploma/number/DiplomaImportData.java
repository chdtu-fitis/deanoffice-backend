package ua.edu.chdtu.deanoffice.service.datasync.edebo.diploma.number;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiplomaImportData {
    private String firstName;
    private String lastName;
    private String middleName;
    private String firstNameEn;
    private String lastNameEn;
    private String middleNameEn;
    private String specialityName;
    private String facultyName;
    private String documentSeries;
    private String documentNumber;
    private String awardTypeId;
    private String educationId;

    DiplomaImportData(){
        firstName = "";
        lastName = "";
        middleName = "";
        firstNameEn = "";
        lastNameEn = "";
        middleNameEn = "";
        specialityName = "";
        facultyName = "";
        documentSeries = "";
        documentNumber = "";
        awardTypeId = "";
        educationId = "";
    }
}
