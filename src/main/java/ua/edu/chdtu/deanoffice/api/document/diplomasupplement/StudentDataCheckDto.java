package ua.edu.chdtu.deanoffice.api.document.diplomasupplement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentDataCheckDto {
    private String surname;
    private String name;
    private String patronimic;
    private String groupName;
    private String message;
}
