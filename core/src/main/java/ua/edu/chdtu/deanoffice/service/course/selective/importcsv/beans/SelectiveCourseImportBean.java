package ua.edu.chdtu.deanoffice.service.course.selective.importcsv.beans;


import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.TrainingCycle;

@Getter
@Setter
public class SelectiveCourseImportBean {
    private String teacher;
    private int degreeId;
    private String department;
    private String fieldOfKnowledge;
    private TrainingCycle trainingCycle;
    private String description;
    private int studyYear;
    private int semester;
    private String courseName;
}
