package ua.edu.chdtu.deanoffice.service.course.selective.statistics;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AppointSelectiveCourse {
    List<String> courseNam;
    String studentsName;

    public AppointSelectiveCourse() {
    }

    public AppointSelectiveCourse(String studentsName) {
        this.studentsName = studentsName;

    }
}
