package ua.edu.chdtu.deanoffice.api.course.util;

import ua.edu.chdtu.deanoffice.api.course.dto.CourseForGroupDTO;

import java.util.List;

public class CoursesForGroupHolder {
    private List<CourseForGroupDTO> newCourses;
    private List<CourseForGroupDTO> updatedCourses;
    private List<Integer> deleteCoursesIds;


    CoursesForGroupHolder(){}

    public List<CourseForGroupDTO> getNewCourses() {
        return newCourses;
    }

    public List<CourseForGroupDTO> getUpdatedCourses() {
        return updatedCourses;
    }

    public List<Integer> getDeleteCoursesIds() {
        return deleteCoursesIds;
    }


}
