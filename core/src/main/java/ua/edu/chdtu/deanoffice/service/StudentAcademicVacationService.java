package ua.edu.chdtu.deanoffice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.repository.StudentAcademicVacationRepository;

@Service
public class StudentAcademicVacationService {
    private final StudentAcademicVacationRepository studentAcademicVacationRepository;

    @Autowired
    public StudentAcademicVacationService(StudentAcademicVacationRepository studentAcademicVacationRepository) {
        this.studentAcademicVacationRepository = studentAcademicVacationRepository;
    }
}
