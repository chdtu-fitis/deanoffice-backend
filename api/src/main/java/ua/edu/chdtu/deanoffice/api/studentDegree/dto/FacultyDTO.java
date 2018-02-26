package ua.edu.chdtu.deanoffice.api.studentDegree.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FacultyDTO {
    private String abbr;
    private boolean active;
    private String name;
    private Integer id;
}
