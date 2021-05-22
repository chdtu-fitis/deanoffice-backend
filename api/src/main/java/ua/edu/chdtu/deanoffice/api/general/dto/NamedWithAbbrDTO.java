package ua.edu.chdtu.deanoffice.api.general.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NamedWithAbbrDTO extends  NamedDTO {
    private String abbr;
}
