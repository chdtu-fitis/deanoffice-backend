package ua.edu.chdtu.deanoffice.service.course.selective.statistics;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.repository.SelectiveCoursesStudentDegreesRepository;
import java.util.List;

@Service
public class SelectiveCourseStatisticsService {
    private SelectiveCoursesStudentDegreesRepository selectiveCoursesStudentDegreesRepository;

    public SelectiveCourseStatisticsService(SelectiveCoursesStudentDegreesRepository selectiveCoursesStudentDegreesRepository){
        this.selectiveCoursesStudentDegreesRepository = selectiveCoursesStudentDegreesRepository;
    }

    public List<StudentsRegistrationOnCoursesPercent> getStudentsPercentWhoChosenSelectiveCourse(int studyYear, int degreeId) {
        int currentYear = selectiveCoursesStudentDegreesRepository.getCurrentYear();
        List<StudentsRegistrationOnCoursesPercent> studentDegrees = selectiveCoursesStudentDegreesRepository.findPercentStudentsWhoChosenSelectiveCourse(studyYear, degreeId, currentYear);
        return (studentDegrees);
    }
}
