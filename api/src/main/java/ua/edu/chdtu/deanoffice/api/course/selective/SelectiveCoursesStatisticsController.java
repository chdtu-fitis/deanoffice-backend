package ua.edu.chdtu.deanoffice.api.course.selective;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.course.selective.dto.statistics.StudentsRegistrationOnCoursesByFacultyAndCourseAndSpecializationPercentDTO;
import ua.edu.chdtu.deanoffice.api.course.selective.dto.statistics.StudentsRegistrationOnCoursesByFacultyAndCoursesPercentDTO;
import ua.edu.chdtu.deanoffice.api.course.selective.dto.statistics.StudentsRegistrationOnCoursesByFacultyAndSpecializationPercentDTO;
import ua.edu.chdtu.deanoffice.api.course.selective.dto.statistics.StudentsRegistrationOnCoursesByFacultyPercentDTO;
import ua.edu.chdtu.deanoffice.api.course.selective.dto.statistics.StudentsRegistrationOnCoursesByGroupPercentDTO;
import ua.edu.chdtu.deanoffice.api.course.selective.dto.statistics.StudentsRegistrationOnCoursesPercentDTO;
import ua.edu.chdtu.deanoffice.service.course.selective.statistics.IPercentStudentsRegistrationOnCourses;
import ua.edu.chdtu.deanoffice.service.course.selective.statistics.SelectiveCourseStatisticsService;
import ua.edu.chdtu.deanoffice.service.course.selective.statistics.SelectiveStatisticsCriteria;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;

@RestController
@RequestMapping("/selective-courses-statistics")
public class SelectiveCoursesStatisticsController {
    private SelectiveCourseStatisticsService selectiveCourseStatisticsService;

    public SelectiveCoursesStatisticsController(SelectiveCourseStatisticsService selectiveCourseStatisticsService) {
        this.selectiveCourseStatisticsService = selectiveCourseStatisticsService;
    }

    @GetMapping("/registered-percent")
    public ResponseEntity getRegisteredStudentsPercent(@RequestParam @NotNull @Min(2010) int studyYear,
                                                       @RequestParam @NotNull int degreeId,
                                                       @RequestParam @NotNull SelectiveStatisticsCriteria selectiveStatisticsCriteria) {
        List<IPercentStudentsRegistrationOnCourses> registeredStudentsPercent = selectiveCourseStatisticsService.getStudentsPercentWhoChosenSelectiveCourse(studyYear, degreeId, selectiveStatisticsCriteria);
        if (selectiveStatisticsCriteria == SelectiveStatisticsCriteria.YEAR) {
            return ResponseEntity.ok(map(registeredStudentsPercent, StudentsRegistrationOnCoursesPercentDTO.class));
        }
        else if (selectiveStatisticsCriteria == SelectiveStatisticsCriteria.FACULTY){
            return ResponseEntity.ok(map(registeredStudentsPercent, StudentsRegistrationOnCoursesByFacultyPercentDTO.class));
        }
        else if (selectiveStatisticsCriteria == SelectiveStatisticsCriteria.GROUP) {
            return ResponseEntity.ok(map(registeredStudentsPercent, StudentsRegistrationOnCoursesByGroupPercentDTO.class));
        }
        else if (selectiveStatisticsCriteria == SelectiveStatisticsCriteria.FACULTY_AND_SPECIALIZATION){
            return ResponseEntity.ok(map(registeredStudentsPercent, StudentsRegistrationOnCoursesByFacultyAndSpecializationPercentDTO.class));
        }
        else if (selectiveStatisticsCriteria == SelectiveStatisticsCriteria.FACULTY_AND_YEAR){
            return ResponseEntity.ok(map(registeredStudentsPercent, StudentsRegistrationOnCoursesByFacultyAndCoursesPercentDTO.class));
        }
        else {                         //FACULTY_AND_COURSES_AND_SPECIALIZATION
            return ResponseEntity.ok(map(registeredStudentsPercent, StudentsRegistrationOnCoursesByFacultyAndCourseAndSpecializationPercentDTO.class));
        }
    }
}
