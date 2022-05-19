package ua.edu.chdtu.deanoffice.service.course.selective.statistics;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class RegisteredStudentsNameResult {
    List<CoursesSelectedByStudentsGroupResult> coursesSelectedByStudentsGroup;
    List<StudentNameAndId> studentsNameAndId;

    public RegisteredStudentsNameResult(List<CoursesSelectedByStudentsGroupResult> coursesSelectedByStudentsGroup, List<StudentNameAndId> studentsNameAndId) {
        this.coursesSelectedByStudentsGroup = coursesSelectedByStudentsGroup;
        this.studentsNameAndId = studentsNameAndId;
    }

    public RegisteredStudentsNameResult() {
    }
}
