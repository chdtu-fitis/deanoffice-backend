package ua.edu.chdtu.deanoffice.service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShortStudentInfoBean {

    private int id;
    private String surname;
    private String name;
    private String patronimic;
    private String studentGroupName;
    private String specialityCode;

    public ShortStudentInfoBean() {
    }

    public ShortStudentInfoBean(int id, String surname, String name, String patronimic, String studentGroupName, String specialityCode) {
        this.id = id;
        this.surname = surname;
        this.name = name;
        this.patronimic = patronimic;
        this.studentGroupName = studentGroupName;
        this.specialityCode = specialityCode;
    }
}
