package ua.edu.chdtu.deanoffice.api.group.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.Specialization;

import java.math.BigDecimal;

@Getter
@Setter
public class GroupDTO {
    @JsonView(GroupViews.Name.class)
    private int id;
    @JsonView(GroupViews.Name.class)
    private String name;
    private Specialization specialization;
    private int creationYear;
    private char tuitionForm = 'f';//f - fulltime, e - extramural
    private char tuitionTerm = 'r';//r - regular, s - shortened
    @JsonView(GroupViews.Name.class)
    private int studySemesters;
    private BigDecimal studyYears;
    private int beginYears;
    private boolean active;
}
