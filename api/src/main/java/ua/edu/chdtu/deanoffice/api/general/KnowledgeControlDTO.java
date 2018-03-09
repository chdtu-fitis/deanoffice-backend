package ua.edu.chdtu.deanoffice.api.general;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.group.dto.GroupViews;

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
