package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.repository.StudentRepository;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static ua.edu.chdtu.deanoffice.util.PersonUtil.toCapitalizedCase;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student findById(Integer id) {
        return studentRepository.findOne(id);
    }

    public List<Student> searchByFullName(String name, String surname, String patronymic, int facultyId) {
         return studentRepository.findAllByFullNameUkr(
                toCapitalizedCase(name),
                toCapitalizedCase(surname),
                toCapitalizedCase(patronymic),
                facultyId
        );
    }

    public Student searchByFullNameAndBirthDate(String name, String surname, String patronymic, Date birthDate ){
        List<Student> studentsList = studentRepository.findByFullNameUkrAndBirthDate(name, surname, patronymic, birthDate);
        if (studentsList.size()==0){
            return null;
        }
        else {
            return studentsList.get(0);
        }
    }

    public Student save(Student student) {
        student.setName(toCapitalizedCase(student.getName()));
        student.setSurname(toCapitalizedCase(student.getSurname()));
        student.setPatronimic(student.getPatronimic());
        student.setNameEng(toCapitalizedCase(student.getNameEng()));
        student.setSurnameEng(toCapitalizedCase(student.getSurnameEng()));
        student.setPatronimicEng(student.getPatronimicEng());
        return this.studentRepository.save(student);
    }

    public void addPhoto(String photoUrl, int studentId) {
        Student student = studentRepository.getOne(studentId);
        student.setPhotoUrl(photoUrl);
        studentRepository.save(student);
    }
}
