package ua.edu.chdtu.deanoffice.api.course.selective.dto.csvimport;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.service.course.selective.importcsv.beans.CorrectSelectiveCourse;
import ua.edu.chdtu.deanoffice.service.course.selective.importcsv.beans.IncorrectSelectiveCourse;

import java.util.List;

@Getter
@Setter
public class SelectiveCourseCsvReportDTO {
    private List<CorrectSelectiveCourseDTO> correctSelectiveCourses;
    private List<IncorrectSelectiveCourseDTO> incorrectSelectiveCourses;
}
