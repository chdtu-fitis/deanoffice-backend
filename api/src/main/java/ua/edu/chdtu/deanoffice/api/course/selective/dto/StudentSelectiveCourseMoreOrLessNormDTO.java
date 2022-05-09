package ua.edu.chdtu.deanoffice.api.course.selective.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StudentSelectiveCourseMoreOrLessNormDTO {
    int studentDegreeId;
    String name;
    String surname;
    String facultyName;
    String specialityCode;
    int year;
    String group;
    int coursesNumber;
}
