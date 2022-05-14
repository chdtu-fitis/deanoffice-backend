package ua.edu.chdtu.deanoffice.service.course.selective.importcsv.beans;

import lombok.*;
import ua.edu.chdtu.deanoffice.entity.*;

@Getter
@Setter
public class CorrectSelectiveCourse {
    private Integer semester;
    private TrainingCycle trainingCycle;
    private Integer fieldOfKnowledge;
    private String courseName;
    private String description;
    private String department;
    private String teacher;

    public CorrectSelectiveCourse(Integer semester, TrainingCycle trainingCycle, Integer fieldOfKnowledge, String courseName,
                           String description, String department, String teacher) {
        this.semester = semester;
        this.trainingCycle = trainingCycle;
        this.fieldOfKnowledge = fieldOfKnowledge;
        this.courseName = courseName;
        this.description = description;
        this.department = department;
        this.teacher = teacher;
    }
}
