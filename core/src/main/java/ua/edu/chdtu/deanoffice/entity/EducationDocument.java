package ua.edu.chdtu.deanoffice.entity;

public enum EducationDocument {
    SECONDARY_SCHOOL_CERTIFICATE(1, "Атестат про повну середню освіту", "Certificate of Full Secondary Education"),
    JUNIOR_BACHELOR_DIPLOMA(2, "Диплом молодшого спеціаліста", "Junior Specialist diploma"),
    BACHELOR_DIPLOMA(3, "Диплом бакалавра", "Bachelor diploma"),
    MASTER_DIPLOMA(4, "Диплом магістра", "Master diploma"),
    PHD_DIPLOMA(5, "Диплом доктора філософії", "PHD diploma"),
    DOCTOR_DIPLOMA(6, "Диплом доктора наук", "Doctor diploma"),
    OTHER_FOREIGN(7, "Іноземний документ", "Foreign document"),
    QUALIFIED_WORKER_DIPLOMA(8, "Диплом кваліфікованого робітника", "Qualified Worker diploma"),
    WORKER_QUALIFICATION_DIPLOMA(9, "Свідоцтво про присвоєння(підвищення) робітничої кваліфікації", "Certificate of Appointment (Upgrade) of the Worker Qualification"),
    SPECIALIST_DIPLOMA (10, "Диплом спеціаліста", "Specialist diploma");

    private final int id;
    private final String nameUkr;
    private final String nameEng;

    EducationDocument(int id, String nameUkr, String nameEng) {
        this.id = id;
        this.nameUkr = nameUkr;
        this.nameEng = nameEng;
    }

    public static boolean isExist(EducationDocument document) {
        if (document != null) {
            return checkDocumentExist(document);
        } else {
            return false;
        }
    }

    private static boolean checkDocumentExist(EducationDocument document) {
        String eduDoc = document.toString();
        for (EducationDocument educationDocument : values()) {
            if (eduDoc.toLowerCase().equals(educationDocument.toString().toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNotExist(EducationDocument document) {
        return !isExist(document);
    }

    public static EducationDocument getPreviousDiplomaType(Integer degreeId) {
        switch (degreeId) {
            case 1:
                return SECONDARY_SCHOOL_CERTIFICATE;
            case 2:
                return JUNIOR_BACHELOR_DIPLOMA;
            case 3:
                return BACHELOR_DIPLOMA;
            default:
                return SECONDARY_SCHOOL_CERTIFICATE;
        }
    }

    public int getId() {
        return id;
    }

    public String getNameEng() {
        return nameEng;
    }

    public String getNameUkr() {
        return nameUkr;
    }
}
