package ua.edu.chdtu.deanoffice.entity;

public enum TypeCycle {
    PROFESSIONAL("Професійної підготовки"),
    GENERAL("Загальної підготовки");

    private final String nameUkr;

    TypeCycle(String nameUkr) {
        this.nameUkr = nameUkr;
    }
}
