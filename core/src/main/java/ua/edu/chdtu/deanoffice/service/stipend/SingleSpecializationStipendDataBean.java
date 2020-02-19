package ua.edu.chdtu.deanoffice.service.stipend;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class SingleSpecializationStipendDataBean {
    private String degreeName;
    private int year;
    private String specializationName;
    private String tuitionTerm;
    private String specialityCode;
    private String specialityName;
    private String groupsName;

    public SingleSpecializationStipendDataBean(){
    }

    public SingleSpecializationStipendDataBean(String degreeName, int year, String specializationName, String tuitionTerm, String specialityCode, String specialityName){
        this.degreeName = degreeName;
        this.year = year;
        this.specializationName = specializationName;
        this.tuitionTerm = tuitionTerm;
        this.specialityCode = specialityCode;
        this.specialityName = specialityName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SingleSpecializationStipendDataBean that = (SingleSpecializationStipendDataBean) o;
        return year == that.year &&
                Objects.equals(degreeName, that.degreeName) &&
                Objects.equals(specializationName, that.specializationName) &&
                Objects.equals(tuitionTerm, that.tuitionTerm) &&
                Objects.equals(specialityCode, that.specialityCode) &&
                Objects.equals(specialityName, that.specialityName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(degreeName, year, specializationName, tuitionTerm, specialityCode, specialityName);
    }
}
