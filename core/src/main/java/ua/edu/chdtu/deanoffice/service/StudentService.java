package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.repository.StudentRepository;

import java.util.List;

@Service
public class StudentService {

    private StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    public Student get(Integer id) {
        return studentRepository.findOne(id);
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

    private String stringToCapitalizeCase(String string) {
        if (string.isEmpty()) {
            return "";
        }
        string = string.toLowerCase();
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

}
