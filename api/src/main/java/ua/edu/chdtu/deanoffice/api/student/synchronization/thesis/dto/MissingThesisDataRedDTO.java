package ua.edu.chdtu.deanoffice.api.student.synchronization.thesis.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MissingThesisDataRedDTO {
    ImportedThesisDataDTO thesisPrimaryData;
    String message;
}
