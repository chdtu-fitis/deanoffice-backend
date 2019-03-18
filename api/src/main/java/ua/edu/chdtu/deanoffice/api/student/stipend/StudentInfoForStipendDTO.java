package ua.edu.chdtu.deanoffice.api.student.stipend;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class StudentInfoForStipendDTO {
    private Integer id;
    private String surname;
    private String name;
    private String patronimic;
    private String degreeName;
    private String groupName;
    private int year;
    private String tuitionTerm;
    private String specialityCode;
    private String specialityName;
    private String specializationName;
    private String departmentAbbreviation;
    private double averageGrade;
    List<CourseForStipendDTO> debtCourses = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentInfoForStipendDTO that = (StudentInfoForStipendDTO) o;
        return year == that.year &&
                Double.compare(that.averageGrade, averageGrade) == 0 &&
                id.equals(that.id) &&
                Objects.equals(surname, that.surname) &&
                Objects.equals(name, that.name) &&
                Objects.equals(patronimic, that.patronimic) &&
                degreeName.equals(that.degreeName) &&
                Objects.equals(groupName, that.groupName) &&
                tuitionTerm.equals(that.tuitionTerm) &&
                specialityCode.equals(that.specialityCode) &&
                specialityName.equals(that.specialityName) &&
                specializationName.equals(that.specializationName) &&
                departmentAbbreviation.equals(that.departmentAbbreviation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, surname, name, patronimic, degreeName, groupName, year, tuitionTerm, specialityCode, specialityName, specializationName, departmentAbbreviation, averageGrade);
    }
}
