package ua.edu.chdtu.deanoffice.api.course.selective.dto.statistics;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentsRegistrationOnCoursesPercentDTO {
    private int studyYear;
    private int totalCount;
    private int registeredCount;
    private int percent;
}
