package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;
import ua.edu.chdtu.deanoffice.util.ObjectUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StudentDegreeService {
    private final StudentDegreeRepository studentDegreeRepository;
    private final CurrentYearService currentYearService;

    public StudentDegreeService(StudentDegreeRepository studentDegreeRepository, CurrentYearService currentYearService) {
        this.studentDegreeRepository = studentDegreeRepository;
        this.currentYearService = currentYearService;
    }

    public StudentDegree getById(Integer id) {
        return studentDegreeRepository.getById(id);
    }

    public List<StudentDegree> getAllByActive(boolean active, int facultyId) {
        return studentDegreeRepository.findAllByActive(active, facultyId);
    }

    public StudentDegree getFirst(Integer studentId) {
        List<StudentDegree> studentDegrees = this.studentDegreeRepository.findAllByStudentId(studentId);
        return (studentDegrees.isEmpty()) ? null : studentDegrees.get(0);
    }

    public List<StudentDegree> getAllByGroupId(Integer groupId) {
        return this.studentDegreeRepository.findStudentDegreeByStudentGroupIdAndActive(groupId, true);
    }

    public List<StudentDegree> getAllActiveByStudent(Integer studentId) {
        return this.studentDegreeRepository.findAllActiveByStudentId(studentId);
    }

    public String checkGraduateFieldValuesAvailability(StudentDegree studentDegree) {
        String message = "";
        message += ObjectUtil.isEmpty(studentDegree.getDiplomaNumber()) ? "Номер диплома" : "";
        message += ObjectUtil.isEmpty(studentDegree.getDiplomaDate()) ? "Дата диплома" : "";
        message += ObjectUtil.isEmpty(studentDegree.getPreviousDiplomaDate()) ? "Попередня дата диплома" : "";
        message += ObjectUtil.isEmpty(studentDegree.getPreviousDiplomaNumber()) ? "Попередній номер диплома" : "";
        message += ObjectUtil.isEmpty(studentDegree.getPreviousDiplomaIssuedBy()) ? "Попередній диплом виданий" : "";
        message += ObjectUtil.isEmpty(studentDegree.getAdmissionDate()) ? "Дата вступу" : "";
        message += ObjectUtil.isEmpty(studentDegree.getProtocolDate()) ? "Дата протокола" : "";
        message += ObjectUtil.isEmpty(studentDegree.getProtocolNumber()) ? "Номер протокола" : "";
        message += ObjectUtil.isEmpty(studentDegree.getSupplementDate()) ? "Дата додатка" : "";
        message += ObjectUtil.isEmpty(studentDegree.getSupplementNumber()) ? "Номер диплома" : "";
        message += ObjectUtil.isEmpty(studentDegree.getThesisName()) ? "Тема дипломної роботи" : "";
        message += ObjectUtil.isEmpty(studentDegree.getThesisNameEng()) ? "Тема дипломної роботи англійською" : "";
        message += ObjectUtil.isEmpty(studentDegree.getStudent().getSurnameEng()) ? "Прізвище англійською мовою" : "";
        message += ObjectUtil.isEmpty(studentDegree.getStudent().getNameEng()) ? "Ім'я англійською мовою" : "";
        message += ObjectUtil.isEmpty(studentDegree.getStudent().getPatronimicEng()) ? "По батькові англійською мовою" : "";
        return message;
    }

    public Map<StudentDegree, String> checkAllGraduates(int facultyId, int degreeId) {
        int year = currentYearService.getYear();
        List<StudentDegree> studentDegrees = studentDegreeRepository.findAllGraduates(year, facultyId, degreeId);
        return studentDegrees
                .stream()
                .filter(sd -> !checkGraduateFieldValuesAvailability(sd).equals(""))
                .collect(Collectors.toMap(sd -> sd,
                        sd -> checkGraduateFieldValuesAvailability(sd)));
    }

    public StudentDegree save(StudentDegree studentDegree) {
        return this.studentDegreeRepository.save(studentDegree);
    }

    public void update(List<StudentDegree> studentDegree) {
        studentDegreeRepository.save(studentDegree);
    }
}
