package ua.edu.chdtu.deanoffice.api.speciality.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpecialityDTO {
    @JsonView(SpecialityView.Basic.class)
    private int id;
    @JsonView(SpecialityView.Basic.class)
    private String name;
    private String nameEng;
    private boolean active;
    @JsonView(SpecialityView.Basic.class)
    private String code;
    private String fieldOfStudy;
    private String fieldOfStudyEng;
}

