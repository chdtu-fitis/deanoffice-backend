package ua.edu.chdtu.deanoffice.api.general.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacultyDTO {
    private int id;
    private String name;
    private String nameEng;
    private String abbr;
    private String dean;
    private String deanEng;
    private String nameGenitive;
}
