package ua.edu.chdtu.deanoffice.service.course.selective;

import lombok.AllArgsConstructor;
import lombok.Data;
import ua.edu.chdtu.deanoffice.entity.PeriodCaseEnum;
import ua.edu.chdtu.deanoffice.entity.TuitionTerm;

@Data
@AllArgsConstructor
public class PeriodCase {
    private int degreeId;
    private int year;
    private TuitionTerm tuitionTerm;
    private PeriodCaseEnum periodCaseEnum;
}
