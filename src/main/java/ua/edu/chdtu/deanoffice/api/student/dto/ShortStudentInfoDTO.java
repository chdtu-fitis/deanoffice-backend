package ua.edu.chdtu.deanoffice.api.student.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
}