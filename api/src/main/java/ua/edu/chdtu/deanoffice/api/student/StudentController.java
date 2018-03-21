package ua.edu.chdtu.deanoffice.api.student;

import com.fasterxml.jackson.annotation.JsonView;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.student.dto.*;
import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.service.*;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice.handleException;
import static ua.edu.chdtu.deanoffice.api.general.Util.getNewResourceLocation;

@RestController
@RequestMapping("/students")
public class StudentController {
    @Autowired
    StudentDegreeService studentDegreeService;
    @Autowired
    StudentService studentService;
    @Autowired
    DegreeService degreeService;
    @Autowired
    StudentGroupService studentGroupService;
    @Autowired
    OrderReasonService orderReasonService;

    @JsonView(StudentDegreeViews.Simple.class)
    @GetMapping("/degrees")
    public ResponseEntity getActiveStudentsDegree(
            @RequestParam(value = "active", required = false, defaultValue = "true") boolean active
    ) {
        return ResponseEntity.ok(getActiveStudentDegree(active));
    }

    @JsonView(StudentDegreeViews.Detail.class)
    @GetMapping("/degrees/more-detail")
    public ResponseEntity getActiveStudentsDegree_moreDetail(
            @RequestParam(value = "active", required = false, defaultValue = "true") boolean active
    ) {
        return ResponseEntity.ok(getActiveStudentDegree(active));
    }

    private List<StudentDegreeDTO> getActiveStudentDegree(boolean active) {
        return parseToStudentDegreeDTO(studentDegreeService.getAllByActive(active));
    }

    private List<StudentDegreeDTO> parseToStudentDegreeDTO(List<StudentDegree> studentDegreeList) {
        return new ModelMapper().map(studentDegreeList, new TypeToken<List<StudentDegreeDTO>>() {}.getType());
    }

    @JsonView(StudentDegreeViews.Search.class)
    @GetMapping("/search")
    public List searchStudentByFullName(
            @RequestParam(value = "name", defaultValue = "", required = false) String name,
            @RequestParam(value = "surname", defaultValue = "", required = false) String surname,
            @RequestParam(value = "patronimic", defaultValue = "", required = false) String patronimic
    ) {
        List<Student> foundStudent = studentService.searchByFullName(name, surname, patronimic);
        List<StudentDTO> foundStudentDTO = parseToStudentDTO(foundStudent);
        foundStudentDTO.forEach(studentDTO -> {
            Student student = foundStudent.get(foundStudentDTO.indexOf(studentDTO));
            studentDTO.setGroups(getGroupNamesForStudent(student));
        });
        return foundStudentDTO;
    }

    private List<StudentDTO> parseToStudentDTO(List<Student> studentList) {
        return new ModelMapper().map(studentList, new TypeToken<List<StudentDTO>>() {
        }.getType());
    }

    private String getGroupNamesForStudent(Student student) {
        return student.getDegrees().stream()
                .map(studentDegree -> studentDegree.getStudentGroup().getName())
                .collect(Collectors.joining(", "));
    }

    @JsonView(StudentDegreeViews.Degree.class)
    @PostMapping("/degrees")
    public ResponseEntity createNewStudentDegree(
            @RequestBody() StudentDegreeDTO newStudentDegree,
            @RequestParam(value = "new_student", defaultValue = "false", required = false) boolean newStudent
    ) {
        Student student;
        StudentDegree studentDegree;

        try {
            if (newStudent) {
                student = createStudent(newStudentDegree.getStudent());
            } else {
                student = studentService.findById(newStudentDegree.getStudent().getId());
            }
            studentDegree = createStudentDegree(newStudentDegree, student);
        } catch (Exception exception) {
            return handleException(exception);
        }

        URI location = getNewResourceLocation(studentDegree.getId());
        return ResponseEntity.created(location).body(new ModelMapper().map(studentDegree, StudentDegreeDTO.class));
    }

    private Student createStudent(StudentDTO newStudentDTO) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        Student newStudent = modelMapper.map(newStudentDTO, Student.class);
        newStudent.setId(0);
        return studentService.create(newStudent);
    }

    private StudentDegree createStudentDegree(StudentDegreeDTO newStudentDegreeDTO, Student student) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        StudentDegree newStudentDegree = modelMapper.map(newStudentDegreeDTO, StudentDegree.class);
        newStudentDegree.setStudent(student);
        newStudentDegree.setStudentGroup(studentGroupService.getById(newStudentDegreeDTO.getStudentGroupId()));
        newStudentDegree.setDegree(newStudentDegree.getStudentGroup().getSpecialization().getDegree());
        newStudentDegree.setActive(true);

        PreviousDiplomaDTO previousDiplomaDTO = getPreviousDiploma(newStudentDegree);
        newStudentDegree.setPreviousDiplomaType(previousDiplomaDTO.getType());
        if (newStudentDegree.getDegree().getId() != 1) {
            newStudentDegree.setPreviousDiplomaNumber(previousDiplomaDTO.getNumber());
            newStudentDegree.setPreviousDiplomaDate(previousDiplomaDTO.getDate());
        }
        return studentDegreeService.save(newStudentDegree);
    }

    private PreviousDiplomaDTO getPreviousDiploma(StudentDegree studentDegree) {
        EducationDocument educationDocument = getPreviousDiplomaType(studentDegree);
        StudentDegree firstStudentDegree = studentDegreeService.getFirstStudentDegree(studentDegree.getStudent().getId());
        if (firstStudentDegree != null) {
            return new PreviousDiplomaDTO(
                    firstStudentDegree.getPreviousDiplomaDate(),
                    firstStudentDegree.getPreviousDiplomaNumber(),
                    educationDocument
            );
        }
        return new PreviousDiplomaDTO(educationDocument);
    }

    private EducationDocument getPreviousDiplomaType(StudentDegree studentDegree) {
        if (EducationDocument.isExist(studentDegree.getPreviousDiplomaType())) {
            return studentDegree.getPreviousDiplomaType();
        }
        return EducationDocument.getPreviousDiplomaType(studentDegree.getDegree().getId());
    }



    @JsonView(StudentDegreeViews.Personal.class)
    @GetMapping("/{id}")
    public ResponseEntity getAllStudentsId(
            @PathVariable("id") Integer studentId
    ) {
        return ResponseEntity.ok(parseToStudentDTO(studentService.findById(studentId)));
    }

    private StudentDTO parseToStudentDTO(Student student) {
        return new ModelMapper().map(student, StudentDTO.class);
    }

    @PutMapping("/")
    public ResponseEntity updateStudent(@RequestBody Student student) {
        Student upStudent;
        try {
            upStudent = studentService.update(student);
        } catch (Exception exception) {
            return handleException(exception);
        }
        return ResponseEntity.ok(parseToStudentDTO(upStudent));
    }


    @JsonView(StudentDegreeViews.Degrees.class)
    @GetMapping("/{id}/degrees")
    public ResponseEntity getAllStudentsDegreeById(
            @PathVariable("id") Integer studentId
    ) {
        return ResponseEntity.ok(parseToStudentDTO(studentService.findById(studentId)));
    }

    @JsonView(StudentDegreeViews.Degrees.class)
    @PutMapping("/{id}/degrees")
    public ResponseEntity updateStudentDegrees(
            @RequestBody List<StudentDegreeDTO> studentDegreesDTO,
            @PathVariable(value = "id") Integer studentId
    ) {
        Student upStudent;
        try {
            ModelMapper modelMapper = new ModelMapper();
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            Type type = new TypeToken<List<StudentDegree>>() {}.getType();
            List<StudentDegree> studentDegrees = modelMapper.map(studentDegreesDTO, type);

            studentDegrees.forEach(studentDegree -> {
                Integer groupId = studentDegreesDTO.get(studentDegrees.indexOf(studentDegree)).getStudentGroupId();
                studentDegree.setStudentGroup(getStudentGroup(groupId));
            });

            studentDegreeService.update(studentDegrees);
            upStudent = studentService.findById(studentId);
        } catch (Exception exception) {
            return handleException(exception);
        }
        return ResponseEntity.ok(parseToStudentDTO(upStudent));
    }

    private StudentGroup getStudentGroup(Integer groupId) {
        return this.studentGroupService.getById(groupId);
    }

    @JsonView(StudentDegreeViews.Expel.class)
    @PostMapping("/degrees/expels")
    public ResponseEntity expelStudentDegree(@RequestBody List<StudentExpelDTO> studentExpelDTOs) {
        List<StudentExpelDTO> studentExpelDTOList;
        try {
            ModelMapper modelMapper = new ModelMapper();
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            Type type = new TypeToken<List<StudentExpel>>() {}.getType();
            List<StudentExpel> studentExpels = modelMapper.map(studentExpelDTOs, type);

            studentExpels.forEach(studentExpel -> {
                StudentExpelDTO studentExpelDTO = studentExpelDTOs.get(studentExpels.indexOf(studentExpel));
                studentExpel.setStudentDegree(studentDegreeService.getById(studentExpelDTO.getStudentDegreeId()));
                studentExpel.setReason(orderReasonService.getById(studentExpelDTO.getReasonId()));
            });

            List<StudentExpel> studentExpelList = studentDegreeService.expelStudents(studentExpels);
            studentExpelDTOList = new ModelMapper().map(studentExpelList, new TypeToken<List<StudentExpelDTO>>() {}.getType());
        } catch (Exception exception) {
            return handleException(exception);
        }
        
        Object[] ids = studentExpelDTOList.stream().map(StudentExpelDTO::getId).toArray();
        URI location = getNewResourceLocation(ids);
        return ResponseEntity.created(location).body(studentExpelDTOList);
    }
}