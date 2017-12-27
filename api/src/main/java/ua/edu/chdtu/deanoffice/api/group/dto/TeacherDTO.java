package ua.edu.chdtu.deanoffice.api.group.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.Department;
import ua.edu.chdtu.deanoffice.entity.Position;

import javax.persistence.Column;
import javax.persistence.ManyToOne;

@Getter
@Setter
public class TeacherDTO {
    @JsonView(GroupViews.Name.class)
    private int id;
    @JsonView(GroupViews.Name.class)
    private String name;
    @JsonView(GroupViews.Name.class)
    private String surname;
    @JsonView(GroupViews.Name.class)
    private String patronimic;
    private boolean active = true;
    private char sex = 'm';
    private Department department;
    private Position position;
    private String scientificDegree;
}
