package ua.edu.chdtu.deanoffice.api.group;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.general.dto.NamedDTO;
import ua.edu.chdtu.deanoffice.api.group.dto.StudentGroupDTO;
import ua.edu.chdtu.deanoffice.api.group.dto.StudentGroupShortDTO;
import ua.edu.chdtu.deanoffice.api.group.dto.StudentGroupView;
import ua.edu.chdtu.deanoffice.entity.StudentAcademicVacation;
import ua.edu.chdtu.deanoffice.entity.StudentExpel;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.service.StudentAcademicVacationService;
import ua.edu.chdtu.deanoffice.service.StudentExpelService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;

import java.util.List;

import static ua.edu.chdtu.deanoffice.api.general.parser.Parser.parse;

@RestController
public class GroupController {
    private StudentGroupService studentGroupService;
    private StudentExpelService studentExpelService;
    private StudentAcademicVacationService studentAcademicVacationService;

    @Autowired
    public GroupController(
            StudentGroupService studentGroupService,
            StudentExpelService studentExpelService,
            StudentAcademicVacationService studentAcademicVacationService
    ) {
        this.studentGroupService = studentGroupService;
        this.studentExpelService = studentExpelService;
        this.studentAcademicVacationService = studentAcademicVacationService;
    }

    @JsonView(StudentGroupView.WithStudents.class)
    @GetMapping("/groups/graduates")
    public ResponseEntity getGraduateGroups(@RequestParam int degreeId) {
        List<StudentGroup> groups = studentGroupService.getGraduateGroups(degreeId);
        return ResponseEntity.ok(parse(groups, StudentGroupShortDTO.class));
    }

    @GetMapping("/groups/filter")
    @JsonView(StudentGroupView.WithStudents.class)
    public ResponseEntity getGroupsByDegreeAndYear(
            @RequestParam Integer degreeId,
            @RequestParam Integer year
    ) {
        List<StudentGroup> groups = studentGroupService.getGroupsByDegreeAndYear(degreeId, year);
        return ResponseEntity.ok(parse(groups, StudentGroupDTO.class));
    }

    @GetMapping("courses/{courseId}/groups")
    public ResponseEntity getGroupsByCourse(@PathVariable int courseId) {
        List<StudentGroup> studentGroups = studentGroupService.getGroupsByCourse(courseId);
        return ResponseEntity.ok(parse(studentGroups, NamedDTO.class));
    }

    @GetMapping("/groups")
    @JsonView(StudentGroupView.AllGroupData.class)
    public ResponseEntity getActiveGroups(
            @RequestParam(value = "only-active", required = false, defaultValue = "true") boolean onlyActive
    ) {
        List<StudentGroup> studentGroups = studentGroupService.getAllByActive(onlyActive);
        return ResponseEntity.ok(parse(studentGroups, StudentGroupDTO.class));
    }

    @GetMapping("/students/degrees/expels/{student_expel_id}/renew-groups")
    public ResponseEntity getGroupsForRenewExpelledStudent(@PathVariable("student_expel_id") Integer studentExpelId) {
        StudentExpel studentExpel = studentExpelService.getById(studentExpelId);
        int degreeId = studentExpel.getStudentDegree().getDegree().getId();
        List<StudentGroup> studentGroups = studentGroupService.getGroupsByDegreeAndYear(degreeId, studentExpel.getStudyYear());
        return ResponseEntity.ok(parse(studentGroups, NamedDTO.class));
    }

    @GetMapping("/students/degrees/academic-vacation/{student_academic_vacation_id}/renew-groups")
    public ResponseEntity getGroupsForRenewAcademicVacationStudent(
            @PathVariable("student_academic_vacation_id") Integer studentAcademicVacationId
    ) {
        StudentAcademicVacation studentAcademicVacation = studentAcademicVacationService.getById(studentAcademicVacationId);
        int degreeId = studentAcademicVacation.getStudentDegree().getDegree().getId();
        List<StudentGroup> studentGroups = studentGroupService.getGroupsByDegreeAndYear(degreeId, studentAcademicVacation.getStudyYear());
        return ResponseEntity.ok(parse(studentGroups, NamedDTO.class));
    }
}
