package ua.edu.chdtu.deanoffice.service.course.selective.statistics;

import java.util.List;

public interface IStatisticsCondition {
    boolean hasToBeCounted(IPercentStudentsRegistrationOnCourses as,
                           IPercentStudentsRegistrationOnCourses count);
}
