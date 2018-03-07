package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.Degree;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.repository.DegreeRepository;
import ua.edu.chdtu.deanoffice.repository.StudentDegreeRepository;

import java.util.List;

@Service
public class StudentDegreeService {
    private final StudentDegreeRepository studentDegreeRepository;

    public StudentDegreeService(StudentDegreeRepository studentDegreeRepository) {
        this.studentDegreeRepository = studentDegreeRepository;
    }

    public StudentDegree getById(Integer id) {
        return studentDegreeRepository.getById(id);
    }

    //TODO cr: імена методів сервісу не повинні механічно повторювати імена методів репозиторію
    //Вони повинні формулюватись в термінах предметної області, бути зрозумілими (наскільки можливо) при читанні користувачеві
    //Цей метод міг би називатись getActiveStudents(), наприклад; наступний - getSetOfStudents() або щось подібне, getById() - getStudent()
    public List<StudentDegree> findAllByActiveId(boolean active) {
        return studentDegreeRepository.findAllByActiveForFacultyId(active, getCurrentFaculty());
    }

    public List<StudentDegree> findAllByStudentDegreeIds(Integer[] id) {
        return studentDegreeRepository.getAllByStudentDegreeIds(id);
    }

    public StudentDegree save(StudentDegree studentDegree) {
        return this.studentDegreeRepository.save(studentDegree);
    }

    //TODO cr: зовсім не місце для такого методу, він повинен бути в іншому сервісі і братись звідти
    private Integer getCurrentFaculty() {
        return 1;
    }
}
