package ua.edu.chdtu.deanoffice.api.general.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentDTO {
    private int id;
    private String name;
    private boolean active = true;
    private String abbr;
}
