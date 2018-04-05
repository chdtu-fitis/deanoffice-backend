package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.StudentAcademicVacation;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.repository.StudentAcademicVacationRepository;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;

import java.util.List;

@Service
public class StudentAcademicVacationService {
    private final StudentAcademicVacationRepository studentAcademicVacationRepository;
    private final StudentDegreeRepository studentDegreeRepository;

    @Autowired
    public StudentAcademicVacationService(
            StudentAcademicVacationRepository studentAcademicVacationRepository,
            StudentDegreeRepository studentDegreeRepository
    ) {
        this.studentAcademicVacationRepository = studentAcademicVacationRepository;
        this.studentDegreeRepository = studentDegreeRepository;
    }

    public StudentAcademicVacation moveToAcademicVacation(StudentAcademicVacation studentAcademicVacation) {
        Integer id = studentAcademicVacation.getStudentDegree().getId();

        StudentDegree studentDegree = studentDegreeRepository.getOne(id);
        studentDegree.setActive(false);
        studentDegreeRepository.save(studentDegree);

        return studentAcademicVacationRepository.save(studentAcademicVacation);
    }

    public List<StudentAcademicVacation> getAll() {
        return studentAcademicVacationRepository.findAll();
    }
}
