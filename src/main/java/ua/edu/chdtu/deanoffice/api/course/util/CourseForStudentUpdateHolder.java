package ua.edu.chdtu.deanoffice.api.course.util;

import ua.edu.chdtu.deanoffice.api.course.dto.CourseDTO;

public class CourseForStudentUpdateHolder {
    private int oldCourseId;
    private CourseDTO newCourse;

    public int getOldCourseId() {
        return oldCourseId;
    }

    public CourseDTO getNewCourse() {
        return newCourse;
    }
}
