package ua.edu.chdtu.deanoffice.api.teacher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.general.dto.PersonFullNameDTO;
import ua.edu.chdtu.deanoffice.api.general.mapper.Mapper;
import ua.edu.chdtu.deanoffice.entity.Teacher;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.exception.UnauthorizedFacultyDataException;
import ua.edu.chdtu.deanoffice.service.TeacherService;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

import static ua.edu.chdtu.deanoffice.api.general.mapper.Mapper.map;

@RestController
@Validated
public class TeacherController {

    private TeacherService teacherService;

    @Autowired
    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping("/teachers-short")
    public ResponseEntity getAllActiveTeachersShort() {
        List<Teacher> teachers = teacherService.getTeachersByActive(true);
        return ResponseEntity.ok(map(teachers, PersonFullNameDTO.class));
    }

    @GetMapping("/teachers-full/search")
    public ResponseEntity getSearchedFacultyActiveTeachersShort(@RequestParam @NotBlank @Size(min=2) String searchStr) {
        List<Teacher> teachers = teacherService.getActiveFacultyTeachersBySurnamePart(searchStr);
        return ResponseEntity.ok(map(teachers, TeacherDTO.class));
    }

    @GetMapping("/teachers-full")
    public ResponseEntity getAllActiveTeachers(@RequestParam(required = false, defaultValue = "true") boolean active) {
        List<Teacher> teachers = teacherService.getTeachersByActive(active);
        return ResponseEntity.ok(map(teachers, TeacherDTO.class));
    }

    @GetMapping("/inactive-teachers")
    public ResponseEntity getInactiveTeachers() {
        List<Teacher> teachers = teacherService.getTeachersByActive(false);
        return ResponseEntity.ok(map(teachers, TeacherDTO.class));
    }

    @GetMapping("/teachers")
    public ResponseEntity getTeachers(@RequestParam(required = false, defaultValue = "true") boolean active) {
        List<Teacher> teachers = teacherService.getFacultyTeachers(active);
        return ResponseEntity.ok(map(teachers, TeacherDTO.class));
    }

    @Secured({"ROLE_DEANOFFICER", "ROLE_NAVCH_METHOD"})
    @PostMapping("/teachers")
    public ResponseEntity addTeacher(@Validated @RequestBody TeacherWriteDTO teacherDTO) {
        Teacher teacher = Mapper.strictMap(teacherDTO, Teacher.class);
        Teacher teacherAfterSave = teacherService.createTeacher(teacher);
        TeacherDTO teacherAfterSaveDTO = map(teacherAfterSave, TeacherDTO.class);
        return new ResponseEntity(teacherAfterSaveDTO, HttpStatus.CREATED);
    }

    @Secured({"ROLE_DEANOFFICER", "ROLE_NAVCH_METHOD"})
    @PutMapping("/teachers/{id}")
    public ResponseEntity changeTeacher(@PathVariable @Min(1) int id, @Validated @RequestBody TeacherWriteDTO teacherDTO)
            throws OperationCannotBePerformedException, UnauthorizedFacultyDataException {
        Teacher teacher = Mapper.strictMap(teacherDTO, Teacher.class);
        teacher.setId(id);
        Teacher savedTeacher = teacherService.updateTeacher(teacher);
        return new ResponseEntity(map(savedTeacher, TeacherDTO.class), HttpStatus.OK);
    }

    @Secured({"ROLE_DEANOFFICER", "ROLE_NAVCH_METHOD"})
    @DeleteMapping("/teachers/{teachersIds}")
    public ResponseEntity deleteTeachers(@PathVariable List<Integer> teachersIds) throws OperationCannotBePerformedException, UnauthorizedFacultyDataException {
        teacherService.deleteByIds(teachersIds);
        return ResponseEntity.noContent().build();
    }

    @Secured({"ROLE_DEANOFFICER", "ROLE_NAVCH_METHOD"})
    @PutMapping("/teachers/restore")
    public ResponseEntity restoreTeachers(@RequestParam @NotEmpty(message="Потрібно вказати хоча б одного викладача для відновлення")
                                            List<Integer> teachersIds) throws OperationCannotBePerformedException, UnauthorizedFacultyDataException {
        teacherService.restoreByIds(teachersIds);
        return ResponseEntity.noContent().build();
    }
}
