package ua.edu.chdtu.deanoffice.service.course.selective.importcsv.beans;

import lombok.*;

import java.util.*;

@Getter
@Setter
public class SelectiveCourseCsvReport {
    private List<CorrectSelectiveCourse> correctSelectiveCourses;
    private List<IncorrectSelectiveCourse> incorrectSelectiveCourses;

    public SelectiveCourseCsvReport() {
        correctSelectiveCourses = new ArrayList<>();
        incorrectSelectiveCourses = new ArrayList<>();
    }

    public void addCorrectSelectiveCourse(CorrectSelectiveCourse correctSelectiveCourse) {
        correctSelectiveCourses.add(correctSelectiveCourse);
    }

    public void addIncorrectSelectiveCourse(IncorrectSelectiveCourse incorrectSelectiveCourse) {
        incorrectSelectiveCourses.add(incorrectSelectiveCourse);
    }
}
