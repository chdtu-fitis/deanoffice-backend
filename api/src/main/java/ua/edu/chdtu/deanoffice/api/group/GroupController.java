package ua.edu.chdtu.deanoffice.api.group;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.dto.NamedDTO;
import ua.edu.chdtu.deanoffice.api.general.parser.Parser;
import ua.edu.chdtu.deanoffice.api.group.dto.StudentGroupDTO;
import ua.edu.chdtu.deanoffice.api.group.dto.StudentGroupShortDTO;
import ua.edu.chdtu.deanoffice.api.group.dto.StudentGroupView;
import ua.edu.chdtu.deanoffice.entity.Specialization;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;
import ua.edu.chdtu.deanoffice.service.SpecializationService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;

import java.net.URI;
import java.util.List;


import static ua.edu.chdtu.deanoffice.api.general.Util.getNewResourceLocation;

@RestController
public class GroupController {
    private final StudentGroupService studentGroupService;
    private final SpecializationService specializationService;
    private final CurrentYearService currentYearService;

    @Autowired
    public GroupController(
            StudentGroupService studentGroupService,
            SpecializationService specializationService,
            CurrentYearService currentYearService
    ) {
        this.studentGroupService = studentGroupService;
        this.specializationService = specializationService;
        this.currentYearService = currentYearService;
    }

    @JsonView(StudentGroupView.WithStudents.class)
    @GetMapping("/groups/graduates")
    public ResponseEntity getGraduateGroups(@RequestParam int degreeId) {
        List<StudentGroup> groups = studentGroupService.getGraduateGroups(degreeId);
        return ResponseEntity.ok(Parser.parse(groups, StudentGroupShortDTO.class));
    }

    @GetMapping("/groups/filter")
    @JsonView(StudentGroupView.WithStudents.class)
    public ResponseEntity getGroupsByDegreeAndYear(
            @RequestParam Integer degreeId,
            @RequestParam Integer year
    ) {
        List<StudentGroup> groups = studentGroupService.getGroupsByDegreeAndYear(degreeId, year);
        return ResponseEntity.ok(Parser.parse(groups, StudentGroupDTO.class));
    }

    @GetMapping("courses/{courseId}/groups")
    public ResponseEntity getGroupsByCourse(@PathVariable int courseId) {
        List<StudentGroup> studentGroups = studentGroupService.getGroupsByCourse(courseId);
        return ResponseEntity.ok(Parser.parse(studentGroups, NamedDTO.class));
    }

    @GetMapping("/groups")
    @JsonView(StudentGroupView.AllGroupData.class)
    public ResponseEntity getActiveGroups(
            @RequestParam(value = "only-active", required = false, defaultValue = "true") boolean onlyActive
    ) {
        List<StudentGroup> studentGroups = studentGroupService.getAllByActive(onlyActive);
        return ResponseEntity.ok(Parser.parse(studentGroups, StudentGroupDTO.class));
    }

    @JsonView(StudentGroupView.AllGroupData.class)
    @PostMapping("/groups")
    public ResponseEntity createGroup(@RequestBody StudentGroupDTO studentGroupDTO) {
        if (studentGroupDTO.getId() != null) {
            return handleException("Group`s id must be null");
        }

        try {
            StudentGroup studentGroup = create(studentGroupDTO);
            studentGroup = studentGroupService.save(studentGroup);

            URI location = getNewResourceLocation(studentGroup.getId());
            return ResponseEntity.created(location).body(studentGroup);
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private StudentGroup create(StudentGroupDTO studentGroupDTO) {
        StudentGroup studentGroup = (StudentGroup) Parser.strictParse(studentGroupDTO, StudentGroup.class);

        Specialization specialization = specializationService.getById(studentGroupDTO.getSpecializationId());
        studentGroup.setSpecialization(specialization);

        studentGroup.setCreationYear(currentYearService.getYear());
        studentGroup.setActive(true);

        return studentGroup;
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, GroupController.class);
    }

    private ResponseEntity handleException(String message) {
        return ExceptionHandlerAdvice.handleException(message, GroupController.class);
    }

    @JsonView(StudentGroupView.AllGroupData.class)
    @GetMapping("/groups/{group_id}")
    public ResponseEntity getGroupById(@PathVariable(value = "group_id") Integer groupId) {
        StudentGroup studentGroup = studentGroupService.getById(groupId);
        return ResponseEntity.ok(Parser.parse(studentGroup, StudentGroupDTO.class));
    }
}
