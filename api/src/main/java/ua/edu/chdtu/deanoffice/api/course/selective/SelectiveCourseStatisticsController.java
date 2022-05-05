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

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;

@RestController
@RequestMapping("/selective-courses-statistics")
public class SelectiveCourseStatisticsController {
    private SelectiveCourseStatisticsService selectiveCourseStatisticsService;

    public SelectiveCourseStatisticsController(SelectiveCourseStatisticsService selectiveCourseStatisticsService) {
        this.selectiveCourseStatisticsService = selectiveCourseStatisticsService;
    }

    @GetMapping("/registered-percent")
    public ResponseEntity getRegisteredStudentsPercent(@RequestParam @NotNull @Min(2010) @Max(2060) int studyYear,
                                                       @RequestParam @NotNull int degreeId,
                                                       @RequestParam @NotNull SelectiveStatisticsCriteria selectiveStatisticsCriteria) {
        List<IPercentStudentsRegistrationOnCourses> registeredStudentsPercent = selectiveCourseStatisticsService.getStudentsPercentWhoChosenSelectiveCourse(studyYear, degreeId, selectiveStatisticsCriteria);
        switch (selectiveStatisticsCriteria){
            case YEAR:
                return ResponseEntity.ok(map(registeredStudentsPercent, StudentsRegistrationOnCoursesPercentDTO.class));
            case FACULTY:
                return ResponseEntity.ok(map(registeredStudentsPercent, StudentsRegistrationOnCoursesByFacultyPercentDTO.class));
            case GROUP:
                return ResponseEntity.ok(map(registeredStudentsPercent, StudentsRegistrationOnCoursesByGroupPercentDTO.class));
            case FACULTY_AND_SPECIALIZATION:
                return ResponseEntity.ok(map(registeredStudentsPercent, StudentsRegistrationOnCoursesByFacultyAndSpecializationPercentDTO.class));
            case FACULTY_AND_YEAR:
                return ResponseEntity.ok(map(registeredStudentsPercent, StudentsRegistrationOnCoursesByFacultyAndCoursesPercentDTO.class));
            default:
                return ResponseEntity.ok(map(registeredStudentsPercent, StudentsRegistrationOnCoursesByFacultyAndCourseAndSpecializationPercentDTO.class));
        }
    }
}
