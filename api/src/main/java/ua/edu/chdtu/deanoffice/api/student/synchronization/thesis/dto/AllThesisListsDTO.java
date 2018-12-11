package ua.edu.chdtu.deanoffice.api.student.synchronization.thesis.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AllThesisListsDTO {
    private List<ListThesisDataForGroupDTO> listThesisDataForGroupDTOs;
    private List<MissingThesisDataRedDTO> missingThesisDataRedDTOs;
}
