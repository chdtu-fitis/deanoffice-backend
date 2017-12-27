package ua.edu.chdtu.deanoffice.api.group.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KnowledgeControlDTO {
    @JsonView(GroupViews.Name.class)
    private int id;
    @JsonView(GroupViews.Name.class)
    private String name;
    private String nameEng;
    private boolean hasGrade;
}
