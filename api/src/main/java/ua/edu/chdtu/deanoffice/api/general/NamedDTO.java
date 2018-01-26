package ua.edu.chdtu.deanoffice.api.general;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NamedDTO {
    private int id;
    private String name;
    //TODO cr: в ДТО можна зробити public проперті без геттерів/сетерів
}
