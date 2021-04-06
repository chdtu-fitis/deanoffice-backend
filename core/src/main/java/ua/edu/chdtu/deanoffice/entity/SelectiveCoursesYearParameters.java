package ua.edu.chdtu.deanoffice.entity;

import lombok.Data;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.Entity;
import java.time.LocalDate;

@Entity
@Data
public class SelectiveCoursesYearParameters extends BaseEntity {
    private LocalDate firstRoundStartDate;
    private LocalDate firstRoundEndDate;
    private LocalDate secondRoundEndDate;
    private int minStudentsCount;
}


