package ua.edu.chdtu.deanoffice.courseforgroup.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeacherDTO {
    private int id;
    private String name;
    private String surname;
    private String patronimic;
    private String departmentName;

    public TeacherDTO(int id, String name, String surname, String patronimic, String departmentName) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.patronimic = patronimic;
        this.departmentName = departmentName;
    }


}
