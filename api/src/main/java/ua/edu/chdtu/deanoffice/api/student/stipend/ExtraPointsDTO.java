package ua.edu.chdtu.deanoffice.api.student.stipend;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentDegreeDTO;

@Getter
@Setter
public class ExtraPointsDTO {
    private StudentDegreeDTO studentDegreeDTO;
    private int semester;
    private int points;

    public ExtraPointsDTO(StudentDegreeDTO studentDegreeDTO, int semester, int points){
        this.studentDegreeDTO = studentDegreeDTO;
        this.semester = semester;
        this.points = points;
    }
}
