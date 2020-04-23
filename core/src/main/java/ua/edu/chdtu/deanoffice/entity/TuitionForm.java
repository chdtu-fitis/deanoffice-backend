package ua.edu.chdtu.deanoffice.entity;

public enum TuitionForm {
    FULL_TIME("Денна"),
    EXTRAMURAL("Заочна");

    private final String nameUkr;

    TuitionForm(String nameUkr) {
        this.nameUkr = nameUkr;
    }

    public String getNameUkr() {
        return nameUkr;
    }

    public static TuitionForm getTuitionFormFromUkrName(String ukrName){
        String ukrNameInUppercase = ukrName.toUpperCase();

        for (TuitionForm tuitionForm: values()) {
            if (tuitionForm.getNameUkr().toUpperCase().equals(ukrNameInUppercase))
                return tuitionForm;
        }
        return null;
    }
}
