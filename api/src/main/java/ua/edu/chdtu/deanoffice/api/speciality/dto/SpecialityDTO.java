package ua.edu.chdtu.deanoffice.api.speciality.dto;

import com.fasterxml.jackson.annotation.JsonView;

public class SpecialityDTO {
    @JsonView(SpecialityView.Basic.class)
    private int id;
    private String name;
    private String nameEng;
    private boolean active;
    @JsonView(SpecialityView.Basic.class)
    private String code;
    private String fieldOfStudy;
    private String fieldOfStudyEng;
}
