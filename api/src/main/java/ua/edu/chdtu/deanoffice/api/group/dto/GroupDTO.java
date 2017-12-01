package ua.edu.chdtu.deanoffice.api.group.dto;

import com.fasterxml.jackson.annotation.JsonView;
import ua.edu.chdtu.deanoffice.entity.Specialization;
import java.math.BigDecimal;

public class GroupDTO {
    @JsonView(GroupViews.Name.class)
    public int id;
    @JsonView(GroupViews.Name.class)
    public String name;
    public Specialization specialization;
    public int creationYear;
    public char tuitionForm = 'f';//f - fulltime, e - extramural
    public char tuitionTerm = 'r';//r - regular, s - shortened
    @JsonView(GroupViews.Name.class)
    public int studySemesters;
    public BigDecimal studyYears;
    public int beginYears;
    public boolean active;

}
