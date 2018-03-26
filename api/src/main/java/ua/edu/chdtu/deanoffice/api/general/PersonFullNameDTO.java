package ua.edu.chdtu.deanoffice.api.general;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonFullNameDTO {
    @JsonView(GeneralView.PersonFullName.class)
    private int id;
    @JsonView(GeneralView.PersonFullName.class)
    private String surname;
    @JsonView(GeneralView.PersonFullName.class)
    private String name;
    @JsonView(GeneralView.PersonFullName.class)
    private String patronimic;
}
