package ua.edu.chdtu.deanoffice.api.student.stipend;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class SpecialityStudentsInfoForStipendDTO {
    private String tuitionTerm;
    private String specialityCode;
    private String specialityName;
    private String degreeName;
    private String groupsName;
    private List<StudentInfoForStipendDTO> studentsInfoForStipend;

    public SpecialityStudentsInfoForStipendDTO() {}

    public SpecialityStudentsInfoForStipendDTO(String tuitionTerm, String specialityCode, String specialityName, String degreeName, String groupsName, List<StudentInfoForStipendDTO> studentsInfoForStipend) {
        this.tuitionTerm = tuitionTerm;
        this.specialityCode = specialityCode;
        this.specialityName = specialityName;
        this.degreeName = degreeName;
        this.groupsName = groupsName;
        this.studentsInfoForStipend = studentsInfoForStipend;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpecialityStudentsInfoForStipendDTO that = (SpecialityStudentsInfoForStipendDTO) o;
        return degreeName.equals(that.degreeName) &&
                Objects.equals(groupsName, that.groupsName) &&
                tuitionTerm.equals(that.tuitionTerm) &&
                specialityCode.equals(that.specialityCode) &&
                specialityName.equals(that.specialityName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(degreeName, groupsName, tuitionTerm, specialityCode, specialityName);
    }
}
