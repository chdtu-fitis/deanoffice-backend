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
        student.setName(stringToCapitalizeCase(student.getName()));
        student.setNameEng(stringToCapitalizeCase(student.getNameEng()));
        student.setSurname(stringToCapitalizeCase(student.getSurname()));
        student.setSurnameEng(stringToCapitalizeCase(student.getSurnameEng()));
        student.setPatronimic(stringToCapitalizeCase(student.getPatronimic()));
        student.setPatronimicEng(stringToCapitalizeCase(student.getPatronimicEng()));
        return this.studentRepository.save(student);
    }

    private String stringToCapitalizeCase(String string) {
        if (string.isEmpty()) {
            return "";
        }
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }
}
