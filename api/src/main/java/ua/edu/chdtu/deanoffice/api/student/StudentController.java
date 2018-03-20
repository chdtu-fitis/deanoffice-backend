package ua.edu.chdtu.deanoffice.api.student;

import com.fasterxml.jackson.annotation.JsonView;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.chdtu.deanoffice.api.student.dto.PreviousDiplomaDTO;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentDTO;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentDegreeDTO;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentDegreeViews;
import ua.edu.chdtu.deanoffice.entity.EducationDocument;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.service.DegreeService;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;
import ua.edu.chdtu.deanoffice.service.StudentService;

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

    @JsonView(StudentDegreeViews.Simple.class)
    @GetMapping("/degrees")
    public List<StudentDegreeDTO> getActiveStudentsDegree(
            @RequestParam(value = "active", required = false, defaultValue = "true") boolean active
    ) {
        return getActiveStudentDegree(active);
    }

    @JsonView(StudentDegreeViews.Detail.class)
    @GetMapping("/degrees/more-detail")
    public List<StudentDegreeDTO> getActiveStudentsDegree_moreDetail(
            @RequestParam(value = "active", required = false, defaultValue = "true") boolean active
    ) {
        return getActiveStudentDegree(active);
    }

    private List<StudentDegreeDTO> getActiveStudentDegree(boolean active) {
        return parseToStudentDegreeDTO(studentDegreeService.getAllByActive(active));
    }

    private List<StudentDegreeDTO> parseToStudentDegreeDTO(List<StudentDegree> studentDegreeList) {
        return new ModelMapper().map(studentDegreeList, new TypeToken<List<StudentDegreeDTO>>() {}.getType());
    }

    private List<StudentDTO> parseToStudentDTO(List<Student> studentList) {
        return new ModelMapper().map(studentList, new TypeToken<List<StudentDTO>>() {
        }.getType());
    }

    @JsonView(StudentDegreeViews.Search.class)
    @GetMapping("/search")
    public List<StudentDTO> searchStudentByFullName(
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

    private String getGroupNamesForStudent(Student student) {
        return student.getDegrees().stream()
                .map(studentDegree -> studentDegree.getStudentGroup().getName())
                .collect(Collectors.joining(", "));
    }

    @JsonView(StudentDegreeViews.Degree.class)
    @PostMapping("/degrees")
    public ResponseEntity<StudentDegreeDTO> createNewStudentDegree(
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
    public ResponseEntity<StudentDTO> getAllStudentsId(
            @PathVariable("id") Integer studentId
    ) {
        return ResponseEntity.ok(parseToStudentDTO(studentService.findById(studentId)));
    }

    private StudentDTO parseToStudentDTO(Student student) {
        return new ModelMapper().map(student, StudentDTO.class);
    }

    @PutMapping("/")
    public ResponseEntity<StudentDTO> updateStudent(@RequestBody Student student) {
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
    public ResponseEntity<StudentDTO> getAllStudentsDegreeById(
            @PathVariable("id") Integer studentId
    ) {
        return ResponseEntity.ok(parseToStudentDTO(studentService.findById(studentId)));
    }

    @JsonView(StudentDegreeViews.Degrees.class)
    @PutMapping("/{id}/degrees")
    public ResponseEntity<StudentDTO> updateStudentDegrees(
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
}