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

    public List<Student> findAllByIds(Integer[] id) {
        return studentRepository.getAllByStudentIds(id);
    }

    public List<Student> searchByFullName(String name, String surname, String patronimic) {
        return studentRepository.findAllByFullNameUkr(
                PersonUtil.toCapitalizedCase(name),
                PersonUtil.toCapitalizedCase(surname),
                PersonUtil.toCapitalizedCase(patronimic)
        );
    }

    public Student getById(Integer studentId) {
        return this.studentRepository.findOne(studentId);
    }

    public Student save(Student student) {
        student.setName(PersonUtil.toCapitalizedCase(student.getName()));
        student.setSurname(PersonUtil.toCapitalizedCase(student.getSurname()));
        student.setPatronimic(PersonUtil.toCapitalizedCase(student.getPatronimic()));
        return this.studentRepository.save(student);
    }
}
