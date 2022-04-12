package ua.edu.chdtu.deanoffice.service.course.selective.statistics;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentsRegistrationOnCoursesPercent {
    private int studyYear;
    private int percent;

    public StudentsRegistrationOnCoursesPercent(int studyYear, long percent) {
        this.studyYear = studyYear;
        this.percent = (int)percent;
    }

    public StudentsRegistrationOnCoursesPercent() {
    }
}
