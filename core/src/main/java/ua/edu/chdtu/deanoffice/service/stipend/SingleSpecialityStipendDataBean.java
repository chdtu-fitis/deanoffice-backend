package ua.edu.chdtu.deanoffice.service.stipend;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class SingleSpecialityStipendDataBean {
    private String degreeName;
    private int year;
    private String tuitionTerm;
    private String specialityCode;
    private String specialityName;
    private String groupsName;

    public SingleSpecialityStipendDataBean(){
    }

    public SingleSpecialityStipendDataBean(String degreeName, int year, String tuitionTerm, String specialityCode, String specialityName){
        this.degreeName = degreeName;
        this.year = year;
        this.tuitionTerm = tuitionTerm;
        this.specialityCode = specialityCode;
        this.specialityName = specialityName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SingleSpecialityStipendDataBean that = (SingleSpecialityStipendDataBean) o;
        return year == that.year &&
                Objects.equals(degreeName, that.degreeName) &&
                Objects.equals(tuitionTerm, that.tuitionTerm) &&
                Objects.equals(specialityCode, that.specialityCode) &&
                Objects.equals(specialityName, that.specialityName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(degreeName, year, tuitionTerm, specialityCode, specialityName);
    }
}
