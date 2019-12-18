package ua.edu.chdtu.deanoffice.service.stipend;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class SingleSpecializationStipendDataBean {
    private String degreeName;
    private int year;
    //private String courseName;
    private String specializationName;
    private String tuitionTerm;
    private String specialityCode;
    private String specialityName;


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
}
