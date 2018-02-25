package ua.edu.chdtu.deanoffice.api.studentDegree.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class SpecialityDTO {
    private Integer id;
    private String code;
    private String fieldOfStudy;
    private String name;
}
