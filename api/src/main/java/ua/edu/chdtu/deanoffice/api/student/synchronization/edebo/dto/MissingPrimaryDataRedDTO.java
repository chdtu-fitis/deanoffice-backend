package ua.edu.chdtu.deanoffice.api.student.synchronization.edebo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MissingPrimaryDataRedDTO {
    StudentDegreePrimaryEdeboDataDTO studentDegreePrimaryData;
    String message;
}
