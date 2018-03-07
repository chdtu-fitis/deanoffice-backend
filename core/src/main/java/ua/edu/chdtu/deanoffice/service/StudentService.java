package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.repository.StudentRepository;
import ua.edu.chdtu.deanoffice.util.PersonUtil;

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
                PersonUtil.toCapitalizeCase(name),
                PersonUtil.toCapitalizeCase(surname),
                PersonUtil.toCapitalizeCase(patronimic)
        );
    }

    public Student getStudentById(Integer studentId) {
        return this.studentRepository.findOne(studentId);
    }

    public Student save(Student student) {
        student.setName(PersonUtil.toCapitalizeCase(student.getName()));
        student.setSurname(PersonUtil.toCapitalizeCase(student.getSurname()));
        student.setPatronimic(PersonUtil.toCapitalizeCase(student.getPatronimic()));
        return this.studentRepository.save(student);
    }
}
