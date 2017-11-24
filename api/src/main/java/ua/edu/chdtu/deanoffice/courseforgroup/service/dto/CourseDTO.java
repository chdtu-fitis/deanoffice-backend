package ua.edu.chdtu.deanoffice.courseforgroup.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseDTO {
    private int id;
    private String name;
    private String type;
    private int hours;

    public CourseDTO(int id, String name, String type, int hours) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.hours = hours;
    }
}
