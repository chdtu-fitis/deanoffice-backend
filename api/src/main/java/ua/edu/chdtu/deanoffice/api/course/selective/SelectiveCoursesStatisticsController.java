package ua.edu.chdtu.deanoffice.api.course.selective;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.course.selective.dto.statistics.StudentsRegistrationOnCoursesPercentDTO;
import ua.edu.chdtu.deanoffice.service.course.selective.statistics.SelectiveCourseStatisticsService;
import ua.edu.chdtu.deanoffice.service.course.selective.statistics.StudentsRegistrationOnCoursesPercent;

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
                                                       @RequestParam @NotNull int degreeId) {
        List<StudentsRegistrationOnCoursesPercent> registeredStudentsPercent = selectiveCourseStatisticsService.getStudentsPercentWhoChosenSelectiveCourse(studyYear, degreeId);
        return ResponseEntity.ok(map(registeredStudentsPercent, StudentsRegistrationOnCoursesPercentDTO.class));
    }

}
