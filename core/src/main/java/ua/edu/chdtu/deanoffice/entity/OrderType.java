package ua.edu.chdtu.deanoffice.entity;

public enum OrderType {
    STUDENT_EXPEL("Про відрахування"),
    ACADEMIC_VACATION("Про надання академвідпустки");

    private final String nameUkr;

    OrderType(String nameUkr) {
        this.nameUkr = nameUkr;
        DegreeEnum a = DegreeEnum.BACHELOR;
    }

    public String getNameUkr() {
        return nameUkr;
    }
}
