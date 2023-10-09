package ua.edu.chdtu.deanoffice.api.student.dto;

public class ShortStudentInfoDTO {

    int id;
    String fullName;

    String groupName;

    double specialityCode;

    public ShortStudentInfoDTO() {
    }

    public ShortStudentInfoDTO(int id, String fullName, String groupName, double specialityCode) {
        this.id = id;
        this.fullName = fullName;
        this.groupName = groupName;
        this.specialityCode = specialityCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public double getSpecialityCode() {
        return specialityCode;
    }

    public void setSpecialityCode(double specialityCode) {
        this.specialityCode = specialityCode;
    }
}