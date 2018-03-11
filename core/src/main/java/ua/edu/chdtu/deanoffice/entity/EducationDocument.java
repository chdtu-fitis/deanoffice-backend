package ua.edu.chdtu.deanoffice.entity;

public enum EducationDocument {
    SECONDARY_SCHOOL_CERTIFICATE(1),
    JUNIOR_BACHELOR_DIPLOMA(2),
    BACHELOR_DIPLOMA(3),
    MASTER_DIPLOMA(4),
    PHD_DIPLOMA(5),
    DOCTOR_DIPLOMA(6),
    OTHER_FOREIGN(7);

    final int code;

    EducationDocument(int code) {
        this.code = code;
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

    public int getCode() {
        return code;
    }

    public String getUkrainianName() {
        switch (this) {
            case SECONDARY_SCHOOL_CERTIFICATE:
                return "Атестат про повну середню освіту";
            case JUNIOR_BACHELOR_DIPLOMA:
                return "Диплом молодшого бакалавра";
            case BACHELOR_DIPLOMA:
                return "Диплом бакалавра";
            case MASTER_DIPLOMA:
                return "Диплом магістра";
            case PHD_DIPLOMA:
                return "Диплом доктора філософії";
            case DOCTOR_DIPLOMA:
                return "Диплом доктора наук";
            case OTHER_FOREIGN:
                return "Іноземний документ";
            default:
                return "Документ";
        }
    }

    public String getEnglishName() {
        switch (this) {
            case SECONDARY_SCHOOL_CERTIFICATE:
                return "Secondary School Leaving Certificate";
            case JUNIOR_BACHELOR_DIPLOMA:
                return "Junior Bachelor diploma";
            case BACHELOR_DIPLOMA:
                return "Bachelor diploma";
            case MASTER_DIPLOMA:
                return "Master diploma";
            case PHD_DIPLOMA:
                return "PhD diploma";
            case DOCTOR_DIPLOMA:
                return "Doctor diploma";
            case OTHER_FOREIGN:
                return "Foreign document";
            default:
                return "Document";
        }
    }
}
