package ua.edu.chdtu.deanoffice.api.course.selective;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.course.selective.dto.StudentSelectiveCourseMoreOrLessNormDTO;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.service.course.selective.SelectiveCourseAnomaliesService;
import ua.edu.chdtu.deanoffice.service.course.selective.statistics.IStudentsNotRightSelectiveCoursesNumber;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/selective-courses-anomaly")
public class SelectiveCourseAnomalyController {
    private SelectiveCourseAnomaliesService selectiveCourseAnomaliesService;

    public SelectiveCourseAnomalyController(SelectiveCourseAnomaliesService selectiveCourseAnomaliesService) {
        this.selectiveCourseAnomaliesService = selectiveCourseAnomaliesService;
    }

    @GetMapping
    public ResponseEntity<List<StudentSelectiveCourseMoreOrLessNormDTO>> getStudentsSelectedSelectiveCoursesMoreNorm(
            @RequestParam int degreeId,
            @RequestParam @Min(2020) @Max(2040) int studyYear,
            @RequestParam(required = false) int course,
            @RequestParam boolean moreNorm) {

        List<IStudentsNotRightSelectiveCoursesNumber> studentDegrees = selectiveCourseAnomaliesService.getStudentsSelectedSelectiveCourses(degreeId, studyYear, course, moreNorm);
        List<StudentSelectiveCourseMoreOrLessNormDTO> result = new ArrayList<>();
        for (IStudentsNotRightSelectiveCoursesNumber studentsNotRightSelectiveCoursesNumber : studentDegrees) {
            StudentSelectiveCourseMoreOrLessNormDTO studentsNotRegisteredForSelectiveCoursesDTO = new StudentSelectiveCourseMoreOrLessNormDTO(
                    studentsNotRightSelectiveCoursesNumber.getStudentDegreeId(), studentsNotRightSelectiveCoursesNumber.getName(),
                    studentsNotRightSelectiveCoursesNumber.getSurname(), studentsNotRightSelectiveCoursesNumber.getFacultyName(),
                    studentsNotRightSelectiveCoursesNumber.getSpecialityCode(), studentsNotRightSelectiveCoursesNumber.getYear(),
                    studentsNotRightSelectiveCoursesNumber.getGroup(), studentsNotRightSelectiveCoursesNumber.getCoursesNumber()
            );
            result.add(studentsNotRegisteredForSelectiveCoursesDTO);
        }
        return ResponseEntity.ok(result);
    }
}
