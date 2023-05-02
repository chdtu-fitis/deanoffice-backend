package ua.edu.chdtu.deanoffice.service.stipend;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseForStipendBean {
    private String courseName;
    private String knowledgeControlName;
    private int semester;

    public CourseForStipendBean(String courseName, String knowledgeControlName, int semester) {
        this.courseName = courseName;
        this.knowledgeControlName = knowledgeControlName;
        this.semester = semester;
    }
}

