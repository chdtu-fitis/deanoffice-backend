package ua.edu.chdtu.deanoffice.api.group;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.general.NamedDTO;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.repository.GroupRepository;
import ua.edu.chdtu.deanoffice.service.GroupService;
import ua.edu.chdtu.deanoffice.service.document.diploma.supplement.GraduateService;

import java.lang.reflect.Type;
import java.util.List;

@RestController
@RequestMapping("/groups")
public class GroupController {
    @Autowired
    private GraduateService graduateService;
    @Autowired
    private GroupService groupService;

    @RequestMapping(method = RequestMethod.GET, path = "/graduates")
    public ResponseEntity<List<NamedDTO>> getGraduateGroups(@RequestParam Integer degreeId) {
        List<StudentGroup> groups = graduateService.getGraduateGroups(degreeId);
        ModelMapper modelMapper = new ModelMapper();
        Type listType = new TypeToken<List<NamedDTO>>() {}.getType();
        List<NamedDTO> groupDTOs = modelMapper.map(groups, listType);
        return ResponseEntity.ok(groupDTOs);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{id}/students")
    public ResponseEntity<List<PersonFullNameDTO>> getGroupStudents(@PathVariable Integer id) {
        List<Student> students = groupService.getGroupStudents(id);
        ModelMapper modelMapper = new ModelMapper();
        Type listType = new TypeToken<List<PersonFullNameDTO>>() {}.getType();
        List<PersonFullNameDTO> studentDTOs = modelMapper.map(students, listType);
        return ResponseEntity.ok(studentDTOs);
    }
}
