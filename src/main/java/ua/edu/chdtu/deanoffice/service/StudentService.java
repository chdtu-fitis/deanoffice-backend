package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentStatus;
import ua.edu.chdtu.deanoffice.repository.StudentRepository;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static ua.edu.chdtu.deanoffice.util.PersonUtil.toCapitalizedCase;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentAcademicVacationService studentAcademicVacationService;

    public StudentService(StudentRepository studentRepository, StudentAcademicVacationService studentAcademicVacationService) {
        this.studentRepository = studentRepository;
        this.studentAcademicVacationService = studentAcademicVacationService;
    }

    public Student findById(Integer id) {
        return studentRepository.findById(id).get();
    }

    public List<Student> searchByFullName(String name, String surname, String patronymic) {
         return studentRepository.findAllByFullNameUkr(
                toCapitalizedCase(name),
                toCapitalizedCase(surname),
                toCapitalizedCase(patronymic)
        );
    }

    public Integer[] searchStudentByFullNameAndSpecialityCode(String name, String surname,
                                                                  String patronymic, String code) {
        if (patronymic.equals("")) {
            return studentRepository.findAllByFullNameUkrAndSpecialityCode(
                    toCapitalizedCase(name),
                    toCapitalizedCase(surname),
                    toCapitalizedCase(code)
            );
        } else {
            return studentRepository.findAllByFullNameUkrAndSpecialityCode(
                    toCapitalizedCase(name),
                    toCapitalizedCase(surname),
                    toCapitalizedCase(patronymic),
                    toCapitalizedCase(code)
            );
        }
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

    public StudentStatus getStudentStatus(int studentId) {
        Student student = findById(studentId);

        if (student == null || student.getDegrees().size() == 0)
            return StudentStatus.NO;

        if (student.getDegrees().stream().anyMatch(studentDegree -> studentDegree.isActive()))
            return StudentStatus.ACTIVE;
        else if (studentAcademicVacationService.getActive(student.getDegrees().stream().map(StudentDegree::getId).collect(Collectors.toList())).size() > 0)
            return StudentStatus.ACADEMIC_VACATION;
        else
            return StudentStatus.NO;
    }

    public List<ShortStudentInfoBean> getAllActiveStudents(String facultyAbbr) {
        List<ShortStudentInfoBean> allActiveStudents;

        if (facultyAbbr == null) {
            allActiveStudents = studentRepository.getAllActiveStudents();
        }
        else {
            allActiveStudents = studentRepository.getAllActiveStudentsByFaculty(facultyAbbr);
        }

        return allActiveStudents;
    }
}
