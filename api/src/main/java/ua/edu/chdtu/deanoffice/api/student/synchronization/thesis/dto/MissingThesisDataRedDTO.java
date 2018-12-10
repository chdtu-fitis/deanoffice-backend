package ua.edu.chdtu.deanoffice.api.student.synchronization.thesis.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MissingThesisDataRedDTO {
    private ImportedThesisDataDTO thesisPrimaryData;
    private String message;
}
