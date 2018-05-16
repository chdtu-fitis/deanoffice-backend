package ua.edu.chdtu.deanoffice.entity;

public enum DegreeEnum {
    BACHELOR(1, "Бакалавр", "Specialist"),
    SPECIALIST(2, "Спеціаліст", "Specialist"),
    MASTER(3, "Магістр", "Master");

    private final int id;
    private final String nameUkr;
    private final String nameEng;

    DegreeEnum(int id, String nameUkr, String nameEng) {
        this.id = id;
        this.nameUkr = nameUkr;
        this.nameEng = nameEng;
    }

    private static boolean checkDegreeExist(DegreeEnum degree) {
        String eduDoc = degree.toString();
        for (DegreeEnum degreeEnum : values()) {
            if (eduDoc.toLowerCase().equals(degreeEnum.toString().toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isExist(DegreeEnum degree) {
        if (degree != null) {
            return checkDegreeExist(degree);
        } else {
            return false;
        }
    }

    public static boolean isNotExist(DegreeEnum degree) {
        return !isExist(degree);
    }

    public static DegreeEnum getDegree(Integer degreeId) {
        switch (degreeId) {
            case 1:
                return BACHELOR;
            case 2:
                return SPECIALIST;
            case 3:
                return MASTER;
            default:
                return BACHELOR;
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
