package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.repository.StudentRepository;

import java.util.List;

import static ua.edu.chdtu.deanoffice.util.PersonUtil.toCapitalizedCase;

@Service
public class StudentService {

    private StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student findAllById(Integer id) {
        return studentRepository.getOne(id);
    }

    public List<Student> searchByFullName(String name, String surname, String patronimic) {
        return studentRepository.findAllByFullNameUkr(
                toCapitalizedCase(name),
                toCapitalizedCase(surname),
                toCapitalizedCase(patronimic)
        );
    }

    public Student getById(Integer studentId) {
        return this.studentRepository.findOne(studentId);
    }

    public Student create(Student student) {
        student.setName(toCapitalizedCase(student.getName()));
        student.setSurname(toCapitalizedCase(student.getSurname()));
        student.setPatronimic(toCapitalizedCase(student.getPatronimic()));
        student.setNameEng(toCapitalizedCase(student.getNameEng()));
        student.setSurnameEng(toCapitalizedCase(student.getSurnameEng()));
        student.setPatronimicEng(toCapitalizedCase(student.getPatronimicEng()));
        return this.studentRepository.save(student);
    }

    public Student update(Student student) {
        Student upStudent = studentRepository.findOne(student.getId());
        upStudent = updateNotNullFields(student, upStudent);
        return studentRepository.save(upStudent);
    }

    // TODO ЗАМЕНИТЬ НА ЧТО ТО АДЕКВАТНОЕ!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private Student updateNotNullFields(Student source, Student destination) {
        if (source.getName() != null) destination.setName(toCapitalizedCase(source.getName()));
        if (source.getSurname() != null) destination.setSurname(toCapitalizedCase(source.getSurname()));
        if (source.getPatronimic() != null) destination.setPatronimic(toCapitalizedCase(source.getPatronimic()));
        if (source.getNameEng() != null) destination.setNameEng(toCapitalizedCase(source.getNameEng()));
        if (source.getSurnameEng() != null) destination.setSurnameEng(toCapitalizedCase(source.getSurnameEng()));
        if (source.getSurnameEng() != null) destination.setPatronimicEng(toCapitalizedCase(source.getPatronimicEng()));
        if (source.getSex() != null) destination.setSex(source.getSex());
        if (source.getBirthDate() != null) destination.setBirthDate(source.getBirthDate());
        if (source.getRegistrationAddress() != null) destination.setRegistrationAddress(source.getRegistrationAddress());
        if (source.getActualAddress() != null) destination.setActualAddress(source.getActualAddress());
        if (source.getSchool() != null) destination.setSchool(source.getSchool());
        if (source.getStudentCardNumber() != null) destination.setStudentCardNumber(source.getStudentCardNumber());
        if (source.getTelephone() != null) destination.setTelephone(source.getTelephone());
        if (source.getEmail() != null) destination.setEmail(source.getEmail());
        if (source.getPrivilege() != null) destination.setPrivilege(source.getPrivilege());
        if (source.getFatherName() != null) destination.setFatherName(toCapitalizedCase(source.getFatherName()));
        if (source.getFatherPhone() != null) destination.setFatherPhone(source.getFatherPhone());
        if (source.getFatherInfo() != null) destination.setFatherInfo(source.getFatherInfo());
        if (source.getMotherName() != null) destination.setMotherName(toCapitalizedCase(source.getMotherName()));
        if (source.getMotherPhone() != null) destination.setMotherPhone(source.getMotherPhone());
        if (source.getMotherInfo() != null) destination.setMotherInfo(source.getMotherInfo());
        if (source.getNotes() != null) destination.setNotes(source.getNotes());

        return destination;
    }
}
