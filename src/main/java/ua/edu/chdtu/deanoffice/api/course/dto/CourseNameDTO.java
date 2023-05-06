package ua.edu.chdtu.deanoffice.api.course.dto;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.CourseName;

@Getter
@Setter
public class CourseNameDTO {
    private int id;
    private String name;
    private String abbreviation;
    private String nameEng;
}
