package ua.edu.chdtu.deanoffice.api.student.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;

import java.util.List;

@Getter
@Setter
public class ImportReportDTO {
    @JsonView(StudentView.DetailAndDegree.class)
    private List<StudentDegreeDTO> insertData;
    @JsonView(StudentView.DetailAndDegree.class)
    private List<StudentDegreeDTO> updateData;
    @JsonView(StudentView.DetailAndDegree.class)
    private List<StudentDegreeDTO> failData;
}