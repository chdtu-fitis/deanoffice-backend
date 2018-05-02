package ua.edu.chdtu.deanoffice.api.course.util;

import lombok.Getter;
import ua.edu.chdtu.deanoffice.api.course.dto.CourseForGroupDTO;

import java.util.List;

@Getter
public class CoursesForGroupHolder {
    private List<CourseForGroupDTO> newCourses;
    private List<CourseForGroupDTO> updatedCourses;
    private List<Integer> deleteCoursesIds;
}
