package ua.edu.chdtu.deanoffice.api.student.synchronization.thesis;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AllThesisListsDTO {
    List<ImportedThesisDataDTO> importedThesisDataDTOs;
    List<MissingThesisDataRedDTO> missingThesisDataRedDTOs;
}
