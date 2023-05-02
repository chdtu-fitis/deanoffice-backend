package ua.edu.chdtu.deanoffice.service.course.selective.importcsv.beans;

import lombok.*;


@Getter
@Setter
public class IncorrectSelectiveCourse extends SelectiveCourseCsvBean {

    private String alert;

    public IncorrectSelectiveCourse(SelectiveCourseCsvBean course, String alert) {
        this.setSemester(course.getSemester());
        this.setTrainingCycle(course.getTrainingCycle());
        this.setFieldOfKnowledge(course.getFieldOfKnowledge());
        this.setCourseName(course.getCourseName());
        this.setDescription(course.getDescription());
        this.setDepartment(course.getDepartment());
        this.setTeacher(course.getTeacher());
        this.alert = alert;
    }

    public  IncorrectSelectiveCourse() {
    }
}

