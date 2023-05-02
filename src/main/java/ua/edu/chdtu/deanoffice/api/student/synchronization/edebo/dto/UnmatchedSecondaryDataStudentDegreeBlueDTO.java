package ua.edu.chdtu.deanoffice.api.student.synchronization.edebo.dto;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentDegreeDTO;

@Getter
@Setter
public class UnmatchedSecondaryDataStudentDegreeBlueDTO {
    private StudentDegreeFullEdeboDataDto studentDegreeFromData, studentDegreeFromDb;
}
