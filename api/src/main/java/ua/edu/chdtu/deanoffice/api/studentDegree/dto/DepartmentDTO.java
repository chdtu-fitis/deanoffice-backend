package ua.edu.chdtu.deanoffice.api.studentDegree.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DepartmentDTO {
    private Integer id;
    private String name;
    private boolean active;
    private String abbr;
}
