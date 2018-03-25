package ua.edu.chdtu.deanoffice.api.group.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseNameDTO {
    @JsonView({GroupViews.Name.class, StudentGroupView.Course.class})
    private int id;
    @JsonView({GroupViews.Name.class, StudentGroupView.Course.class})
    private String name;
    private String nameEng;
    private String abbreviation;
}
