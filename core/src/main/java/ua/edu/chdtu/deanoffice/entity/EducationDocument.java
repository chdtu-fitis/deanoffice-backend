package ua.edu.chdtu.deanoffice.entity;

public enum EducationDocument {
    SECONDARY_SCHOOL_CERTIFICATE(1, "Атестат про повну середню освіту", "Secondary School Leaving Certificate"),
    JUNIOR_BACHELOR_DIPLOMA(2, "Диплом молодшого бакалавра", "Junior Bachelor diploma"),
    BACHELOR_DIPLOMA(3, "Диплом бакалавра", "Bachelor diploma"),
    MASTER_DIPLOMA(4, "Диплом магістра", "Master diploma"),
    PHD_DIPLOMA(5, "Диплом доктора філософії", "PHD diploma"),
    DOCTOR_DIPLOMA(6, "Диплом доктора наук", "Doctor diploma"),
    OTHER_FOREIGN(7, "Іноземний документ", "Foreign document");

    final int code;
    final String nameUkr;
    final String nameEng;

    EducationDocument(int code, String nameUkr, String nameEng) {
        this.code = code;
        this.nameUkr = nameUkr;
        this.nameEng = nameEng;
    }

    public static boolean isExist(EducationDocument document) {
        String eduDoc = document.toString();
        for (EducationDocument educationDocument: values()) {
            if (eduDoc.toLowerCase().equals(educationDocument.toString().toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public static EducationDocument getPreviousDiplomaType(Integer degreeId) {
        switch (degreeId) {
            case 1: case 2: return SECONDARY_SCHOOL_CERTIFICATE;
            case 3: return BACHELOR_DIPLOMA;
            default: return SECONDARY_SCHOOL_CERTIFICATE;
        }
    }
}
