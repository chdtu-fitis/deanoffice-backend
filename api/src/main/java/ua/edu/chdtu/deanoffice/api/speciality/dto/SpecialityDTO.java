package ua.edu.chdtu.deanoffice.api.speciality.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpecialityDTO {
    private int id;
    private String name;
    private String nameEng;
    private boolean active;
    private String code;
    private String fieldOfStudy;
    private String fieldOfStudyEng;
}