package ua.edu.chdtu.deanoffice.entity;

public enum Citizenship {
    AZE(31, "Азербайджан", "Azerbaijan"),
    ARM(51, "Вірменія", "Armenia"),
    BGR(100, "Болгарія", "Bulgaria"),
    BLR(112, "Білорусь", "Belarus"),
    CAN(120, "Камерун", "Cameroon"),
    CHN(156, "Китай", "China"),
    COL(170, "Колумбія", "Columbia"),
    GEO(268, "Грузія", "Georgia"),
    PSE(274, "Палестина", "Palestine"),
    DEU(276, "Німеччина", "Germany"),
    GHA(288, "Гана", "Ghana"),
    GRC(300, "Греція", "Greece"),
    GIN(324, "Гвинея", "Guinea"),
    HUN(348, "Угорщина", "Hungary"),
    JOR(400, "Йорданія", "Jordan"),
    IRQ(368, "Ірак", "Iraq"),
    ISR(376, "Ізраїль", "Israel"),
    CIV(384, "Кот-Д'Івуар",  "Cote d'Ivoire"),
    MDA(498, "Молдова", "Moldova"),
    MAR(504, "Марокко", "Morocco"),
    POL(616, "Польща", "Poland"),
    ROU(642, "Румунія", "Romania"),
    RUS(643, "Росія", "Russia"),
    TUR(792, "Туреччина", "Turkey"),
    TKM(795, "Туркменістан", "Turkmenistan"),
    UZB(860, "Узбекистан", "Uzbekistan"),
    UKR(804, "Україна", "Ukraine");

    private final Integer countryCode;
    private final String nameUkr;
    private final String nameEng;

    Citizenship(Integer countryCode, String nameUkr, String nameEng) {
        this.countryCode = countryCode;
        this.nameUkr = nameUkr;
        this.nameEng = nameEng;
    }

    public Integer getCountryCode() {
        return countryCode;
    }

    public String getNameUkr() {
        return nameUkr;
    }

    public String getNameEng() {
        return nameEng;
    }

    public static Citizenship getCitizenshipByCountryCode(Integer countryCode) {
        for (Citizenship citizenship : Citizenship.values()) {
            if (citizenship.getCountryCode().equals(countryCode))
                return citizenship;
        }
        return null;
    }

    public static Citizenship getCitizenshipByCountryUkrName(String nameUkr) {
        for (Citizenship citizenship : Citizenship.values()) {
            if (citizenship.getNameUkr().equals(nameUkr))
                return citizenship;
        }
        return null;
    }
}
