package ua.edu.chdtu.deanoffice.api.course.selective.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentSelectiveCourseMoreOrLessNormDTO {
    int studentDegreeId;
    String name;
    String surname;
    String facultyName;
    String specialityCode;
    int year;
    String groupName;
    int coursesNumber;
}
