package ua.edu.chdtu.deanoffice.service;

import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.repository.FacultyRepository;

@Service
public class FacultyService {
    private final FacultyRepository facultyRepository;

    public FacultyService (FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public void checkStudentDegree(Integer studentDegreeId, Integer facultyId) throws Exception{
        Integer realFacultyId = facultyRepository.findIdByStudent(studentDegreeId);
        if (realFacultyId == null)
            throw new Exception("404");
        else if (realFacultyId == facultyId)
            return;
        else
            throw new Exception("403");
    }
}
