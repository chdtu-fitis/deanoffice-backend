package ua.edu.chdtu.deanoffice.entity;


import ua.edu.chdtu.deanoffice.exception.NotFoundException;

public enum TypeCycle {
    PROFESSIONAL("Професійної підготовки"),
    GENERAL("Загальної підготовки");

    private final String nameUkr;

    TypeCycle(String nameUkr) {
        this.nameUkr = nameUkr;
    }

    public static TypeCycle getTypeCycleByName(String name) throws NotFoundException {
        for (TypeCycle typeCycle : TypeCycle.values()) {
            if (typeCycle.toString().equals(name))
                return typeCycle;
        }

        throw new NotFoundException("Не вдалося знайти TypeCycle за вказаним ім'ям");
    }
}
