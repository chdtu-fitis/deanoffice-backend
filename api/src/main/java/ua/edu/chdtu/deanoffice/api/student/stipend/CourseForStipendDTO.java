package ua.edu.chdtu.deanoffice.api.student.stipend;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseForStipendDTO {
    private String courseName;
    private String knowledgeControlName;
    private int semester;

}
