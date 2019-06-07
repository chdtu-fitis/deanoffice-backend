package ua.edu.chdtu.deanoffice.api.student.stipend;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExtraPointsDTO {
    private Integer studentDegreeId;
    private int points;

    public ExtraPointsDTO(Integer studentDegreeId, int points){
        this.studentDegreeId = studentDegreeId;
        this.points = points;
    }
}
