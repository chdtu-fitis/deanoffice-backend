package ua.edu.chdtu.deanoffice.api.course.selective;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.course.selective.dto.statistics.StudentsNotRegisteredForSelectiveCoursesDTO;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.service.course.selective.statistics.SelectiveCourseStatisticsService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/selective-courses-statistics")
public class SelectiveCourseStatisticsController {
    private SelectiveCourseStatisticsService selectiveCourseStatisticsService;

    public SelectiveCourseStatisticsController(SelectiveCourseStatisticsService selectiveCourseStatisticsService) {
        this.selectiveCourseStatisticsService = selectiveCourseStatisticsService;
    }

    @GetMapping
    public ResponseEntity<List<StudentsNotRegisteredForSelectiveCoursesDTO>> getStudentsNotSelectedSelectiveCourses(
            @RequestParam int degreeId,
            @RequestParam int studyYear) {
        List<StudentDegree> studentDegrees = selectiveCourseStatisticsService.getStudentsNotSelectedSelectiveCourses(degreeId, studyYear);
        List<StudentsNotRegisteredForSelectiveCoursesDTO> result = new ArrayList<>();
        for (StudentDegree studentDegree : studentDegrees) {
            StudentsNotRegisteredForSelectiveCoursesDTO studentsNotRegisteredForSelectiveCoursesDTO = new StudentsNotRegisteredForSelectiveCoursesDTO(
                    studentDegree.getStudent().getSurname() + " " + studentDegree.getStudent().getName(),
                    studentDegree.getSpecialization().getFaculty().getAbbr(),
                    studentDegree.getSpecialization().getSpeciality().getCode(),
                    studentDegree.getStudentGroup() != null ? studentDegree.getStudentGroup().getName() : "",
                    studentDegree.getSpecialization().getDepartment().getAbbr());
            result.add(studentsNotRegisteredForSelectiveCoursesDTO);
        }
        return ResponseEntity.ok(result);
    }
}
