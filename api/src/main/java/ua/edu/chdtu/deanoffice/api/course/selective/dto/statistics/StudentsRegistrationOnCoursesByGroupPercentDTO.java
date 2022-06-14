package ua.edu.chdtu.deanoffice.api.course.selective.dto.statistics;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentsRegistrationOnCoursesByGroupPercentDTO {
    private String groupName;
    private String facultyName;
    private int studyYear;
    private String department;
    private int totalCount;
    private int registeredCount;
    private int registeredPercent;
    private int choosingLessCount;
    private int choosingLessPercent;
    private int notRegisteredCount;
    private int notRegisteredPercent;
}
