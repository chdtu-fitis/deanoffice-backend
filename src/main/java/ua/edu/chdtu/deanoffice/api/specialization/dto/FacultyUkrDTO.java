package ua.edu.chdtu.deanoffice.api.specialization.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacultyUkrDTO {
    @JsonView(SpecializationView.Faculty.class)
    private int id;
    @JsonView(SpecializationView.Faculty.class)
    private String name;
    @JsonView(SpecializationView.Faculty.class)
    private String abbr;
    @JsonView(SpecializationView.Faculty.class)
    private String dean;
}
