package ua.edu.chdtu.deanoffice.api.general;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NamedDTO {
    @JsonView(GeneralView.Named.class)
    private int id;
    @JsonView(GeneralView.Named.class)
    private String name;
}
