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
        student.setSurname(stringToCapitalizeCase(student.getSurname()));
        student.setPatronimic(stringToCapitalizeCase(student.getPatronimic()));
        return this.studentRepository.save(student);
    }

    //TODO cr: це не метод даного класу, оскільки не використовує його полів, це класичний утилітний метод, static за сутністю,
    // його потрібно перенести в пакет util, там же можна подивитись приклади методів.
    // Він може використовуватись іншими сервісами також, що ще раз підтверджує потрібність перенести його в util
    // Крім цього, потрібно врахувати можливість подвійних прізвищ, подвійних імен. В нас була одна студентка, в якої прізвище складалось з двох окремих слів
    private String stringToCapitalizeCase(String string) {
        if (string.isEmpty()) {
            return "";
        }
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }
}
