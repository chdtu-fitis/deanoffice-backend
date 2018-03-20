package ua.edu.chdtu.deanoffice.api.general;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NamedDTO {
    @JsonView(NamedView.Named.class)
    private int id;
    @JsonView(NamedView.Named.class)
    private String name;
}
