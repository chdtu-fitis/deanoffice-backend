package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;
import ua.edu.chdtu.deanoffice.repository.StudentRepository;

import java.util.List;

import static ua.edu.chdtu.deanoffice.util.PersonUtil.toCapitalizedCase;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentDegreeRepository studentDegreeRepository;

    public StudentService(
            StudentRepository studentRepository,
            StudentDegreeRepository studentDegreeRepository
    ) {
        this.studentRepository = studentRepository;
        this.studentDegreeRepository = studentDegreeRepository;
    }

    public Student findById(Integer id) {
        return studentRepository.getOne(id);
    }

    public List<Student> searchByFullName(String name, String surname, String patronimic, int facultyId) {
        return studentDegreeRepository.findAllByFullNameUkr(
                toCapitalizedCase(name),
                toCapitalizedCase(surname),
                toCapitalizedCase(patronimic),
                facultyId
        );
    }

    public Student save(Student student) {
        student.setName(toCapitalizedCase(student.getName()));
        student.setSurname(toCapitalizedCase(student.getSurname()));
        student.setPatronimic(toCapitalizedCase(student.getPatronimic()));
        student.setNameEng(toCapitalizedCase(student.getNameEng()));
        student.setSurnameEng(toCapitalizedCase(student.getSurnameEng()));
        student.setPatronimicEng(toCapitalizedCase(student.getPatronimicEng()));
        return this.studentRepository.save(student);
    }

    public void addPhoto(byte[] photo, int studentId) {
        Student student = studentRepository.getOne(studentId);
        student.setPhoto(photo);
        studentRepository.save(student);
    }


}
