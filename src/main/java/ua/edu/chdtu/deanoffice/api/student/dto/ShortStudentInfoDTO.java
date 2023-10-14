package ua.edu.chdtu.deanoffice.api.student.dto;

public class ShortStudentInfoDTO {

    private int id;
    private String fullName;

    private String groupName;

    private String specialityCode;

    public ShortStudentInfoDTO() {
    }

    public ShortStudentInfoDTO(int id, String fullName, String groupName, String specialityCode) {
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

    public String getSpecialityCode() {
        return specialityCode;
    }

    public void setSpecialityCode(String specialityCode) {
        this.specialityCode = specialityCode;
    }
}