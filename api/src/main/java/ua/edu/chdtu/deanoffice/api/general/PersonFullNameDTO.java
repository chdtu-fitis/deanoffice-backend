package ua.edu.chdtu.deanoffice.api.general;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonFullNameDTO {
    private int id;
    private String surname;
    private String name;
    private String patronimic;
}
