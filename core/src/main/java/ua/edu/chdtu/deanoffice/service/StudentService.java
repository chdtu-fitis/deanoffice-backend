package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.repository.StudentGroupRepository;
import ua.edu.chdtu.deanoffice.repository.StudentRepository;

import java.util.List;

@Service
public class StudentService {

    private StudentRepository studentRepository;
    private StudentGroupRepository studentGroupRepository;

    public StudentService(StudentRepository studentRepository,
                          StudentGroupRepository studentGroupRepository) {
        this.studentRepository = studentRepository;
        this.studentGroupRepository = studentGroupRepository;
    }

    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    public List<Student> findAllByStudentIds(Integer[] id) {
        return studentRepository.getAllByStudentIds(id);
    }

    public List<Student> searchStudentByFullName(String name, String surname, String patronimic) {
        return studentRepository.findAllByFullNameUkr(
                stringToCapitalizeCase(name),
                stringToCapitalizeCase(surname),
                stringToCapitalizeCase(patronimic)
        );
    }

    public Student getStudentById(Integer studentId) {
        return this.studentRepository.findOne(studentId);
    }

    public Student save(Student student) {
        return this.studentRepository.save(student);
    }

    private String stringToCapitalizeCase(String string) {
        if (string.isEmpty()) {
            return "";
        }
        string = string.toLowerCase();
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }
}
