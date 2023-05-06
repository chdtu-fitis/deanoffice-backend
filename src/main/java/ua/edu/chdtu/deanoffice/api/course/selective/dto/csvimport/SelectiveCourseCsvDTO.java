package ua.edu.chdtu.deanoffice.api.course.selective.dto.csvimport;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SelectiveCourseCsvDTO  {
    private String semester;
    private String trainingCycle;
    private String fieldOfKnowledge;
    private String courseName;
    private String description;
    private String department;
    private String teacher;
}
