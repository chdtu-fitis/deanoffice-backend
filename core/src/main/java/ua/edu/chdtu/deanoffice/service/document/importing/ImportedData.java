package ua.edu.chdtu.deanoffice.service.document.importing;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class ImportedData {
    private String firstName;
    private String lastName;
    private String middleName;
    private String firstNameEn;
    private String lastNameEn;
    private String middleNameEn;
    private String birthday;
    private String educationId;
    private String personsSexName;
    private String educationDateBegin;
    private String educationDateEnd;
    private String facultyName;
    private String qualificationGroupName;
    private String baseQualificationName;
    private String educationFormName;
    private String personEducationPaymentTypeName;
    private String fullSpecialityName;
    private String fullSpecializationName;
    private String programName;
    private String programNameEn;
    private String personSexId;
    private String countryId;
    private String ipnNumber;
    private String personDocumentTypeId;
    private String documentSeries;
    private String documentNumbers;
    private String documentDateGet;
    private String documentIssued;
    private String eduDocInfo;
    private String universityFrom;
    private String refillInfo;
    private String eduFromInfo;
    private String personDocumentType;
    private String personDocumentTypeName;
    private String documentSeries2;
    private String documentNumbers2;
    private String documentDateGet2;
    private String documentIssued2;

    ImportedData() {
        this.firstName = "";
        this.lastName = "";
        this.middleName = "";
        this.firstNameEn = "";
        this.lastNameEn = "";
        this.middleNameEn = "";
        this.birthday = "";
        this.educationId = "";
        this.personsSexName = "";
        this.educationDateBegin = "";
        this.educationDateEnd = "";
        this.facultyName = "";
        this.qualificationGroupName = "";
        this.baseQualificationName = "";
        this.educationFormName = "";
        this.personEducationPaymentTypeName = "";
        this.fullSpecialityName = "";
        this.fullSpecializationName = "";
        this.programName = "";
        this.programNameEn = "";
        this.personSexId = "";
        this.countryId = "";
        this.ipnNumber = "";
        this.personDocumentTypeId = "";
        this.documentSeries = "";
        this.documentNumbers = "";
        this.documentDateGet = "";
        this.documentIssued = "";
        this.eduDocInfo = "";
        this.universityFrom = "";
        this.refillInfo = "";
        this.eduFromInfo = "";
        this.personDocumentType = "";
        this.personDocumentTypeName = "";
        this.documentSeries2 = "";
        this.documentNumbers2 = "";
        this.documentDateGet2 = "";
        this.documentIssued2 = "";
    }
}
