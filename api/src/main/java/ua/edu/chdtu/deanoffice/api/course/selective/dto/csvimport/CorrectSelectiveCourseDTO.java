package ua.edu.chdtu.deanoffice.api.course.selective.dto.csvimport;

import lombok.*;
import ua.edu.chdtu.deanoffice.entity.*;

@Getter
@Setter
public class CorrectSelectiveCourseDTO {
    private Integer semester;
    private TrainingCycle trainingCycle;
    private Integer fieldOfKnowledge;
    private String courseName;
    private String description;
    private String department;
    private String teacher;
}
