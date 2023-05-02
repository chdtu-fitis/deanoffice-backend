package ua.edu.chdtu.deanoffice.api.student.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.chdtu.deanoffice.entity.StudentStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentStatusDTO {
    private StudentStatus studentStatus;
}
