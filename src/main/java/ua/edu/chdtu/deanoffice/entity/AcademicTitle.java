package ua.edu.chdtu.deanoffice.entity;

public enum AcademicTitle {
    PROFESSOR("професор", "Professor"),
    DOZENT("доцент", "Associate Professor");

    private final String nameUkr;
    private final String nameEng;

    AcademicTitle(String nameUkr, String nameEng) {
        this.nameUkr = nameUkr;
        this.nameEng = nameEng;
    }

    public String getNameUkr() {
        return nameUkr;
    }
}
