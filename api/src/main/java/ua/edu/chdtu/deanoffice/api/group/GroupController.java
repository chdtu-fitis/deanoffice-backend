package ua.edu.chdtu.deanoffice.api.group;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.dto.NamedDTO;
import ua.edu.chdtu.deanoffice.api.general.mapper.Mapper;
import ua.edu.chdtu.deanoffice.api.group.dto.StudentGroupDTO;
import ua.edu.chdtu.deanoffice.api.group.dto.StudentGroupShortDTO;
import ua.edu.chdtu.deanoffice.api.group.dto.StudentGroupView;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.Specialization;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;
import ua.edu.chdtu.deanoffice.service.CurrentYearService;
import ua.edu.chdtu.deanoffice.service.SpecializationService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


import static java.util.Arrays.asList;
import static ua.edu.chdtu.deanoffice.api.general.Util.getNewResourceLocation;

@RestController
public class GroupController {
    private final StudentGroupService studentGroupService;
    private final SpecializationService specializationService;
    private final CurrentYearService currentYearService;
    private final StudentDegreeService studentDegreeService;

    @Autowired
    public GroupController(
            StudentGroupService studentGroupService,
            SpecializationService specializationService,
            CurrentYearService currentYearService,
            StudentDegreeService studentDegreeService
    ) {
        this.studentGroupService = studentGroupService;
        this.currentYearService = currentYearService;
        this.specializationService = specializationService;
        this.studentDegreeService = studentDegreeService;
    }

    @JsonView(StudentGroupView.WithStudents.class)
    @GetMapping("/groups/graduates")
    public ResponseEntity getGraduateGroups(@RequestParam int degreeId, @CurrentUser ApplicationUser user) {
        List<StudentGroup> groups = studentGroupService.getGraduateGroups(degreeId, user.getFaculty().getId());
        return ResponseEntity.ok(Mapper.map(groups, StudentGroupShortDTO.class));
    }

    @GetMapping("/groups/filter")
    @JsonView(StudentGroupView.WithStudents.class)
    public ResponseEntity getGroupsByDegreeAndYear(
            @RequestParam Integer degreeId,
            @RequestParam Integer year,
            @CurrentUser ApplicationUser user
    ) {
        List<StudentGroup> groups = studentGroupService.getGroupsByDegreeAndYear(degreeId, year, user.getFaculty().getId());
        return ResponseEntity.ok(Mapper.map(groups, StudentGroupDTO.class));
    }

    @GetMapping("courses/{courseId}/groups")
    public ResponseEntity getGroupsByCourse(@PathVariable int courseId, @CurrentUser ApplicationUser user) {
        List<StudentGroup> studentGroups = studentGroupService.getGroupsByCourse(courseId, user.getFaculty().getId());
        return ResponseEntity.ok(Mapper.map(studentGroups, NamedDTO.class));
    }

    @GetMapping("/groups")
    @JsonView(StudentGroupView.AllGroupData.class)
    public ResponseEntity getActiveGroups(
            @RequestParam(value = "only-active", required = false, defaultValue = "true") boolean onlyActive,
            @CurrentUser ApplicationUser user
    ) {
        List<StudentGroup> studentGroups = studentGroupService.getAllByActive(onlyActive, user.getFaculty().getId());
        return ResponseEntity.ok(Mapper.map(studentGroups, StudentGroupDTO.class));
    }

    @JsonView(StudentGroupView.AllGroupData.class)
    @PostMapping("/groups")
    public ResponseEntity createGroup(@RequestBody StudentGroupDTO studentGroupDTO) {
        try {
            if (studentGroupDTO.getId() != null) {
                throwException("Group`s id must be null");
            }
            StudentGroup studentGroup = create(studentGroupDTO);
            studentGroup.setCreationYear(currentYearService.getYear());
            studentGroup.setActive(true);
            studentGroup = studentGroupService.save(studentGroup);

            URI location = getNewResourceLocation(studentGroup.getId());
            return ResponseEntity.created(location).body(studentGroup);
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private void throwException(String message) throws Exception {
        throw new Exception(message);
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, GroupController.class);
    }

    private StudentGroup create(StudentGroupDTO studentGroupDTO) {
        StudentGroup studentGroup = (StudentGroup) Mapper.strictMap(studentGroupDTO, StudentGroup.class);
        Specialization specialization = specializationService.getById(studentGroupDTO.getSpecialization().getId());
        studentGroup.setSpecialization(specialization);
        return studentGroup;
    }

    @JsonView(StudentGroupView.AllGroupData.class)
    @GetMapping("/groups/{group_id}")
    public ResponseEntity getGroupById(@PathVariable(value = "group_id") Integer groupId) {
        StudentGroup studentGroup = studentGroupService.getById(groupId);
        return ResponseEntity.ok(Mapper.map(studentGroup, StudentGroupDTO.class));
    }

    @PutMapping("/groups")
    public ResponseEntity updateGroup(@RequestBody StudentGroupDTO studentGroupDTO) {
        try {
            if (studentGroupDTO.getId() == null) {
                throwException("Group`s id must not be null");
            } else if (studentGroupDTO.getId().equals(0)) {
                throwException("Group`s id must not be null");
            }
            if (!studentGroupDTO.isActive()) {
                throwException("You can not update inactive group");
            }
            StudentGroup studentGroup = create(studentGroupDTO);
            List<StudentDegree> studentDegrees = studentDegreeService.getAllByGroupId(studentGroup.getId());
            studentGroup.setStudentDegrees(studentDegrees);
            studentGroupService.save(studentGroup);

            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @DeleteMapping("/groups/{group_ids}")
    public ResponseEntity deleteGroup(@PathVariable("group_ids") Integer[] groupIds) {
        List<StudentGroup> studentGroups = studentGroupService.getByIds(groupIds);
        if (studentGroups.size() != groupIds.length) {
            return ExceptionHandlerAdvice.handleException(
                    "Not found groups " + Arrays.toString(findNouFoundStudentGroups(studentGroups, asList(groupIds))),
                    GroupController.class,
                    HttpStatus.NOT_FOUND
            );
        }
        try {
            if (hasInactiveStudentGroup(studentGroups)) {
                throwException("Groups " + Arrays.toString(findInactiveStudentGroup(studentGroups).toArray()) + " already inactive");
            }
            studentGroupService.delete(studentGroups);
            return ResponseEntity.noContent().build();
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private Integer[] findNouFoundStudentGroups(List<StudentGroup> found, List<Integer> initial) {
        List<Integer> foundIds = found.stream().map(BaseEntity::getId).collect(Collectors.toList());
        return (Integer[]) initial.stream().filter(integer -> findNouFoundStudentGroup(integer, foundIds)).toArray();
    }

    private boolean findNouFoundStudentGroup(Integer initialId, List<Integer> foundIds) {
        foundIds = foundIds.stream().filter(integer -> integer.equals(initialId)).collect(Collectors.toList());
        return foundIds.size() == 0;
    }

    private boolean hasInactiveStudentGroup(List<StudentGroup> studentGroups) {
        return findInactiveStudentGroup(studentGroups).size() != 0;
    }

    private List<StudentGroup> findInactiveStudentGroup(List<StudentGroup> studentGroups) {
        return studentGroups.stream().filter(studentGroup -> !studentGroup.isActive()).collect(Collectors.toList());
    }
}
