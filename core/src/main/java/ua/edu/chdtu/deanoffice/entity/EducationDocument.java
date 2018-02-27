package ua.edu.chdtu.deanoffice.entity;

public enum EducationDocument {
    SECONDARY_SCHOOL_CERTIFICATE, JUNIOR_BACHELOR_DIPLOMA, BACHELOR_DIPLOMA, MASTER_DIPLOMA, PHD_DIPLOMA, DOCTOR_DIPLOMA, OTHER_FOREIGN;

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

    @Override
    public String toString() {
        return getEnglishName();
    }
}
