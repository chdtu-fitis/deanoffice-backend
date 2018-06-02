package ua.edu.chdtu.deanoffice.api.course.util;

import ua.edu.chdtu.deanoffice.api.course.dto.CourseDTO;
import ua.edu.chdtu.deanoffice.api.course.dto.CourseForGroupDTO;

import java.util.List;

public class CourseForGroupUpdateHolder {
    private int oldCourseId;
    private CourseDTO newCourse;
    private int courseForGroupId;

    public int getCourseForGroupId() {
        return courseForGroupId;
    }

    public int getOldCourseId() {
        return oldCourseId;
    }

    public CourseDTO getNewCourse() {
        return newCourse;
    }
}
