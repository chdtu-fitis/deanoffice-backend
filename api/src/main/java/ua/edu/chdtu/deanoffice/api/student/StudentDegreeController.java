package ua.edu.chdtu.deanoffice.api.student;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.chdtu.deanoffice.api.general.ExceptionHandlerAdvice;
import ua.edu.chdtu.deanoffice.api.general.ExceptionToHttpCodeMapUtil;
import ua.edu.chdtu.deanoffice.api.general.mapper.Mapper;
import ua.edu.chdtu.deanoffice.api.group.dto.StudentDegreeFullNameDTO;
import ua.edu.chdtu.deanoffice.api.student.dto.PreviousDiplomaDTO;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentDTO;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentDegreeDTO;
import ua.edu.chdtu.deanoffice.api.student.dto.StudentView;
import ua.edu.chdtu.deanoffice.entity.ApplicationUser;
import ua.edu.chdtu.deanoffice.entity.EducationDocument;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.exception.NotFoundException;
import ua.edu.chdtu.deanoffice.exception.OperationCannotBePerformedException;
import ua.edu.chdtu.deanoffice.service.StudentDegreeService;
import ua.edu.chdtu.deanoffice.service.StudentGroupService;
import ua.edu.chdtu.deanoffice.service.StudentService;
import ua.edu.chdtu.deanoffice.service.security.FacultyAuthorizationService;
import ua.edu.chdtu.deanoffice.webstarter.security.CurrentUser;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static ua.edu.chdtu.deanoffice.api.general.Util.getNewResourceLocation;

@RestController
public class StudentDegreeController {
    private final StudentDegreeService studentDegreeService;
    private final StudentService studentService;
    private final StudentGroupService studentGroupService;
    private final FacultyAuthorizationService facultyAuthorizationService;

    @Autowired
    public StudentDegreeController(
            StudentDegreeService studentDegreeService,
            StudentService studentService,
            StudentGroupService studentGroupService,
            FacultyAuthorizationService facultyAuthorizationService) {
        this.studentDegreeService = studentDegreeService;
        this.studentService = studentService;
        this.studentGroupService = studentGroupService;
        this.facultyAuthorizationService = facultyAuthorizationService;
    }

    @JsonView(StudentView.Simple.class)
    @GetMapping("/students/degrees")
    public ResponseEntity getActiveStudentsDegree(@CurrentUser ApplicationUser user) {
        try {
            return ResponseEntity.ok(getActiveStudentDegrees(user.getFaculty().getId()));
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @JsonView(StudentView.Detail.class)
    @GetMapping("/students/degrees/more-detail")
    public ResponseEntity getActiveStudentsDegree_moreDetail(@CurrentUser ApplicationUser user) {
        try {
            return ResponseEntity.ok(getActiveStudentDegrees(user.getFaculty().getId()));
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private List<StudentDegreeDTO> getActiveStudentDegrees(int facultyId) {
        return Mapper.map(studentDegreeService.getAllByActive(true, facultyId), StudentDegreeDTO.class);
    }

    @JsonView(StudentView.Degree.class)
    @PostMapping("/students/degrees")
    public ResponseEntity createNewStudentDegree(
            @RequestBody StudentDegreeDTO newStudentDegree,
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
            return ResponseEntity.created(location).body(Mapper.map(studentDegree, StudentDegreeDTO.class));
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private Student createStudent(StudentDTO newStudentDTO) {
        Student newStudent = (Student) Mapper.strictMap(newStudentDTO, Student.class);
        if (newStudent.getId() != 0) {
            newStudent.setId(0);
        }
        return studentService.save(newStudent);
    }

    private StudentDegree createStudentDegree(StudentDegreeDTO newStudentDegreeDTO, Student student) {
        StudentDegree newStudentDegree = (StudentDegree) Mapper.strictMap(newStudentDegreeDTO, StudentDegree.class);
        newStudentDegree.setStudent(student);
        newStudentDegree.setStudentGroup(studentGroupService.getById(newStudentDegreeDTO.getStudentGroupId()));
        newStudentDegree.setSpecialization(newStudentDegree.getStudentGroup().getSpecialization());
        newStudentDegree.setActive(true);

        PreviousDiplomaDTO previousDiplomaDTO = getPreviousDiploma(newStudentDegree);
        newStudentDegree.setPreviousDiplomaType(previousDiplomaDTO.getType());
        boolean degreeIsNotBachelor = newStudentDegree.getSpecialization().getDegree().getId() != 1;
        if (degreeIsNotBachelor) {
            newStudentDegree.setPreviousDiplomaNumber(previousDiplomaDTO.getNumber());
            newStudentDegree.setPreviousDiplomaDate(previousDiplomaDTO.getDate());
        }
        return studentDegreeService.save(newStudentDegree);
    }

    private PreviousDiplomaDTO getPreviousDiploma(StudentDegree studentDegree) {
        EducationDocument educationDocument = getPreviousDiplomaType(studentDegree);
        StudentDegree firstStudentDegree = studentDegreeService.getFirst(studentDegree.getStudent().getId());
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
        return EducationDocument.getForecastedDiplomaTypeByDegree(studentDegree.getSpecialization().getDegree().getId());
    }

    @JsonView(StudentView.Degrees.class)
    @GetMapping("/students/{id}/degrees")
    public ResponseEntity getAllStudentsDegreeById(@PathVariable("id") Integer studentId) {
        try {
            Student student = studentService.findById(studentId);
            return ResponseEntity.ok(Mapper.map(student, StudentDTO.class));
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

//    @JsonView(StudentView.Degrees.class)
    @PutMapping("/students/{id}/degrees")
    public ResponseEntity updateStudentDegrees(
            @RequestBody List<StudentDegreeDTO> studentDegreesDTOs,
            @CurrentUser ApplicationUser user
    ) {
        try {
            validateStudentDegreesUpdates(studentDegreesDTOs);
            List<Integer> studentDegreeIds = studentDegreesDTOs.stream().map(StudentDegreeDTO::getId).collect(Collectors.toList());
            List<StudentDegree> studentDegrees = studentDegreeService.getActiveByIdsAndFaculty(studentDegreeIds, user.getFaculty().getId());

            for (StudentDegree sd : studentDegrees) {
                StudentDegreeDTO currSdDto = studentDegreesDTOs.stream().filter(sdDto -> sdDto.getId() == sd.getId()).findFirst().get();
                Mapper.mapStudentDegreeDtoToStudentDegreeSimpleFields(currSdDto, sd);
                if ((sd.getStudentGroup() == null && currSdDto.getStudentGroupId() != 0) ||
                        (sd.getStudentGroup() != null && sd.getStudentGroup().getId() != currSdDto.getStudentGroupId())) {
                    sd.setStudentGroup(getStudentGroup(currSdDto.getStudentGroupId()));
                    sd.setSpecialization(sd.getStudentGroup().getSpecialization());
                }
            }
            studentDegreeService.update(studentDegrees);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private void validateStudentDegreesUpdates(List<StudentDegreeDTO> studentDegreesDTOs) throws OperationCannotBePerformedException {
        if (studentDegreesDTOs == null || studentDegreesDTOs.size()== 0) {
            String exceptionMessage = "Список студентів не може бути порожнім. Зверніться до адміністратора або розробника системи";
            throw new OperationCannotBePerformedException(exceptionMessage);
        }
        if (isAnyStudentDegreeMissingId(studentDegreesDTOs)) {
            String exceptionMessage = "Отримано некоректну інформацію. Зверніться до адміністратора або розробника системи";
            throw new OperationCannotBePerformedException(exceptionMessage);
        }
        Set<Integer> distinctIds = studentDegreesDTOs.stream().map(sd -> sd.getId()).collect(Collectors.toSet());
        if (distinctIds.size() != studentDegreesDTOs.size()) {
            String exceptionMessage = "Не можна двічі зберегти одного і того ж студента. Зверніться до адміністратора або розробника системи";
            throw new OperationCannotBePerformedException(exceptionMessage);
        }
    }

    private boolean isAnyStudentDegreeMissingId(List<StudentDegreeDTO> studentDegreeDTOs) {
        return studentDegreeDTOs.stream().anyMatch((studentDegreeDTO) -> studentDegreeDTO.getId() == null);
    }

    private StudentGroup getStudentGroup(Integer groupId) {
        return this.studentGroupService.getById(groupId);
    }

    @GetMapping("/groups/{group_id}/students")
    public ResponseEntity getStudentsByGroupId(@PathVariable("group_id") Integer groupId) {
        try {
            List<StudentDegree> students = this.studentDegreeService.getAllByGroupId(groupId);
            return ResponseEntity.ok(Mapper.map(students, StudentDegreeFullNameDTO.class));
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @PostMapping("/group/{groupId}/add-students")
    public ResponseEntity assignStudentsToGroup(
            @PathVariable("groupId") Integer groupId,
            @RequestBody Integer[] studentDegreeIds,
            @CurrentUser ApplicationUser user
            ) {
        try {
            validateInputDataForAssignStudentsToGroup(studentDegreeIds);
            List<StudentDegree> studentDegrees = studentDegreeService.getByIds(Arrays.asList(studentDegreeIds));
            if (studentDegrees.isEmpty()) {
                String message = "За переданими даними жодного студента не було знайдено для призначення групи. " +
                        "Зверніться до адміністратора або розробника системи.";
                throw new NotFoundException(message);
            }
            StudentGroup studentGroup = studentGroupService.getById(groupId);
            if (Objects.isNull(studentGroup)) {
                String message = "Групу для призначення не вдалося знайти. Зверніться до адміністратора або розробника системи.";
                throw new NotFoundException(message);
            }
            facultyAuthorizationService.verifyAccessibilityOfGroupAndStudents(user, studentDegrees, studentGroup);
            studentDegreeService.assignStudentsToGroup(studentDegrees, studentGroup);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    @PostMapping("/students/record-book-numbers")
    public ResponseEntity assignRecordBookNumbersToStudents(
            @RequestBody Map<Integer, String> studentDegreeIdsAndRecordBooksNumbers,
            @CurrentUser ApplicationUser user) {
        try {
            validateInputDataForAssignRecordBookNumbersToStudents(studentDegreeIdsAndRecordBooksNumbers);
            List<StudentDegree> studentDegrees =
                    studentDegreeService.getByIds(new ArrayList<>(studentDegreeIdsAndRecordBooksNumbers.keySet()));
            facultyAuthorizationService.verifyAccessibilityOfStudentDegrees(user, studentDegrees);
            if (studentDegrees.isEmpty()) {
                String message = "За переданими даними жодного студента не було знайдено для призначення " +
                        "номеру залікової книжки. Зверніться до адміністратора або розробника системи.";
                throw new NotFoundException(message);
            }
            studentDegreeService.assignRecordBookNumbersToStudents(studentDegreeIdsAndRecordBooksNumbers);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            return handleException(exception);
        }
    }

    private void validateInputDataForAssignRecordBookNumbersToStudents(
            Map<Integer, String> studentDegreeToRecordNumber
    ) throws OperationCannotBePerformedException {
        if (studentDegreeToRecordNumber.size() == 0) {
            String message = "Для призначення номеру залікової книжки потрібно передати хоча б одного студента.";
            throw new OperationCannotBePerformedException(message);
        }
        if (studentDegreeToRecordNumber.values().stream().anyMatch(item -> Objects.isNull(item) || item.isEmpty())) {
            String message = "Номер залікової книжки не може бути порожнім.";
            throw new OperationCannotBePerformedException(message);
        }
        Set<String> recordBookNumberSet = new HashSet<>(studentDegreeToRecordNumber.values());
        if (recordBookNumberSet.size() != studentDegreeToRecordNumber.size()) {
            String message = "Номер залікової книжки не може бути однаковим у декількох студентів";
            throw new OperationCannotBePerformedException(message);
        }
    }

    private void validateInputDataForAssignStudentsToGroup(Integer[] studentDegreeIds) throws OperationCannotBePerformedException {
        if (studentDegreeIds.length == 0) {
            String message = "Для призначення групи потрібно передати хоча б одного студента.";
            throw new OperationCannotBePerformedException(message);
        }
    }

    private ResponseEntity handleException(Exception exception) {
        return ExceptionHandlerAdvice.handleException(exception, StudentDegreeController.class, ExceptionToHttpCodeMapUtil.map(exception));
    }
}
