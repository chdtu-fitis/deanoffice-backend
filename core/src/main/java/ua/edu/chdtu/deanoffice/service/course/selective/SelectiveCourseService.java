package ua.edu.chdtu.deanoffice.service.course.selective;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.SelectiveCourse;
import ua.edu.chdtu.deanoffice.repository.SelectiveCourseRepository;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;

import java.util.List;

@Service
public class SelectiveCourseService {
    private SelectiveCourseRepository selectiveCourseRepository;
    private CurrentYearService currentYearService;

    public SelectiveCourseService(SelectiveCourseRepository selectiveCourseRepository, CurrentYearService currentYearService) {
        this.selectiveCourseRepository = selectiveCourseRepository;
        this.currentYearService = currentYearService;
    }

    public List<SelectiveCourse> getSelectiveCoursesByStudyYearAndDegreeAndSemester(Integer studyYear, int degreeId, int semester) {
        if (studyYear == null) {
            studyYear = currentYearService.getYear() + 1;
        }
        return selectiveCourseRepository.findAllAvailableByStudyYearAndDegreeAndSemester(studyYear, degreeId, semester);
    }

    public SelectiveCourse getById(Integer id) {
        return selectiveCourseRepository.findOne(id);
    }

    public void delete(SelectiveCourse selectiveCourse) {
        selectiveCourse.setAvailable(false);
        selectiveCourseRepository.save(selectiveCourse);
    }

    public SelectiveCourse create(SelectiveCourse selectiveCourse) {
        selectiveCourse.setAvailable(true);
        return this.selectiveCourseRepository.save(selectiveCourse);
    }

    public void restore(SelectiveCourse selectiveCourse) {
        selectiveCourse.setAvailable(true);
        selectiveCourseRepository.save(selectiveCourse);
    }

    public SelectiveCourse update(SelectiveCourse selectiveCourse) {
        return selectiveCourseRepository.save(selectiveCourse);
    }
}