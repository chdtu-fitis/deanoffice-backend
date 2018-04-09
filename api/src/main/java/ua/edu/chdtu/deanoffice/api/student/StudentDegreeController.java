package ua.edu.chdtu.deanoffice.api.student;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.general.parser.Parser;
import ua.edu.chdtu.deanoffice.api.student.dto.PreviousDiplomaDTO;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentDTO;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentDegreeDTO;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentView;
import ua.edu.chdtu.deanoffice.entity.EducationDocument;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;
import ua.edu.chdtu.deanoffice.service.StudentService;

import java.net.URI;
import java.util.List;

import static ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice.handleException;
import static ua.edu.chdtu.deanoffice.api.general.Util.getNewResourceLocation;

@RestController
@RequestMapping("/students")
public class StudentDegreeController {
    private final StudentDegreeService studentDegreeService;
    private final StudentService studentService;
    private final StudentGroupService studentGroupService;

    @Autowired
    public StudentDegreeController(
            StudentDegreeService studentDegreeService,
            StudentService studentService,
            StudentGroupService studentGroupService
    ) {
        this.studentDegreeService = studentDegreeService;
        this.studentService = studentService;
        this.studentGroupService = studentGroupService;
    }

    @JsonView(StudentView.Simple.class)
    @GetMapping("/degrees")
    public ResponseEntity getActiveStudentsDegree(
            @RequestParam(value = "active", required = false, defaultValue = "true") boolean active
    ) {
        return ResponseEntity.ok(getActiveStudentDegrees(active));
    }

    @JsonView(StudentView.Detail.class)
    @GetMapping("/degrees/more-detail")
    public ResponseEntity getActiveStudentsDegree_moreDetail(
            @RequestParam(value = "active", required = false, defaultValue = "true") boolean active
    ) {
        return ResponseEntity.ok(getActiveStudentDegrees(active));
    }

    private List<StudentDegreeDTO> getActiveStudentDegrees(boolean active) {
        return Parser.parse(studentDegreeService.getAllByActive(active), StudentDegreeDTO.class);
    }

    @JsonView(StudentView.Degree.class)
    @PostMapping("/degrees")
    public ResponseEntity createNewStudentDegree(
            @RequestBody() StudentDegreeDTO newStudentDegree,
            @RequestParam(value = "new_student", defaultValue = "false", required = false) boolean newStudent
    ) {
        try {
            Student student;
            if (newStudent) {
                student = createStudent(newStudentDegree.getStudent());
            } else {
                student = studentService.findById(newStudentDegree.getStudent().getId());
            }
            StudentDegree studentDegree = createStudentDegree(newStudentDegree, student);

            URI location = getNewResourceLocation(studentDegree.getId());
            return ResponseEntity.created(location).body(Parser.parse(studentDegree, StudentDegreeDTO.class));
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private Student createStudent(StudentDTO newStudentDTO) {
        Student newStudent = (Student) Parser.strictParse(newStudentDTO, Student.class);
        if (newStudent.getId() != 0) {
            newStudent.setId(0);
        }
        return studentService.create(newStudent);
    }

    private StudentDegree createStudentDegree(StudentDegreeDTO newStudentDegreeDTO, Student student) {
        StudentDegree newStudentDegree = (StudentDegree) Parser.strictParse(newStudentDegreeDTO, StudentDegree.class);
        newStudentDegree.setStudent(student);
        newStudentDegree.setStudentGroup(studentGroupService.getById(newStudentDegreeDTO.getStudentGroupId()));
        newStudentDegree.setDegree(newStudentDegree.getStudentGroup().getSpecialization().getDegree());
        newStudentDegree.setActive(true);

        PreviousDiplomaDTO previousDiplomaDTO = getPreviousDiploma(newStudentDegree);
        newStudentDegree.setPreviousDiplomaType(previousDiplomaDTO.getType());
        boolean degreeIsNotBachelor = newStudentDegree.getDegree().getId() != 1;
        if (degreeIsNotBachelor) {
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

    @JsonView(StudentView.Degrees.class)
    @GetMapping("/{id}/degrees")
    public ResponseEntity getAllStudentsDegreeById(@PathVariable("id") Integer studentId) {
        Student student = studentService.findById(studentId);
        return ResponseEntity.ok(Parser.parse(student, StudentDTO.class));
    }

    @JsonView(StudentView.Degrees.class)
    @PutMapping("/{id}/degrees")
    public ResponseEntity updateStudentDegrees(
            @RequestBody List<StudentDegreeDTO> studentDegreesDTO,
            @PathVariable(value = "id") Integer studentId
    ) {
        if (checkNullId(studentDegreesDTO)) {
            return ResponseEntity.unprocessableEntity().body("[StudentDegree]: Id not must be null");
        }

        try {
            List<StudentDegree> studentDegrees = Parser.strictParse(studentDegreesDTO, StudentDegree.class);
            Student student = studentService.findById(studentId);

            studentDegrees.forEach(studentDegree -> {
                Integer groupId = studentDegreesDTO.get(studentDegrees.indexOf(studentDegree)).getStudentGroupId();
                studentDegree.setStudentGroup(getStudentGroup(groupId));
                studentDegree.setStudent(student);
            });

            studentDegreeService.update(studentDegrees);
            Student upStudent = studentService.findById(studentId);
            return ResponseEntity.ok(Parser.parse(upStudent, StudentDTO.class));
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private boolean checkNullId(List<StudentDegreeDTO> studentDegreeDTOs) {
        for (StudentDegreeDTO sd : studentDegreeDTOs) {
            if (sd.getId() == null) {
                return true;
            }
        }
        return false;
    }

    private StudentGroup getStudentGroup(Integer groupId) {
        return this.studentGroupService.getById(groupId);
    }
}
