package ua.edu.chdtu.deanoffice.api.general.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentDTO {
    @JsonView(GeneralView.Department.class)
    private int id;
    @JsonView(GeneralView.Department.class)
    private String name;
    @JsonView(GeneralView.Department.class)
    private boolean active = true;
    @JsonView(GeneralView.Department.class)
    private String abbr;
}
