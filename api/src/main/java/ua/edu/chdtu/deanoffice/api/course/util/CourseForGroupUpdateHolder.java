package ua.edu.chdtu.deanoffice.api.course.util;

import ua.edu.chdtu.deanoffice.api.course.dto.CourseDTO;
import ua.edu.chdtu.deanoffice.api.course.dto.CourseForGroupDTO;

import java.util.List;

public class CourseForGroupUpdateHolder {
    private CourseDTO oldCourse;
    private CourseDTO newCourse;
    private int courseForGroupId;

    public int getCourseForGroupId() {
        return courseForGroupId;
    }

    public CourseDTO getOldCourse() {
        return oldCourse;
    }

    public CourseDTO getNewCourse() {
        return newCourse;
    }
}
