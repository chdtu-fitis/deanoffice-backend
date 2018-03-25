package ua.edu.chdtu.deanoffice.api.general;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.api.group.dto.GroupViews;
import ua.edu.chdtu.deanoffice.api.group.dto.StudentGroupView;

@Getter
@Setter
public class KnowledgeControlDTO {
    @JsonView({GroupViews.Name.class, StudentGroupView.Course.class})
    private int id;
    @JsonView({GroupViews.Name.class, StudentGroupView.Course.class})
    private String name;
    private String nameEng;
    private boolean hasGrade;
}
