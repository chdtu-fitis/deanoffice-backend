package ua.edu.chdtu.deanoffice.api.order.dto;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.dto.FacultyDTO;
import ua.edu.chdtu.deanoffice.api.general.dto.NamedDTO;
import ua.edu.chdtu.deanoffice.entity.Faculty;

@Getter
@Setter
public class OrderApproverDTO {
    private int id;
    private String position;
    private String fullName;
    private NamedDTO faculty;
    private boolean active;
}
