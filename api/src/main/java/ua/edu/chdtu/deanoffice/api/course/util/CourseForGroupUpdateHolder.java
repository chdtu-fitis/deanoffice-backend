package ua.edu.chdtu.deanoffice.api.course.util;

import ua.edu.chdtu.deanoffice.api.course.dto.CourseForGroupDTO;

import java.util.List;

public class CourseForGroupUpdateHolder {
    private CourseForGroupDTO  oldCourse;
    private CourseForGroupDTO newCourse;

    public CourseForGroupDTO getOldCourse() {
        return oldCourse;
    }

    public CourseForGroupDTO getNewCourse() {
        return newCourse;
    }
}
