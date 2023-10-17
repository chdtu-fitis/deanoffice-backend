package ua.edu.chdtu.deanoffice.service;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPatronimic() {
        return patronimic;
    }

    public void setPatronimic(String patronimic) {
        this.patronimic = patronimic;
    }

    public String getStudentGroupName() {
        return studentGroupName;
    }

    public void setStudentGroupName(String studentGroupName) {
        this.studentGroupName = studentGroupName;
    }

    public String getSpecialityCode() {
        return specialityCode;
    }

    public void setSpecialityCode(String specialityCode) {
        this.specialityCode = specialityCode;
    }
}
