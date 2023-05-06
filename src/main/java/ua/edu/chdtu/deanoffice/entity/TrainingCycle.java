package ua.edu.chdtu.deanoffice.entity;


import ua.edu.chdtu.deanoffice.exception.NotFoundException;

public enum TrainingCycle {
    PROFESSIONAL("Професійної підготовки"),
    GENERAL("Загальної підготовки");

    private final String nameUkr;

    TrainingCycle(String nameUkr) {
        this.nameUkr = nameUkr;
    }

    public static TrainingCycle getTypeCycleByName(String name) throws NotFoundException {
        for (TrainingCycle trainingCycle : TrainingCycle.values()) {
            if (trainingCycle.toString().equals(name))
                return trainingCycle;
        }

        throw new NotFoundException("Не вдалося знайти TrainingCycle за вказаним ім'ям");
    }
}
