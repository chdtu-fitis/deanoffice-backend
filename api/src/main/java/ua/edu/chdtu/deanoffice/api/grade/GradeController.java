package ua.edu.chdtu.deanoffice.api.grade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.entity.Grade;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;
import ua.edu.chdtu.deanoffice.service.GradeService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ua.edu.chdtu.deanoffice.api.general.parser.Parser.parse;

@Controller
@RequestMapping("/grades")
public class GradeController {
    private GradeService gradeService;
    private StudentDegreeService studentDegreeService;
    private CourseForGroupService courseForGroupService;

    @Autowired
    public GradeController(
            GradeService gradeService,
            StudentDegreeService studentDegreeService,
            CourseForGroupService courseForGroupService
    ) {
        this.gradeService = gradeService;
        this.studentDegreeService = studentDegreeService;
        this.courseForGroupService = courseForGroupService;
    }

    @PutMapping("/")
    public ResponseEntity<List<GradeDTO>> putGrades(@RequestBody List<Grade> grades) {
        this.gradeService.insertGrades(grades);
        return ResponseEntity.ok(new ArrayList<>());
    }

    @GetMapping("/{groupId}/{semester}")
    public ResponseEntity<List<GradeDTO>> getGrades(
            @PathVariable Integer groupId,
            @PathVariable Integer semester) {
        List<Grade> grades = this.gradeService.getGradesForStudents(getStudentsIdsByGroupId(groupId),
                getCoursesIdsByGroupIdAndSemester(groupId, semester));
        return ResponseEntity.ok(parse(grades, GradeDTO.class));
    }

    private List<Integer> getStudentsIdsByGroupId(Integer groupId) {
        List<StudentDegree> students = this.studentDegreeService.getAllByGroupId(groupId);
        return students.stream().map(BaseEntity::getId).collect(Collectors.toList());
    }

    private List<Integer> getCoursesIdsByGroupIdAndSemester(Integer groupId, Integer semester) {
        List<CourseForGroup> courses = this.courseForGroupService.getCoursesForGroupBySemester(groupId, semester);
        return courses.stream().map(course -> course.getCourse().getId()).collect(Collectors.toList());
    }
}
