package ua.edu.chdtu.deanoffice.service.selective.courses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.SelectiveCourse;
import ua.edu.chdtu.deanoffice.repository.SelectiveCourseRepository;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;
import java.util.List;

@Service
public class SelectiveCourseService {
    private SelectiveCourseRepository selectiveCourseRepository;
    private CurrentYearService currentYearService;

    @Autowired
    public SelectiveCourseService(SelectiveCourseRepository selectiveCourseRepository, CurrentYearService currentYearService) {
        this.selectiveCourseRepository = selectiveCourseRepository;
        this.currentYearService = currentYearService;
    }

    public List<SelectiveCourse> getSelectiveCoursesInCurrentYear(Integer studyYear) {
        if (studyYear == null) {
            studyYear = currentYearService.getYear() + 1;
        }
        return selectiveCourseRepository.findAllAvailableByStudyYear(studyYear);
    }
}