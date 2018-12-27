package ua.edu.chdtu.deanoffice.api.student.stipend;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class StudentInfoForStipendDto {
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
    List<CourseForStipendDto> debtCourses;

    public StudentInfoForStipendDto(Integer id, String surname, String name, String patronimic, String degreeName, String groupName, int year, String tuitionTerm, String specialityCode, String specialityName, String specializationName, String departmentAbbreviation, double averageGrade) {
        this.id = id;
        this.surname = surname;
        this.name = name;
        this.patronimic = patronimic;
        this.degreeName = degreeName;
        this.groupName = groupName;
        this.year = year;
        this.tuitionTerm = tuitionTerm;
        this.specialityCode = specialityCode;
        this.specialityName = specialityName;
        this.specializationName = specializationName;
        this.departmentAbbreviation = departmentAbbreviation;
        this.averageGrade = averageGrade;
        debtCourses = new LinkedList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentInfoForStipendDto that = (StudentInfoForStipendDto) o;
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
