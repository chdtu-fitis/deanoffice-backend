package ua.edu.chdtu.deanoffice.api.grade;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.entity.Grade;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;
import ua.edu.chdtu.deanoffice.service.CourseForGroupService;
import ua.edu.chdtu.deanoffice.service.GradeService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/grades")
public class GradeController {
    private GradeService gradeService;
    private StudentGroupService studentGroupService;
    private CourseForGroupService courseForGroupService;

    @Autowired
    public GradeController(GradeService gradeService,
                           StudentGroupService studentGroupService,
                           CourseForGroupService courseForGroupService
    ) {
        this.gradeService = gradeService;
        this.studentGroupService = studentGroupService;
        this.courseForGroupService = courseForGroupService;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{groupId}/{semester}")
    public ResponseEntity<List<GradeDTO>> getGrades(
            @PathVariable Integer groupId,
            @PathVariable Integer semester) {
        List<Grade> grades = this.gradeService.getGradesForStudents(getStudentsIdsByGroupId(groupId),
                getCoursesIdsByGroupIdAndSemester(groupId, semester));
        return ResponseEntity.ok(parseToGradesForGradeDTO(grades));
    }

    private List<Integer> getStudentsIdsByGroupId(Integer groupId) {
        List<StudentDegree> students = this.studentGroupService.getStudentsByGroupId(groupId);
        return students.stream().map(BaseEntity::getId).collect(Collectors.toList());
    }

    private List<Integer> getCoursesIdsByGroupIdAndSemester(Integer groupId, Integer semester) {
        List<CourseForGroup> courses = this.courseForGroupService.getCoursesForGroupBySemester(groupId, semester);
        return courses.stream().map(course -> course.getCourse().getId()).collect(Collectors.toList());
    }

    private List<GradeDTO> parseToGradesForGradeDTO(List<Grade> gradesForGroupList) {
        Type listType = new TypeToken<List<GradeDTO>>() {}.getType();
        return new ModelMapper().map(gradesForGroupList, listType);
    }
}
