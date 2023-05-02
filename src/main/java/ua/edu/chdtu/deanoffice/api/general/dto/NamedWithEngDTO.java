package ua.edu.chdtu.deanoffice.api.general.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NamedWithEngDTO {
    @JsonView(GeneralView.Named.class)
    private int id;
    @JsonView(GeneralView.Named.class)
    private String name;
    @JsonView(GeneralView.Named.class)
    private String nameEng;
}
