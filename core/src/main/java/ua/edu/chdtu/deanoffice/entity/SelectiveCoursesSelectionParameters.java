package ua.edu.chdtu.deanoffice.entity;

import lombok.Data;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Data
public class SelectiveCoursesSelectionParameters extends BaseEntity {
    @Temporal(TemporalType.DATE)
    private Date firstRoundStartDate;
    @Temporal(TemporalType.DATE)
    private Date firstRoundEndSecondRoundStartDate;
    @Temporal(TemporalType.DATE)
    private Date secondRoundEndDate;
    private int minimumCountOfStudents;
}


