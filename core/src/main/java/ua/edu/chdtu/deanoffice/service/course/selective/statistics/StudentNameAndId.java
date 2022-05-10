package ua.edu.chdtu.deanoffice.service.course.selective.statistics;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentNameAndId {
    private int id;
    private String name;

    public StudentNameAndId(int id, String nameStudent) {
        this.id = id;
        this.name = nameStudent;
    }

    public StudentNameAndId() {
    }
}
