package ua.edu.chdtu.deanoffice.api.student.synchronization.thesis;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.student.synchronization.thesis.ImportedThesisDataDTO;

@Getter
@Setter
public class MissingThesisDataRedDTO {
    ImportedThesisDataDTO importedThesisDataDTO;
    String message;
}
