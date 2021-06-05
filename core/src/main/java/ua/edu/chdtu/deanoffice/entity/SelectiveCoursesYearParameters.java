package ua.edu.chdtu.deanoffice.entity;

import lombok.Data;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Enumerated;
import java.util.Date;

@Entity
@Data
public class SelectiveCoursesYearParameters extends BaseEntity {
    @Temporal(TemporalType.TIMESTAMP)
    private Date firstRoundStartDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date firstRoundEndDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date secondRoundStartDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date secondRoundEndDate;
    private int studyYear;
    private int bachelorGeneralMinStudentsCount;
    private int bachelorProfessionalMinStudentsCount;
    private int masterGeneralMinStudentsCount;
    private int masterProfessionalMinStudentsCount;
    private int phdGeneralMinStudentsCount;
    private int phdProfessionalMinStudentsCount;
    private int maxStudentsCount;
    @Enumerated(EnumType.STRING)
    private PeriodCaseEnum periodCase;
}
