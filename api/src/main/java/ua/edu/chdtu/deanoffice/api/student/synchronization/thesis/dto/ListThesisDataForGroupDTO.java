package ua.edu.chdtu.deanoffice.api.student.synchronization.thesis.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ListThesisDataForGroupDTO {
    private String groupName;
    private List<ImportedThesisDataDTO> thesisDataBeans;
}
