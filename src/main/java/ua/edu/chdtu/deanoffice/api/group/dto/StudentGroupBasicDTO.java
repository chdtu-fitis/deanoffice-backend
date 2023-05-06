package ua.edu.chdtu.deanoffice.api.group.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.general.dto.GeneralView;
import ua.edu.chdtu.deanoffice.api.general.dto.NamedDTO;

@Getter
@Setter
public class StudentGroupBasicDTO extends NamedDTO {
    @JsonView(GeneralView.Named.class)
    private int creationYear;
    @JsonView(GeneralView.Named.class)
    private int realBeginYear;
    @JsonView(GeneralView.Named.class)
    private int beginYears;
}
