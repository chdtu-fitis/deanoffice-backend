package ua.edu.chdtu.deanoffice.entity;

public enum Citizenship {
    AZE(31, "Азербайджан"),
    IRQ(368, "Ірак"),
    ISR(376, "Ізраїль"),
    MAR(504, "Марокко"),
    TKM(795, "Туркменістан"),
    UKR(804, "Україна"),
    RUS(643, "Росія"),
    BLR(112, "Білорусь"),
    MDA(498, "Молдова"),
    BGR(100, "Болгарія"),
    HUN(348, "Угорщина"),
    ROU(642, "Румунія"),
    POL(616, "Польща"),
    ARM(51, "Вірменія"),
    GRC(300, "Греція"),
    DEU(276, "Німеччина");

    private final Integer countryCode;
    private final String nameUkr;

    Citizenship(Integer countryCode, String nameUkr) {
        this.countryCode = countryCode;
        this.nameUkr = nameUkr;
    }

    public Integer getCountryCode() {
        return countryCode;
    }

    public String getNameUkr() {
        return nameUkr;
    }

    public static Citizenship getCitizenshipByCountryCode(Integer countryCode) {
        for (Citizenship citizenship : Citizenship.values()) {
            if (citizenship.getCountryCode().equals(countryCode))
                return citizenship;
        }
        return null;
    }
}
