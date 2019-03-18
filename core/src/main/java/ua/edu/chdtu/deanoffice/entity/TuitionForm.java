package ua.edu.chdtu.deanoffice.entity;

public enum TuitionForm {
    FULL_TIME("Денна"),
    EXTRAMURAL("Скорочена");

    private final String nameUkr;

    TuitionForm(String nameUkr) {
        this.nameUkr = nameUkr;
    }

    public String getNameUkr() {
        return nameUkr;
    }
}
